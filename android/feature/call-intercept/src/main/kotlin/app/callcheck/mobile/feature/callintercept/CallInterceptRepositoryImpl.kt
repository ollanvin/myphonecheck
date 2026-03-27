package app.callcheck.mobile.feature.callintercept

import android.util.Log
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.data.search.CachedEntry
import app.callcheck.mobile.data.search.SearchResultCachePolicy
import app.callcheck.mobile.feature.decisionengine.DecisionEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

private const val TAG = "CallInterceptRepo"
private const val DEVICE_EVIDENCE_TIMEOUT_MS = 1000L
private const val SEARCH_TIMEOUT_MS = 3000L

/**
 * Production implementation of the call intercept pipeline.
 *
 * Gathers device evidence and search evidence in parallel,
 * then feeds both to DecisionEngine for final scoring.
 *
 * 성능 최적화:
 * - Decision 캐시: 동일 번호 반복 수신 시 TTL(1시간) 이내 캐시 반환
 * - 네트워크 호출 0, CPU 0 — 배터리 절약 핵심
 * - LRU 방식: 캐시 크기 50 초과 시 가장 오래된 항목 제거
 */
class CallInterceptRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deviceEvidenceProvider: DeviceEvidenceProvider,
    private val searchEvidenceProvider: SearchEvidenceProvider,
    private val localLearningProvider: LocalLearningProvider,
    private val decisionEngine: DecisionEngine,
) : CallInterceptRepository {

    /**
     * 인메모리 Decision 캐시.
     * Key: normalizedNumber (E.164)
     * Value: CachedEntry<DecisionResult>
     *
     * TTL: DECISION_CACHE_TTL_MS (1시간)
     * 용량: MEMORY_CACHE_MAX_ENTRIES (50)
     *
     * 앱 종료 시 자동 소멸 — 프라이버시 보호.
     */
    private val decisionCache = ConcurrentHashMap<String, CachedEntry<DecisionResult>>()

    /**
     * 네트워크 연결 상태 확인.
     *
     * Android ConnectivityManager API 사용 (API 23+).
     * 오프라인 시 search 단계를 건너뛰고 device + localLearning만으로 판단.
     * 이 함수 자체는 < 1ms (시스템 콜 1회).
     */
    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    override suspend fun processIncomingCall(
        normalizedNumber: String,
        deviceCountryCode: String?,
    ): DecisionResult {
        // ── 캐시 히트 확인 ──
        val cached = decisionCache[normalizedNumber]
        if (cached != null && cached.isValid(SearchResultCachePolicy.DECISION_CACHE_TTL_MS)) {
            Log.i(TAG, "Cache HIT: $normalizedNumber (age=${System.currentTimeMillis() - cached.cachedAtMs}ms)")
            return cached.data
        }

        return coroutineScope {
            try {
                Log.d(TAG, "Pipeline start: $normalizedNumber (country=$deviceCountryCode)")

                val online = isNetworkAvailable()
                Log.d(TAG, "Network: ${if (online) "ONLINE" else "OFFLINE"}")

                // Parallel: device evidence + local learning (항상 실행)
                // + search enrichment (온라인일 때만)
                val deviceJob = async {
                    withTimeoutOrNull(DEVICE_EVIDENCE_TIMEOUT_MS) {
                        try {
                            deviceEvidenceProvider.gather(normalizedNumber)
                        } catch (e: Exception) {
                            Log.e(TAG, "Device evidence error", e)
                            null
                        }
                    }
                }

                // 오프라인 fallback: search 생략 → 네트워크 호출 0, <20ms 판단
                val searchJob = if (online) {
                    async {
                        withTimeoutOrNull(SEARCH_TIMEOUT_MS) {
                            try {
                                searchEvidenceProvider.gather(normalizedNumber, deviceCountryCode)
                            } catch (e: Exception) {
                                Log.w(TAG, "Search evidence error", e)
                                null
                            }
                        }
                    }
                } else {
                    null
                }

                // Local learning: Room 조회 (<5ms, 인덱스 기반)
                val localLearningJob = async {
                    try {
                        localLearningProvider.getSignal(normalizedNumber)
                    } catch (e: Exception) {
                        Log.w(TAG, "Local learning error (non-fatal)", e)
                        null
                    }
                }

                val deviceEvidence = deviceJob.await()
                val searchEvidence = searchJob?.await()
                val localLearning = localLearningJob.await()

                Log.d(TAG, "Evidence gathered - device: ${deviceEvidence != null}, search: ${searchEvidence != null}, local: ${localLearning != null}, offline: ${!online}")

                // Decision engine — synchronous, < 50ms
                val result = decisionEngine.evaluate(
                    deviceEvidence = deviceEvidence,
                    searchEvidence = searchEvidence,
                    localLearning = localLearning,
                )

                Log.d(TAG, "Decision: ${result.category} / ${result.riskLevel} / ${result.action}")

                // ── 캐시 저장 ──
                cacheResult(normalizedNumber, result)

                result

            } catch (e: Exception) {
                Log.e(TAG, "Pipeline error", e)
                DecisionResult.fallback()
            }
        }
    }

    /**
     * 결과를 캐시에 저장. LRU 방식으로 용량 관리.
     */
    private fun cacheResult(normalizedNumber: String, result: DecisionResult) {
        // 용량 초과 시 만료된 항목 먼저 제거, 그래도 넘치면 가장 오래된 항목 제거
        if (decisionCache.size >= SearchResultCachePolicy.MEMORY_CACHE_MAX_ENTRIES) {
            evictExpiredEntries()
        }
        if (decisionCache.size >= SearchResultCachePolicy.MEMORY_CACHE_MAX_ENTRIES) {
            evictOldestEntry()
        }

        decisionCache[normalizedNumber] = CachedEntry(
            data = result,
            phoneNumber = normalizedNumber,
        )
        Log.d(TAG, "Cache STORE: $normalizedNumber (size=${decisionCache.size})")
    }

    private fun evictExpiredEntries() {
        val now = System.currentTimeMillis()
        val expired = decisionCache.entries.filter {
            !it.value.isValid(SearchResultCachePolicy.DECISION_CACHE_TTL_MS, now)
        }
        expired.forEach { decisionCache.remove(it.key) }
        if (expired.isNotEmpty()) {
            Log.d(TAG, "Cache EVICT expired: ${expired.size} entries")
        }
    }

    private fun evictOldestEntry() {
        val oldest = decisionCache.entries.minByOrNull { it.value.cachedAtMs }
        if (oldest != null) {
            decisionCache.remove(oldest.key)
            Log.d(TAG, "Cache EVICT LRU: ${oldest.key}")
        }
    }
}
