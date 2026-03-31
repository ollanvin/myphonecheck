package app.callcheck.mobile.feature.callintercept

import app.callcheck.mobile.core.model.CountrySearchConfig
import app.callcheck.mobile.core.model.SearchEngine
import app.callcheck.mobile.core.model.TimeoutPolicy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 2초 SLA 강제 타임아웃 엔포서.
 *
 * 자비스 기준:
 * "2초 안에 결과가 부족해도 '결과 없음'이 아니라
 *  '현재까지 발견된 결과'를 먼저 보여주고,
 *  뒤에서 확정값으로 갱신해야 합니다."
 *
 * 타임라인:
 *   0ms      — 전화번호 정규화 시작
 *   ~50ms    — 정규화 완료, 국가 라우터 시작
 *   ~150ms   — 라우터 완료, 1순위 검색 시작
 *   ~1200ms  — 1순위 타임아웃, 결과 있으면 수집 / 없으면 2순위 시작
 *   ~1500ms  — earlyDisplay: 현재까지 결과 UI에 1차 표시
 *   ~1800ms  — 2순위 타임아웃, 3순위 디렉토리 검색 시작
 *   2000ms   — hardDeadline: 무조건 최종 결과 강제 표시
 *
 * 설계 원칙:
 * - 3순위 동시 검색, 각 엔진별 독립 타임아웃
 * - earlyDisplayMs 시점에 중간 결과 콜백
 * - hardDeadlineMs 시점에 확보된 결과로 강제 완료
 * - 모든 검색은 독립 코루틴, 하나가 실패해도 나머지 계속
 * - 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class SearchTimeoutEnforcer @Inject constructor() {

    /**
     * 검색 결과 단일 항목.
     */
    data class SearchResult(
        /** 결과를 제공한 검색 엔진 */
        val engine: SearchEngine,
        /** 검색 결과 snippet */
        val snippet: String,
        /** 결과 URL */
        val url: String = "",
        /** 이 결과의 위험 점수 (0.0 ~ 1.0) */
        val riskScore: Float = 0f,
        /** 이 결과의 안전 점수 (0.0 ~ 1.0) */
        val safeScore: Float = 0f,
        /** 수집 시각 (시작으로부터의 경과 ms) */
        val collectedAtMs: Long = 0L,
    )

    /**
     * 검색 실행 상태.
     */
    enum class SearchPhase {
        /** 정규화 진행 중 (0 ~ 50ms) */
        NORMALIZING,
        /** 국가 라우팅 진행 중 (50 ~ 150ms) */
        ROUTING,
        /** 1순위 검색 진행 중 (150 ~ 1200ms) */
        PRIMARY_SEARCH,
        /** 2순위 검색 진행 중 (1200 ~ 1800ms) */
        SECONDARY_SEARCH,
        /** 3순위 디렉토리 검색 진행 중 */
        TERTIARY_SEARCH,
        /** 중간 결과 표시 완료 */
        EARLY_DISPLAY,
        /** 최종 결과 강제 표시 — 2초 SLA 완료 */
        HARD_DEADLINE_REACHED,
        /** 정상 완료 (2초 이내에 모든 검색 완료) */
        COMPLETED,
    }

    /**
     * 검색 진행 상태 스냅샷.
     *
     * earlyDisplay 및 hardDeadline 시점에 콜백으로 전달.
     */
    data class SearchSnapshot(
        /** 현재 검색 단계 */
        val phase: SearchPhase,
        /** 현재까지 수집된 결과 목록 */
        val results: List<SearchResult>,
        /** 검색 시작 후 경과 시간 (ms) */
        val elapsedMs: Long,
        /** 사용된 검색 엔진 목록 */
        val enginesUsed: List<SearchEngine>,
        /** 타임아웃으로 실패한 엔진 목록 */
        val enginesTimedOut: List<SearchEngine>,
        /** 최종 완료 여부 */
        val isFinal: Boolean,
    )

    /**
     * 검색 엔진 실행기 인터페이스.
     *
     * 실제 검색 로직은 이 인터페이스를 구현하여 주입.
     * SearchEvidenceProvider가 이를 구현.
     */
    fun interface EngineExecutor {
        /**
         * 지정 엔진으로 전화번호 검색을 수행.
         *
         * @param engine 사용할 검색 엔진
         * @param phoneNumber E.164 정규화된 전화번호
         * @param config 국가별 검색 설정
         * @return 검색 결과 목록 (빈 목록 = 결과 없음)
         */
        suspend fun execute(
            engine: SearchEngine,
            phoneNumber: String,
            config: CountrySearchConfig,
        ): List<SearchResult>
    }

    /**
     * 2초 SLA 강제 검색 실행.
     *
     * @param phoneNumber E.164 정규화된 전화번호
     * @param config 국가별 검색 설정
     * @param executor 검색 엔진 실행기
     * @param onEarlyDisplay 중간 결과 콜백 (earlyDisplayMs 시점)
     * @param onHardDeadline 최종 강제 결과 콜백 (hardDeadlineMs 시점)
     * @return 최종 SearchSnapshot
     */
    suspend fun executeWithSla(
        phoneNumber: String,
        config: CountrySearchConfig,
        executor: EngineExecutor,
        onEarlyDisplay: ((SearchSnapshot) -> Unit)? = null,
        onHardDeadline: ((SearchSnapshot) -> Unit)? = null,
    ): SearchSnapshot = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val timeout = config.timeoutPolicy
        val collectedResults = mutableListOf<SearchResult>()
        val enginesUsed = mutableListOf<SearchEngine>()
        val enginesTimedOut = mutableListOf<SearchEngine>()
        var currentPhase = SearchPhase.NORMALIZING
        var earlyDisplaySent = false

        val supervisorJob = SupervisorJob()
        val searchScope = CoroutineScope(coroutineContext + supervisorJob)

        // ── Hard Deadline 타이머 (2000ms) ──
        // 무조건 이 시점에 결과 강제 표시
        var hardDeadlineReached = false
        val deadlineJob = searchScope.launch {
            delay(timeout.hardDeadlineMs)
            hardDeadlineReached = true
            currentPhase = SearchPhase.HARD_DEADLINE_REACHED
            val snapshot = buildSnapshot(
                phase = currentPhase,
                results = collectedResults.toList(),
                startTime = startTime,
                enginesUsed = enginesUsed.toList(),
                enginesTimedOut = enginesTimedOut.toList(),
                isFinal = true,
            )
            onHardDeadline?.invoke(snapshot)
        }

        // ── Early Display 타이머 (1500ms) ──
        val earlyDisplayJob = searchScope.launch {
            delay(timeout.earlyDisplayMs)
            if (!hardDeadlineReached && collectedResults.isNotEmpty()) {
                earlyDisplaySent = true
                currentPhase = SearchPhase.EARLY_DISPLAY
                val snapshot = buildSnapshot(
                    phase = currentPhase,
                    results = collectedResults.toList(),
                    startTime = startTime,
                    enginesUsed = enginesUsed.toList(),
                    enginesTimedOut = enginesTimedOut.toList(),
                    isFinal = false,
                )
                onEarlyDisplay?.invoke(snapshot)
            }
        }

        try {
            // ── Phase 1: 1순위 검색 ──
            currentPhase = SearchPhase.PRIMARY_SEARCH
            val primaryEngine = config.primaryEngine
            if (primaryEngine != SearchEngine.NONE &&
                primaryEngine !in config.bannedEngines
            ) {
                enginesUsed.add(primaryEngine)
                val primaryResults = withTimeoutOrNull(timeout.primaryTimeoutMs) {
                    try {
                        executor.execute(primaryEngine, phoneNumber, config)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        emptyList()
                    }
                }
                if (primaryResults != null) {
                    synchronized(collectedResults) {
                        collectedResults.addAll(primaryResults.map {
                            it.copy(collectedAtMs = System.currentTimeMillis() - startTime)
                        })
                    }
                } else {
                    enginesTimedOut.add(primaryEngine)
                }
            }

            // 1순위에서 충분한 결과를 얻었으면 2순위 스킵 가능
            // 그러나 SLA 내에서 최대 정확도를 위해 2순위도 병행

            // ── Phase 2: 2순위 + 3순위 병행 검색 ──
            if (!hardDeadlineReached) {
                currentPhase = SearchPhase.SECONDARY_SEARCH

                val secondaryEngine = config.secondaryEngine
                val tertiaryEngine = config.tertiarySource

                // 2순위, 3순위 동시 실행 (각각 독립 타임아웃)
                val secondaryDeferred: kotlinx.coroutines.Deferred<List<SearchResult>?>? = if (
                    secondaryEngine != SearchEngine.NONE &&
                    secondaryEngine !in config.bannedEngines &&
                    !hardDeadlineReached
                ) {
                    enginesUsed.add(secondaryEngine)
                    searchScope.async {
                        withTimeoutOrNull(timeout.secondaryTimeoutMs) {
                            try {
                                executor.execute(secondaryEngine, phoneNumber, config)
                            } catch (e: CancellationException) {
                                throw e
                            } catch (_: Exception) {
                                emptyList<SearchResult>()
                            }
                        }
                    }
                } else null

                val tertiaryDeferred: kotlinx.coroutines.Deferred<List<SearchResult>?>? = if (
                    tertiaryEngine != SearchEngine.NONE &&
                    tertiaryEngine !in config.bannedEngines &&
                    !hardDeadlineReached
                ) {
                    enginesUsed.add(tertiaryEngine)
                    searchScope.async {
                        withTimeoutOrNull(timeout.tertiaryTimeoutMs) {
                            try {
                                executor.execute(tertiaryEngine, phoneNumber, config)
                            } catch (e: CancellationException) {
                                throw e
                            } catch (_: Exception) {
                                emptyList<SearchResult>()
                            }
                        }
                    }
                } else null

                // 2순위 결과 수집
                val secondaryResults = try {
                    secondaryDeferred?.await()
                } catch (_: Exception) {
                    null
                }
                if (secondaryResults != null) {
                    synchronized(collectedResults) {
                        collectedResults.addAll(secondaryResults.map {
                            it.copy(collectedAtMs = System.currentTimeMillis() - startTime)
                        })
                    }
                } else if (secondaryDeferred != null) {
                    enginesTimedOut.add(secondaryEngine)
                }

                // 3순위 결과 수집
                currentPhase = SearchPhase.TERTIARY_SEARCH
                val tertiaryResults = try {
                    tertiaryDeferred?.await()
                } catch (_: Exception) {
                    null
                }
                if (tertiaryResults != null) {
                    synchronized(collectedResults) {
                        collectedResults.addAll(tertiaryResults.map {
                            it.copy(collectedAtMs = System.currentTimeMillis() - startTime)
                        })
                    }
                } else if (tertiaryDeferred != null) {
                    enginesTimedOut.add(tertiaryEngine)
                }
            }

            // ── 검색 완료 (2초 이내) ──
            currentPhase = SearchPhase.COMPLETED
        } catch (e: CancellationException) {
            // 상위에서 취소된 경우
            throw e
        } catch (_: Exception) {
            // 예외 발생 시에도 현재까지 결과 반환
        } finally {
            // 타이머 정리
            deadlineJob.cancel()
            earlyDisplayJob.cancel()
            supervisorJob.cancel()
        }

        buildSnapshot(
            phase = if (hardDeadlineReached) SearchPhase.HARD_DEADLINE_REACHED else currentPhase,
            results = collectedResults.toList(),
            startTime = startTime,
            enginesUsed = enginesUsed.toList(),
            enginesTimedOut = enginesTimedOut.toList(),
            isFinal = true,
        )
    }

    /**
     * SLA 통과 여부 검증.
     *
     * @param snapshot 검색 결과 스냅샷
     * @param policy 타임아웃 정책
     * @return true = 2초 SLA 통과
     */
    fun verifySla(snapshot: SearchSnapshot, policy: TimeoutPolicy): SlaVerification {
        val withinDeadline = snapshot.elapsedMs <= policy.hardDeadlineMs
        val hasResults = snapshot.results.isNotEmpty()
        val allEnginesResponded = snapshot.enginesTimedOut.isEmpty()

        return SlaVerification(
            passed = withinDeadline,
            elapsedMs = snapshot.elapsedMs,
            hardDeadlineMs = policy.hardDeadlineMs,
            resultCount = snapshot.results.size,
            enginesUsed = snapshot.enginesUsed.size,
            enginesTimedOut = snapshot.enginesTimedOut.size,
            hasResults = hasResults,
            allEnginesResponded = allEnginesResponded,
        )
    }

    /**
     * SLA 검증 결과.
     */
    data class SlaVerification(
        /** SLA 통과 여부 (2초 이내 UI 표시) */
        val passed: Boolean,
        /** 실제 소요 시간 (ms) */
        val elapsedMs: Long,
        /** SLA 한계 시간 (ms) */
        val hardDeadlineMs: Long,
        /** 수집된 결과 수 */
        val resultCount: Int,
        /** 사용된 엔진 수 */
        val enginesUsed: Int,
        /** 타임아웃된 엔진 수 */
        val enginesTimedOut: Int,
        /** 결과 존재 여부 */
        val hasResults: Boolean,
        /** 모든 엔진 응답 여부 */
        val allEnginesResponded: Boolean,
    ) {
        fun toReportLine(): String = buildString {
            append(if (passed) "✅ PASS" else "❌ FAIL")
            append(" | ${elapsedMs}ms / ${hardDeadlineMs}ms")
            append(" | 결과: ${resultCount}건")
            append(" | 엔진: ${enginesUsed}개 사용")
            if (enginesTimedOut > 0) {
                append(" (${enginesTimedOut}개 타임아웃)")
            }
        }
    }

    /**
     * 전체 190개국 SLA 시뮬레이션 보고.
     *
     * 실제 네트워크 없이 타임아웃 정책만으로 통과 여부 판정.
     * 모든 국가의 hardDeadlineMs ≤ 2000ms 이면 PASS.
     */
    fun generateSlaComplianceReport(
        registry: GlobalSearchProviderRegistry,
    ): SlaComplianceReport {
        val results = mutableListOf<CountrySlaResult>()
        var passCount = 0
        var failCount = 0
        val failures = mutableListOf<String>()

        registry.allCountries().forEach { config ->
            val hardDeadline = config.timeoutPolicy.hardDeadlineMs
            val passed = hardDeadline <= GLOBAL_SLA_MS

            // 금지 엔진 위반 체크
            val bannedViolation = when {
                config.countryCode == "CN" && config.primaryEngine == SearchEngine.GOOGLE ->
                    "CN에서 Google 사용 금지 위반"
                config.countryCode == "CN" && config.secondaryEngine == SearchEngine.GOOGLE ->
                    "CN에서 Google 사용 금지 위반"
                else -> null
            }

            // 현지 엔진 누락 체크
            val localEngineViolation = when {
                config.countryCode == "KR" && config.primaryEngine != SearchEngine.NAVER ->
                    "KR 1순위가 Naver가 아님"
                config.countryCode == "JP" && config.primaryEngine != SearchEngine.YAHOO_JAPAN ->
                    "JP 1순위가 Yahoo Japan이 아님"
                config.countryCode == "RU" && config.primaryEngine != SearchEngine.YANDEX ->
                    "RU 1순위가 Yandex가 아님"
                config.countryCode == "CZ" && config.primaryEngine != SearchEngine.SEZNAM ->
                    "CZ 1순위가 Seznam이 아님"
                else -> null
            }

            val violation = bannedViolation ?: localEngineViolation
            val finalPassed = passed && violation == null

            if (finalPassed) passCount++ else {
                failCount++
                val reason = buildString {
                    if (!passed) append("SLA 초과(${hardDeadline}ms)")
                    if (violation != null) {
                        if (isNotEmpty()) append(" + ")
                        append(violation)
                    }
                }
                failures.add("[${config.countryCode}] $reason")
            }

            results.add(
                CountrySlaResult(
                    countryCode = config.countryCode,
                    tier = config.tier.name,
                    primaryEngine = config.primaryEngine.displayName,
                    hardDeadlineMs = hardDeadline,
                    passed = finalPassed,
                    failureReason = if (!finalPassed) (
                        if (!passed) "SLA 초과" else ""
                    ) + (violation ?: "") else null,
                )
            )
        }

        return SlaComplianceReport(
            totalCountries = results.size,
            passCount = passCount,
            failCount = failCount,
            results = results,
            failures = failures,
        )
    }

    data class CountrySlaResult(
        val countryCode: String,
        val tier: String,
        val primaryEngine: String,
        val hardDeadlineMs: Long,
        val passed: Boolean,
        val failureReason: String? = null,
    )

    data class SlaComplianceReport(
        val totalCountries: Int,
        val passCount: Int,
        val failCount: Int,
        val results: List<CountrySlaResult>,
        val failures: List<String>,
    ) {
        fun toFormattedReport(): String = buildString {
            appendLine("═══════════════════════════════════════════")
            appendLine("  190개국 Search Provider SLA 통과표")
            appendLine("═══════════════════════════════════════════")
            appendLine()
            appendLine("총 등록: ${totalCountries}개국")
            appendLine("SLA 통과: ${passCount}개국")
            appendLine("SLA 실패: ${failCount}개국")
            appendLine()

            if (failures.isNotEmpty()) {
                appendLine("── 실패 국가 ──")
                failures.forEach { appendLine("  $it") }
                appendLine()
            }

            appendLine("── 티어별 현황 ──")
            val byTier = results.groupBy { it.tier }
            for ((tier, countries) in byTier) {
                val tierPass = countries.count { it.passed }
                val tierTotal = countries.size
                appendLine("  $tier: ${tierPass}/${tierTotal} 통과")
            }
            appendLine()

            appendLine("── 국가별 상세 ──")
            results.sortedWith(compareBy({ it.tier }, { it.countryCode })).forEach { r ->
                val status = if (r.passed) "✅" else "❌"
                appendLine("  $status [${r.countryCode}] ${r.tier} | ${r.primaryEngine} | ${r.hardDeadlineMs}ms${r.failureReason?.let { " | $it" } ?: ""}")
            }
        }
    }

    // ── Internal ──

    private fun buildSnapshot(
        phase: SearchPhase,
        results: List<SearchResult>,
        startTime: Long,
        enginesUsed: List<SearchEngine>,
        enginesTimedOut: List<SearchEngine>,
        isFinal: Boolean,
    ) = SearchSnapshot(
        phase = phase,
        results = results,
        elapsedMs = System.currentTimeMillis() - startTime,
        enginesUsed = enginesUsed,
        enginesTimedOut = enginesTimedOut,
        isFinal = isFinal,
    )

    companion object {
        /** 글로벌 SLA 한계 (ms) */
        const val GLOBAL_SLA_MS = 2000L
    }
}
