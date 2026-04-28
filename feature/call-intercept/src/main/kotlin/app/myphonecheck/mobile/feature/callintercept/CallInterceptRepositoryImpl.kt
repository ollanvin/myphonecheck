package app.myphonecheck.mobile.feature.callintercept

import android.util.Log
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.core.model.BehaviorPatternSignal
import app.myphonecheck.mobile.core.model.ConclusionCategory
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.IdentifierChannel
import app.myphonecheck.mobile.core.model.InterceptRoute
import app.myphonecheck.mobile.core.model.IdentifierAnalysisInput
import app.myphonecheck.mobile.core.model.PhaseMeta
import app.myphonecheck.mobile.core.model.PhaseResult
import app.myphonecheck.mobile.core.model.PhaseSource
import app.myphonecheck.mobile.core.model.PreJudgeResult
import app.myphonecheck.mobile.core.model.RiskLevel
import app.myphonecheck.mobile.core.model.TwoPhaseDecision
import app.myphonecheck.mobile.core.model.UserCallAction
import app.myphonecheck.mobile.data.localcache.repository.PreJudgeCacheRepository
import app.myphonecheck.mobile.data.search.CachedEntry
import app.myphonecheck.mobile.data.search.SearchResultCachePolicy
import app.myphonecheck.mobile.feature.decisionengine.DecisionEngine
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
 * Stage 9: 완전체 인터셉트 파이프라인.
 *
 * 4대 신규 모듈 통합:
 * 1. InterceptPriorityRouter — 분기 우선순위 (SKIP/INSTANT/LIGHT/FULL)
 * 2. CountryInterceptPolicyProvider — 국가별 인터셉트 정책
 * 3. 2-Phase Scoring — Phase 1 즉시 + Phase 2 확정
 * 4. InterceptOutcomeLearner — 사용자 행동 기반 학습 루프
 *
 * 파이프라인 흐름:
 * ┌─────────────────────────────────────────────────────────────┐
 * │ 수신 → Router(분기) → Phase1(즉시) → Phase2(풀) → 학습루프  │
 * └─────────────────────────────────────────────────────────────┘
 */
class CallInterceptRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deviceEvidenceProvider: DeviceEvidenceProvider,
    private val searchEvidenceProvider: SearchEvidenceProvider,
    private val localLearningProvider: LocalLearningProvider,
    private val preJudgeCacheRepository: PreJudgeCacheRepository,
    private val decisionEngine: DecisionEngine,
    private val priorityRouter: InterceptPriorityRouter,
    private val countryPolicyProvider: CountryInterceptPolicyProvider,
    private val performanceTracker: InterceptPerformanceTracker,
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

    // ══════════════════════════════════════════
    // 기존 호환: DecisionResult 반환
    // ══════════════════════════════════════════

    override suspend fun analyzeIdentifier(
        input: IdentifierAnalysisInput,
    ): DecisionResult {
        return analyzeIdentifierTwoPhase(input).finalResult()
    }

    // ══════════════════════════════════════════
    // Stage 9: 2-Phase 파이프라인
    // ══════════════════════════════════════════

    override suspend fun analyzeIdentifierTwoPhase(
        input: IdentifierAnalysisInput,
    ): TwoPhaseDecision {
        val normalizedNumber = input.normalizedNumber
        val deviceCountryCode = input.deviceCountryCode
        val pipelineStartMs = System.currentTimeMillis()
        val countryCode = deviceCountryCode ?: "ZZ"

        // ── Step 1: PreJudge 캐시 lookup ──
        val preJudge = try {
            preJudgeCacheRepository.lookup(normalizedNumber)
        } catch (e: Exception) {
            Log.w(TAG, "PreJudge lookup error: ${e.message}")
            null
        }

        // ── Step 2: 로컬 학습 데이터에서 사용자 행동 이력 조회 ──
        val localLearning = try {
            localLearningProvider.getSignal(normalizedNumber)
        } catch (e: Exception) {
            null
        }

        // ── Step 3: Priority Router 분기 ──
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val isInternational = isInternationalNumber(normalizedNumber, countryCode)
        val recentCount = if (input.channel == IdentifierChannel.CALL) {
            recentCallLog[normalizedNumber]?.count {
                it > System.currentTimeMillis() - 3_600_000L
            } ?: 0
        } else {
            0
        }

        val route = priorityRouter.route(
            normalizedNumber = normalizedNumber,
            preJudge = preJudge,
            isSavedContact = input.isSavedContact,
            isInternational = isInternational,
            isVoip = false, // TODO: TelephonyManager 기반
            currentHour = currentHour,
            recentCallCount = recentCount,
            lastUserAction = localLearning?.lastAction,
            totalAnsweredCount = localLearning?.answeredCount ?: 0,
            countryRiskElevated = countryPolicyProvider.isElevatedRiskCountry(countryCode),
        )

        Log.i(TAG, "Route: ${route.name} for $normalizedNumber (country=$countryCode)")

        // ── Step 4: Route별 파이프라인 실행 ──
        val networkOn = isNetworkAvailable()
        val countryBoost = countryPolicyProvider.getRiskBoost(normalizedNumber, countryCode)

        val decision = when (route) {
            InterceptRoute.SKIP -> {
                buildSkipDecision(pipelineStartMs, route)
            }
            InterceptRoute.INSTANT -> {
                buildInstantDecision(input, preJudge, pipelineStartMs, route)
            }
            InterceptRoute.LIGHT -> {
                buildLightDecision(input, countryCode, preJudge, pipelineStartMs, route)
            }
            InterceptRoute.FULL -> {
                buildFullDecision(input, countryCode, preJudge, pipelineStartMs, route)
            }
        }

        // ── Step 5: 성능 계측 기록 ──
        performanceTracker.record(
            decision = decision,
            numberHash = normalizedNumber.hashCode().toString(),
            countryRiskBoost = countryBoost,
            networkAvailable = networkOn,
        )

        return decision
    }

    // ══════════════════════════════════════════
    // INSTANT: Phase 1만 (0~5ms)
    // ══════════════════════════════════════════

    private fun buildInstantDecision(
        input: IdentifierAnalysisInput,
        preJudge: PreJudgeResult?,
        startMs: Long,
        route: InterceptRoute,
    ): TwoPhaseDecision {
        val normalizedNumber = input.normalizedNumber
        val now = System.currentTimeMillis()
        if (input.channel == IdentifierChannel.CALL) {
            trackRecentCall(normalizedNumber, now)
        }

        val phase1 = if (preJudge != null && preJudge.isUsable()) {
            PhaseResult(
                action = preJudge.action,
                riskScore = preJudge.riskScore,
                category = preJudge.category,
                confidence = preJudge.effectiveConfidence(),
                summary = preJudge.summary,
                riskLevel = decisionEngine.riskLevelFromScore(preJudge.riskScore),
                source = PhaseSource.PRE_JUDGE_CACHE,
                completedAtMs = now,
            )
        } else {
            // 저장 연락처 등 PreJudge 없는 INSTANT → 안전 기본값
            PhaseResult(
                action = ActionRecommendation.ANSWER,
                riskScore = 0f,
                category = ConclusionCategory.KNOWN_CONTACT,
                confidence = 0.90f,
                summary = "저장된 연락처",
                riskLevel = RiskLevel.LOW,
                source = PhaseSource.PRE_JUDGE_CACHE,
                completedAtMs = now,
            )
        }

        Log.i(TAG, "INSTANT: ${phase1.action} (${now - startMs}ms)")

        return TwoPhaseDecision(
            phase1 = phase1,
            phase2 = null,
            phaseMeta = PhaseMeta(
                pipelineStartMs = startMs,
                phase1LatencyMs = now - startMs,
                route = route,
            ),
        )
    }

    // ══════════════════════════════════════════
    // LIGHT: Phase 1 (캐시) + Phase 2 (Device only, ~200ms)
    // ══════════════════════════════════════════

    private suspend fun buildLightDecision(
        input: IdentifierAnalysisInput,
        countryCode: String,
        preJudge: PreJudgeResult?,
        startMs: Long,
        route: InterceptRoute,
    ): TwoPhaseDecision {
        val normalizedNumber = input.normalizedNumber
        val now = System.currentTimeMillis()
        if (input.channel == IdentifierChannel.CALL) {
            trackRecentCall(normalizedNumber, now)
        }

        // Phase 1: 캐시 기반 즉시 판단 (또는 국가 정책 기반 기본값)
        val phase1 = buildPhase1FromCacheOrPolicy(normalizedNumber, preJudge, countryCode, now)
        val phase1LatencyMs = System.currentTimeMillis() - startMs

        // Phase 2: Device evidence만 수집
        val phase2 = try {
            val deviceEvidence = withTimeoutOrNull(DEVICE_EVIDENCE_TIMEOUT_MS) {
                deviceEvidenceProvider.gather(normalizedNumber)
            }

            val localLearning = localLearningProvider.getSignal(normalizedNumber)
            val behaviorSignal = buildBehaviorSignal(input, countryCode)

            val result = applySecondaryIdentifierSignals(
                base = decisionEngine.evaluate(
                    deviceEvidence = deviceEvidence,
                    searchEvidence = null,
                    localLearning = localLearning,
                    behaviorPattern = behaviorSignal,
                    actionState = input.actionState,
                ),
                input = input,
            )
            Log.d(
                TAG,
                "importance level=${result.importanceLevel} rule=${result.importanceReason}",
            )

            // 국가별 위험 가중 적용
            val countryRiskBoost = countryPolicyProvider.getRiskBoost(normalizedNumber, countryCode)
            val adjustedRiskScore = (result.confidence + countryRiskBoost).coerceIn(0f, 1f)

            // 캐시 저장
            if (input.channel == IdentifierChannel.CALL) {
                cacheResult(normalizedNumber, result)
                storePreJudge(normalizedNumber, result)
            }

            val phase2Time = System.currentTimeMillis()
            PhaseResult(
                action = result.action,
                riskScore = adjustedRiskScore,
                category = result.category,
                confidence = result.confidence,
                summary = result.summary,
                riskLevel = result.riskLevel,
                source = PhaseSource.DEVICE_ONLY,
                completedAtMs = phase2Time,
            )
        } catch (e: Exception) {
            Log.w(TAG, "LIGHT Phase2 error: ${e.message}")
            null
        }

        val phase2LatencyMs = System.currentTimeMillis() - startMs
        Log.i(TAG, "LIGHT: phase1=${phase1.action}(${phase1LatencyMs}ms), phase2=${phase2?.action}(${phase2LatencyMs}ms)")

        return TwoPhaseDecision(
            phase1 = phase1,
            phase2 = phase2,
            phaseMeta = PhaseMeta(
                pipelineStartMs = startMs,
                phase1LatencyMs = phase1LatencyMs,
                phase2LatencyMs = phase2LatencyMs,
                route = route,
                conflictDetected = phase2 != null && phase1.action != phase2.action,
            ),
        )
    }

    // ══════════════════════════════════════════
    // FULL: Phase 1 (캐시) + Phase 2 (4축 병렬, ~4500ms)
    // ══════════════════════════════════════════

    private suspend fun buildFullDecision(
        input: IdentifierAnalysisInput,
        countryCode: String,
        preJudge: PreJudgeResult?,
        startMs: Long,
        route: InterceptRoute,
    ): TwoPhaseDecision {
        val normalizedNumber = input.normalizedNumber
        val now = System.currentTimeMillis()
        if (input.channel == IdentifierChannel.CALL) {
            trackRecentCall(normalizedNumber, now)
        }

        // Phase 1: 캐시 기반 즉시 판단
        val phase1 = buildPhase1FromCacheOrPolicy(normalizedNumber, preJudge, countryCode, now)
        val phase1LatencyMs = System.currentTimeMillis() - startMs

        // Phase 2: 풀 파이프라인
        val phase2 = coroutineScope {
            try {
                val online = isNetworkAvailable()

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
                                searchEvidenceProvider.gather(normalizedNumber, countryCode)
                            } catch (e: Exception) {
                                Log.w(TAG, "Search evidence error", e)
                                null
                            }
                        }
                    }
                } else null

                val localLearningJob = async {
                    try {
                        localLearningProvider.getSignal(normalizedNumber)
                    } catch (e: Exception) {
                        null
                    }
                }

                val deviceEvidence = deviceJob.await()
                val searchEvidence = searchJob?.await()
                val localLearning = localLearningJob.await()
                val behaviorSignal = buildBehaviorSignal(input, countryCode)

                val result = applySecondaryIdentifierSignals(
                    base = decisionEngine.evaluate(
                        deviceEvidence = deviceEvidence,
                        searchEvidence = searchEvidence,
                        localLearning = localLearning,
                        behaviorPattern = behaviorSignal,
                        actionState = input.actionState,
                    ),
                    input = input,
                )
                Log.d(
                    TAG,
                    "importance level=${result.importanceLevel} rule=${result.importanceReason}",
                )

                // 국가별 위험 가중 적용
                val countryRiskBoost = countryPolicyProvider.getRiskBoost(normalizedNumber, countryCode)
                val spamPeakBoost = if (countryPolicyProvider.isSpamPeakHour(
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY), countryCode
                    )) 0.03f else 0f
                val totalBoost = countryRiskBoost + spamPeakBoost

                // 캐시 저장
                if (input.channel == IdentifierChannel.CALL) {
                    cacheResult(normalizedNumber, result)
                    storePreJudge(normalizedNumber, result)
                }

                val phase2Time = System.currentTimeMillis()
                PhaseResult(
                    action = result.action,
                    riskScore = (result.riskLevel.ordinal / 3f + totalBoost).coerceIn(0f, 1f),
                    category = result.category,
                    confidence = result.confidence,
                    summary = result.summary,
                    riskLevel = result.riskLevel,
                    source = PhaseSource.FULL_PIPELINE,
                    completedAtMs = phase2Time,
                )
            } catch (e: Exception) {
                Log.e(TAG, "FULL Phase2 error", e)
                null
            }
        }

        val phase2LatencyMs = System.currentTimeMillis() - startMs
        Log.i(TAG, "FULL: phase1=${phase1.action}(${phase1LatencyMs}ms), phase2=${phase2?.action}(${phase2LatencyMs}ms)")

        return TwoPhaseDecision(
            phase1 = phase1,
            phase2 = phase2,
            phaseMeta = PhaseMeta(
                pipelineStartMs = startMs,
                phase1LatencyMs = phase1LatencyMs,
                phase2LatencyMs = phase2LatencyMs,
                route = route,
                conflictDetected = phase2 != null && phase1.action != phase2.action,
            ),
        )
    }

    // ══════════════════════════════════════════
    // Phase 1 빌더 (캐시 or 국가 정책 기반)
    // ══════════════════════════════════════════

    private fun buildPhase1FromCacheOrPolicy(
        normalizedNumber: String,
        preJudge: PreJudgeResult?,
        countryCode: String,
        now: Long,
    ): PhaseResult {
        // Tier 0: PreJudge 캐시
        if (preJudge != null && preJudge.isUsable()) {
            return PhaseResult(
                action = preJudge.action,
                riskScore = preJudge.riskScore,
                category = preJudge.category,
                confidence = preJudge.effectiveConfidence(),
                summary = preJudge.summary,
                riskLevel = decisionEngine.riskLevelFromScore(preJudge.riskScore),
                source = PhaseSource.PRE_JUDGE_CACHE,
                completedAtMs = now,
            )
        }

        // Tier 1: 인메모리 캐시
        val cached = decisionCache[normalizedNumber]
        if (cached != null && cached.isValid(SearchResultCachePolicy.DECISION_CACHE_TTL_MS)) {
            val result = cached.data
            return PhaseResult(
                action = result.action,
                riskScore = result.riskLevel.ordinal / 3f,
                category = result.category,
                confidence = result.confidence,
                summary = result.summary,
                riskLevel = result.riskLevel,
                source = PhaseSource.MEMORY_CACHE,
                completedAtMs = now,
            )
        }

        // 캐시 미스: 국가 정책 기반 기본 판단
        val countryRisk = countryPolicyProvider.getRiskBoost(normalizedNumber, countryCode)
        val baseAction = if (countryRisk >= 0.10f) {
            ActionRecommendation.ANSWER_WITH_CAUTION
        } else {
            ActionRecommendation.HOLD
        }

        return PhaseResult(
            action = baseAction,
            riskScore = countryRisk,
            category = ConclusionCategory.INSUFFICIENT_EVIDENCE,
            confidence = 0.20f,
            summary = if (baseAction == ActionRecommendation.HOLD) "판단 중..." else "주의 필요",
            riskLevel = if (countryRisk >= 0.10f) RiskLevel.MEDIUM else RiskLevel.UNKNOWN,
            source = PhaseSource.COUNTRY_POLICY,
            completedAtMs = now,
        )
    }

    // ══════════════════════════════════════════
    // SKIP (안전망)
    // ══════════════════════════════════════════

    private fun buildSkipDecision(startMs: Long, route: InterceptRoute): TwoPhaseDecision {
        val now = System.currentTimeMillis()
        return TwoPhaseDecision(
            phase1 = PhaseResult(
                action = ActionRecommendation.ANSWER,
                riskScore = 0f,
                category = ConclusionCategory.KNOWN_CONTACT,
                confidence = 1.0f,
                summary = "안전",
                riskLevel = RiskLevel.LOW,
                source = PhaseSource.FALLBACK,
                completedAtMs = now,
            ),
            phaseMeta = PhaseMeta(
                pipelineStartMs = startMs,
                phase1LatencyMs = now - startMs,
                route = route,
            ),
        )
    }

    // ══════════════════════════════════════════
    // 공통 유틸
    // ══════════════════════════════════════════

    private fun trackRecentCall(normalizedNumber: String, timestampMs: Long) {
        val timestamps = recentCallLog.getOrPut(normalizedNumber) { mutableListOf() }
        timestamps.add(timestampMs)
        while (timestamps.size > 10) timestamps.removeAt(0)
    }

    private fun buildBehaviorSignal(
        input: IdentifierAnalysisInput,
        deviceCountryCode: String?,
    ): BehaviorPatternSignal {
        val normalizedNumber = input.normalizedNumber
        val now = System.currentTimeMillis()
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timestamps = if (input.channel == IdentifierChannel.CALL) {
            recentCallLog[normalizedNumber] ?: emptyList()
        } else {
            emptyList()
        }
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

    private fun applySecondaryIdentifierSignals(
        base: DecisionResult,
        input: IdentifierAnalysisInput,
    ): DecisionResult {
        if (input.channel != IdentifierChannel.SMS) return base

        val metadata = input.messageMetadata ?: return base
        if (!metadata.hasUrl && !metadata.hasShortLink) return base

        val riskBoost = when {
            metadata.hasShortLink -> 0.25f
            metadata.hasUrl -> 0.12f
            else -> 0f
        } + when {
            metadata.urlCount >= 2 -> 0.08f
            metadata.urlCount == 1 -> 0.03f
            else -> 0f
        } + when {
            metadata.longestUrlLength >= 48 -> 0.05f
            metadata.longestUrlLength >= 24 -> 0.02f
            else -> 0f
        }

        val adjustedBoost = if (input.isSavedContact) riskBoost * 0.5f else riskBoost
        val boostedRiskLevel = decisionEngine.riskLevelFromScore(
            (riskLevelScore(base.riskLevel) + adjustedBoost).coerceIn(0f, 1f),
        )

        val messageReasons = buildList {
            if (metadata.hasUrl) add("문자 링크 포함")
            if (metadata.urlCount >= 2) add("메시지 링크 ${metadata.urlCount}개 감지")
            if (metadata.longestUrlLength >= 24) add("긴 링크 패턴 감지")
            if (metadata.hasShortLink) add("단축 링크 포함")
        }

        val adjustedCategory = when {
            input.isSavedContact -> base.category
            metadata.hasShortLink &&
                base.category !in setOf(ConclusionCategory.SCAM_RISK_HIGH, ConclusionCategory.MSG_FINANCIAL_SCAM) ->
                ConclusionCategory.MSG_PHISHING_LINK
            metadata.hasUrl && base.category == ConclusionCategory.INSUFFICIENT_EVIDENCE ->
                ConclusionCategory.MSG_UNKNOWN_SENDER
            else -> base.category
        }

        val adjustedAction = moreCautiousAction(
            current = base.action,
            candidate = when (adjustedCategory) {
                ConclusionCategory.MSG_PHISHING_LINK -> ActionRecommendation.BLOCK_REVIEW
                ConclusionCategory.MSG_UNKNOWN_SENDER -> ActionRecommendation.ANSWER_WITH_CAUTION
                else -> base.action
            },
        )

        val adjustedSummary = when (adjustedCategory) {
            ConclusionCategory.MSG_PHISHING_LINK,
            ConclusionCategory.MSG_UNKNOWN_SENDER,
            -> context.resources.getString(adjustedCategory.summaryResId)
            else -> base.summary
        }

        return base.copy(
            riskLevel = if (boostedRiskLevel.ordinal > base.riskLevel.ordinal) boostedRiskLevel else base.riskLevel,
            category = adjustedCategory,
            action = adjustedAction,
            confidence = (base.confidence + if (messageReasons.isNotEmpty()) 0.08f else 0f).coerceIn(0f, 1f),
            summary = adjustedSummary,
            reasons = (messageReasons + base.reasons).distinct().take(3),
        )
    }

    private fun riskLevelScore(level: RiskLevel): Float = when (level) {
        RiskLevel.UNKNOWN -> 0f
        RiskLevel.LOW -> 0.15f
        RiskLevel.MEDIUM -> 0.45f
        RiskLevel.HIGH -> 0.75f
    }

    private fun moreCautiousAction(
        current: ActionRecommendation,
        candidate: ActionRecommendation,
    ): ActionRecommendation = if (actionSeverity(candidate) > actionSeverity(current)) {
        candidate
    } else {
        current
    }

    private fun actionSeverity(action: ActionRecommendation): Int = when (action) {
        ActionRecommendation.ANSWER -> 0
        ActionRecommendation.HOLD -> 1
        ActionRecommendation.ANSWER_WITH_CAUTION -> 2
        ActionRecommendation.REJECT -> 3
        ActionRecommendation.BLOCK_REVIEW -> 4
    }

    private fun isInternationalNumber(normalizedNumber: String, deviceCountryCode: String?): Boolean {
        if (deviceCountryCode == null || !normalizedNumber.startsWith("+")) return false
        val numberCountry = normalizedNumber.removePrefix("+")
        val policy = countryPolicyProvider.getPolicy(deviceCountryCode)
        if (policy.dialCode.isEmpty()) return false
        return !numberCountry.startsWith(policy.dialCode)
    }

    private suspend fun storePreJudge(normalizedNumber: String, result: DecisionResult) {
        try {
            preJudgeCacheRepository.store(normalizedNumber, result)
        } catch (e: Exception) {
            Log.w(TAG, "PreJudge store error: ${e.message}")
        }
    }

    private fun cacheResult(normalizedNumber: String, result: DecisionResult) {
        if (decisionCache.size >= SearchResultCachePolicy.MEMORY_CACHE_MAX_ENTRIES) {
            evictExpiredEntries()
        }
        if (decisionCache.size >= SearchResultCachePolicy.MEMORY_CACHE_MAX_ENTRIES) {
            evictOldestEntry()
        }
        decisionCache[normalizedNumber] = CachedEntry(data = result, phoneNumber = normalizedNumber)
    }

    private fun evictExpiredEntries() {
        val now = System.currentTimeMillis()
        val expired = decisionCache.entries.filter {
            !it.value.isValid(SearchResultCachePolicy.DECISION_CACHE_TTL_MS, now)
        }
        expired.forEach { decisionCache.remove(it.key) }
    }

    private fun evictOldestEntry() {
        val oldest = decisionCache.entries.minByOrNull { it.value.cachedAtMs }
        if (oldest != null) {
            decisionCache.remove(oldest.key)
        }
    }
}
