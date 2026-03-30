package app.callcheck.mobile.feature.callintercept

import android.util.Log
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import app.callcheck.mobile.core.model.BehaviorPatternSignal
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.PreJudgeResult
import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.data.localcache.repository.PreJudgeCacheRepository
import app.callcheck.mobile.data.search.CachedEntry
import app.callcheck.mobile.data.search.SearchResultCachePolicy
import app.callcheck.mobile.feature.decisionengine.DecisionEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

private const val TAG = "CallInterceptRepo"
private const val DEVICE_EVIDENCE_TIMEOUT_MS = 1000L
private const val SEARCH_TIMEOUT_MS = 3000L

/**
 * 3-Tier 인터셉트 파이프라인.
 *
 * Tier 0: PreJudge 영속 캐시 — Room DB lookup, 0ms, 엔진 실행 없음
 * Tier 1: 인메모리 캐시 — ConcurrentHashMap, TTL 1h, 네트워크 0
 * Tier 2: 풀 파이프라인 — Device + Search + LocalLearning + BehaviorPattern
 *
 * "전화가 울리기 전에 판단" — Tier 0이 핵심.
 */
class CallInterceptRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deviceEvidenceProvider: DeviceEvidenceProvider,
    private val searchEvidenceProvider: SearchEvidenceProvider,
    private val localLearningProvider: LocalLearningProvider,
    private val preJudgeCacheRepository: PreJudgeCacheRepository,
    private val decisionEngine: DecisionEngine,
) : CallInterceptRepository {

    /** Tier 1: 인메모리 캐시 (TTL 1h, 최대 50건) */
    private val decisionCache = ConcurrentHashMap<String, CachedEntry<DecisionResult>>()

    /** 반복 수신 추적: 번호별 최근 수신 시각 (최대 10개) */
    private val recentCallLog = ConcurrentHashMap<String, MutableList<Long>>()

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
        val pipelineStartMs = System.currentTimeMillis()

        // ══════════════════════════════════════════════
        // Tier 0: 0ms PreJudge — Room 영속 캐시 lookup
        // 전화 울리기 전 판단. 엔진 실행 없음.
        // ══════════════════════════════════════════════
        try {
            val preJudge = preJudgeCacheRepository.lookup(normalizedNumber)
            if (preJudge != null && preJudge.isUsable()) {
                Log.i(TAG, "Tier0 HIT: $normalizedNumber (conf=${preJudge.effectiveConfidence()}, hits=${preJudge.hitCount})")
                trackRecentCall(normalizedNumber, pipelineStartMs)
                return DecisionResult(
                    riskLevel = decisionEngine.riskLevelFromScore(preJudge.riskScore),
                    category = preJudge.category,
                    action = preJudge.action,
                    confidence = preJudge.effectiveConfidence(),
                    summary = preJudge.summary,
                    reasons = emptyList(),
                    deviceEvidence = null,
                    searchEvidence = null,
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Tier0 lookup error (non-fatal): ${e.message}")
        }

        // ══════════════════════════════════════════════
        // Tier 1: 인메모리 캐시 (TTL 1시간)
        // ══════════════════════════════════════════════
        val cached = decisionCache[normalizedNumber]
        if (cached != null && cached.isValid(SearchResultCachePolicy.DECISION_CACHE_TTL_MS)) {
            Log.i(TAG, "Tier1 HIT: $normalizedNumber (age=${pipelineStartMs - cached.cachedAtMs}ms)")
            trackRecentCall(normalizedNumber, pipelineStartMs)
            return cached.data
        }

        // ══════════════════════════════════════════════
        // Tier 2: 풀 파이프라인
        // Device + Search + LocalLearning + BehaviorPattern
        // ══════════════════════════════════════════════
        return coroutineScope {
            try {
                Log.d(TAG, "Tier2 start: $normalizedNumber (country=$deviceCountryCode)")

                val online = isNetworkAvailable()
                Log.d(TAG, "Network: ${if (online) "ONLINE" else "OFFLINE"}")

                // Parallel: device + local learning (항상) + search (온라인만)
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

                // BehaviorPattern: 시간대 + 반복 + VoIP (동기, <1ms)
                val behaviorSignal = buildBehaviorSignal(normalizedNumber, deviceCountryCode)

                Log.d(TAG, "Evidence: device=${deviceEvidence != null}, search=${searchEvidence != null}, local=${localLearning != null}, behavior=${behaviorSignal.recentHourCallCount}calls/1h, offline=${!online}")

                // Decision engine — 4축 판단, < 50ms
                val result = decisionEngine.evaluate(
                    deviceEvidence = deviceEvidence,
                    searchEvidence = searchEvidence,
                    localLearning = localLearning,
                    behaviorPattern = behaviorSignal,
                )

                Log.d(TAG, "Decision: ${result.category} / ${result.riskLevel} / ${result.action}")

                // Tier 1 인메모리 캐시 저장
                cacheResult(normalizedNumber, result)

                // Tier 0 영속 캐시 저장 (판단 반환 안 막음)
                try {
                    preJudgeCacheRepository.store(normalizedNumber, result)
                    Log.d(TAG, "Tier0 STORE: $normalizedNumber")
                } catch (e: Exception) {
                    Log.w(TAG, "Tier0 store error (non-fatal): ${e.message}")
                }

                // 반복 수신 추적
                trackRecentCall(normalizedNumber, pipelineStartMs)

                result

            } catch (e: Exception) {
                Log.e(TAG, "Pipeline error", e)
                DecisionResult.fallback()
            }
        }
    }

    // ── 반복 수신 추적 ──

    private fun trackRecentCall(normalizedNumber: String, timestampMs: Long) {
        val timestamps = recentCallLog.getOrPut(normalizedNumber) { mutableListOf() }
        timestamps.add(timestampMs)
        while (timestamps.size > 10) timestamps.removeAt(0)
    }

    // ── BehaviorPatternSignal 생성 ──

    private fun buildBehaviorSignal(
        normalizedNumber: String,
        deviceCountryCode: String?,
    ): BehaviorPatternSignal {
        val now = System.currentTimeMillis()
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timestamps = recentCallLog[normalizedNumber] ?: emptyList()
        val oneHourAgo = now - 3_600_000L
        val oneDayAgo = now - 86_400_000L

        return BehaviorPatternSignal(
            currentHour = currentHour,
            recentCallTimestamps = timestamps,
            recentHourCallCount = timestamps.count { it > oneHourAgo },
            recent24hCallCount = timestamps.count { it > oneDayAgo },
            isVoipCall = false, // TODO: TelephonyManager 기반 VoIP 감지
            isInternationalCall = isInternationalNumber(normalizedNumber, deviceCountryCode),
            isRoaming = false, // TODO: TelephonyManager.isNetworkRoaming()
        )
    }

    private fun isInternationalNumber(normalizedNumber: String, deviceCountryCode: String?): Boolean {
        if (deviceCountryCode == null || !normalizedNumber.startsWith("+")) return false
        val numberCountry = normalizedNumber.removePrefix("+")
        val dialCode = countryToDialCode(deviceCountryCode) ?: return false
        return !numberCountry.startsWith(dialCode)
    }

    private fun countryToDialCode(countryCode: String): String? = when (countryCode.uppercase()) {
        "KR" -> "82"; "US", "CA" -> "1"; "JP" -> "81"; "CN" -> "86"
        "GB" -> "44"; "DE" -> "49"; "FR" -> "33"; "AU" -> "61"
        "IN" -> "91"; "BR" -> "55"; "TW" -> "886"; "HK" -> "852"
        "SG" -> "65"; "TH" -> "66"; "VN" -> "84"; "PH" -> "63"
        "ID" -> "62"; "MY" -> "60"; "MX" -> "52"; "RU" -> "7"
        else -> null
    }

    // ── Tier 1 인메모리 캐시 관리 ──

    private fun cacheResult(normalizedNumber: String, result: DecisionResult) {
        if (decisionCache.size >= SearchResultCachePolicy.MEMORY_CACHE_MAX_ENTRIES) {
            evictExpiredEntries()
        }
        if (decisionCache.size >= SearchResultCachePolicy.MEMORY_CACHE_MAX_ENTRIES) {
            evictOldestEntry()
        }
        decisionCache[normalizedNumber] = CachedEntry(data = result, phoneNumber = normalizedNumber)
        Log.d(TAG, "Tier1 STORE: $normalizedNumber (size=${decisionCache.size})")
    }

    private fun evictExpiredEntries() {
        val now = System.currentTimeMillis()
        val expired = decisionCache.entries.filter {
            !it.value.isValid(SearchResultCachePolicy.DECISION_CACHE_TTL_MS, now)
        }
        expired.forEach { decisionCache.remove(it.key) }
        if (expired.isNotEmpty()) Log.d(TAG, "Tier1 EVICT expired: ${expired.size}")
    }

    private fun evictOldestEntry() {
        val oldest = decisionCache.entries.minByOrNull { it.value.cachedAtMs }
        if (oldest != null) {
            decisionCache.remove(oldest.key)
            Log.d(TAG, "Tier1 EVICT LRU: ${oldest.key}")
        }
    }
}
