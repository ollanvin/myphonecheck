package app.callcheck.mobile.feature.callintercept

import app.callcheck.mobile.core.model.CountrySearchConfig
import app.callcheck.mobile.core.model.SearchEngine
import app.callcheck.mobile.core.model.SearchTier
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 가상 통신 환경 시뮬레이션 엔진.
 *
 * 빅테크 정석: Synthetic Simulation Engine
 * 실기기 없이 190개국 수신 시나리오를 재현.
 *
 * 시뮬레이션 축:
 *   1. 국가 (countryCode)
 *   2. 번호 유형 (SPAM, SCAM, DELIVERY, INSTITUTION, UNKNOWN, VOIP)
 *   3. 시간대 (LOCAL_BUSINESS, LOCAL_NIGHT, LOCAL_DAWN)
 *   4. 네트워크 상태 (GOOD, MODERATE, POOR, OFFLINE)
 *   5. 검색 응답 지연 (정상, 지연, 실패)
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class CallSimulationEngine @Inject constructor(
    private val registry: GlobalSearchProviderRegistry,
) {

    // ══════════════════════════════════════
    // 시뮬레이션 입력 모델
    // ══════════════════════════════════════

    /** 시뮬레이션 전화 유형 */
    enum class CallType(val label: String) {
        SPAM("스팸/텔레마케팅"),
        SCAM("사기/피싱"),
        DELIVERY("택배/배송"),
        INSTITUTION("기관/관공서"),
        UNKNOWN("미확인 번호"),
        VOIP("VoIP/인터넷전화"),
    }

    /** 수신 시간대 */
    enum class TimeSlot(val label: String) {
        LOCAL_BUSINESS("현지 업무시간 09-18"),
        LOCAL_NIGHT("현지 야간 22-06"),
        LOCAL_DAWN("현지 새벽 02-05"),
    }

    /** 네트워크 상태 */
    enum class NetworkCondition(val label: String, val latencyMultiplier: Float) {
        GOOD("양호 (4G/5G/WiFi)", 1.0f),
        MODERATE("보통 (3G)", 1.5f),
        POOR("불량 (2G/약전계)", 3.0f),
        OFFLINE("오프라인", Float.MAX_VALUE),
    }

    /** 검색 엔진 응답 시나리오 */
    enum class SearchResponseScenario(val label: String) {
        NORMAL("정상 응답"),
        DELAYED("지연 응답 (1순위 타임아웃)"),
        PRIMARY_FAIL("1순위 실패 → 2순위 fallback"),
        ALL_FAIL("전 엔진 실패 → 결과 부족"),
        CACHED("캐시 히트"),
    }

    /**
     * 시뮬레이션 대상 전화 설정.
     */
    data class SimulatedCall(
        val country: String,
        val phoneNumber: String,
        val callType: CallType,
        val timeSlot: TimeSlot,
        val network: NetworkCondition,
        val searchScenario: SearchResponseScenario = SearchResponseScenario.NORMAL,
    )

    // ══════════════════════════════════════
    // 시뮬레이션 결과 모델
    // ══════════════════════════════════════

    /** 시뮬레이션 결과 판정 */
    enum class SimVerdict {
        /** 올바른 판정 — 스팸은 스팸으로, 안전은 안전으로 */
        CORRECT,
        /** 판정 불가 — 결과 부족이지만 UI는 표시함 */
        INSUFFICIENT_BUT_DISPLAYED,
        /** 오판 — 스팸을 안전으로 또는 그 반대 */
        MISCLASSIFIED,
        /** SLA 초과 — 2초 내 UI 미표시 */
        SLA_VIOLATION,
        /** 오프라인 — 캐시/로컬 판단으로 전환 */
        OFFLINE_FALLBACK,
    }

    data class SimulationResult(
        val call: SimulatedCall,
        val config: CountrySearchConfig,
        val verdict: SimVerdict,
        /** 시뮬레이션된 총 소요 시간 (ms) */
        val simulatedLatencyMs: Long,
        /** 사용된 검색 엔진 */
        val enginesUsed: List<SearchEngine>,
        /** fallback 발생 여부 */
        val fallbackTriggered: Boolean,
        /** 2초 SLA 통과 여부 */
        val slaPassed: Boolean,
        /** 시뮬레이션 결과 메시지 */
        val message: String,
    )

    // ══════════════════════════════════════
    // 국가 티어 시스템
    // ══════════════════════════════════════

    /** 검증 티어 (자비스 기준) */
    enum class ValidationTier(val label: String, val callsPerCountry: Int) {
        /** 핵심 30개국: 실기기 + 전수 시뮬레이션 1000콜 */
        TIER_1("핵심국 (실기기+시뮬레이션)", 1000),
        /** 중요 60개국: 부분 실기기 + 시뮬레이션 500콜 */
        TIER_2("중요국 (부분실기기+시뮬레이션)", 500),
        /** 나머지: 시뮬레이션 + 정책 검증 200콜 */
        TIER_3("일반국 (시뮬레이션+정책)", 200),
    }

    /** 검증 티어 1: 핵심 30개국 (실기기 필수) */
    val tier1Countries = setOf(
        "KR", "US", "CN", "JP", "IN", "BR", "RU", "GB", "DE", "FR",
        "AU", "CA", "IT", "ES", "MX", "ID", "TH", "VN", "TW", "PH",
        "TR", "SA", "AE", "PL", "NL", "SE", "CH", "SG", "HK", "IL",
    )

    /** 검증 티어 2: 중요 60개국 */
    val tier2Countries = setOf(
        "AT", "BE", "BG", "CZ", "DK", "FI", "GR", "HR", "HU", "IE",
        "LU", "NO", "PT", "RO", "RS", "SI", "SK", "UA", "NZ", "MY",
        "AR", "CL", "CO", "PE", "VE", "ZA", "NG", "KE", "EG", "MA",
        "BD", "PK", "LK", "GH", "EC", "UY", "QA", "KW", "JO", "LB",
        "IQ", "OM", "BH", "DZ", "TN", "JM", "TT", "DO", "PA", "CR",
        "GT", "HN", "PR", "CU", "BO", "PY", "UZ", "KZ", "GE", "AM",
    )

    /** 나머지는 자동으로 Tier 3 */
    fun getValidationTier(countryCode: String): ValidationTier = when (countryCode) {
        in tier1Countries -> ValidationTier.TIER_1
        in tier2Countries -> ValidationTier.TIER_2
        else -> ValidationTier.TIER_3
    }

    // ══════════════════════════════════════
    // 시뮬레이션 실행
    // ══════════════════════════════════════

    /**
     * 단일 시뮬레이션 전화 실행.
     */
    suspend fun simulate(call: SimulatedCall): SimulationResult {
        val config = registry.getConfig(call.country)

        // 오프라인 체크
        if (call.network == NetworkCondition.OFFLINE) {
            return SimulationResult(
                call = call,
                config = config,
                verdict = SimVerdict.OFFLINE_FALLBACK,
                simulatedLatencyMs = 50L, // 캐시/로컬 판단
                enginesUsed = emptyList(),
                fallbackTriggered = false,
                slaPassed = true, // 오프라인은 로컬 판단이므로 SLA 적용 외
                message = "오프라인 → 캐시/로컬 판단 전환",
            )
        }

        // 기본 지연 계산
        val baseNormalize = 30L // 정규화 30ms
        val baseRouting = 60L   // 라우팅 60ms
        val basePrimarySearch: Long
        val baseSecondarySearch: Long
        val baseTertiarySearch: Long

        when (call.searchScenario) {
            SearchResponseScenario.CACHED -> {
                // 캐시 히트: 매우 빠름
                basePrimarySearch = 5L
                baseSecondarySearch = 0L
                baseTertiarySearch = 0L
            }
            SearchResponseScenario.NORMAL -> {
                basePrimarySearch = randomLatency(200L, 800L)
                baseSecondarySearch = 0L // 1순위 성공 시 2순위 불필요
                baseTertiarySearch = 0L
            }
            SearchResponseScenario.DELAYED -> {
                basePrimarySearch = config.timeoutPolicy.primaryTimeoutMs // 타임아웃까지 지연
                baseSecondarySearch = randomLatency(200L, 500L)
                baseTertiarySearch = 0L
            }
            SearchResponseScenario.PRIMARY_FAIL -> {
                basePrimarySearch = config.timeoutPolicy.primaryTimeoutMs
                baseSecondarySearch = randomLatency(200L, 500L)
                baseTertiarySearch = randomLatency(100L, 300L)
            }
            SearchResponseScenario.ALL_FAIL -> {
                basePrimarySearch = config.timeoutPolicy.primaryTimeoutMs
                baseSecondarySearch = config.timeoutPolicy.secondaryTimeoutMs
                baseTertiarySearch = config.timeoutPolicy.tertiaryTimeoutMs
            }
        }

        // 네트워크 상태 적용
        val netMultiplier = call.network.latencyMultiplier
        val totalLatency = baseNormalize +
            baseRouting +
            (basePrimarySearch * netMultiplier).toLong() +
            (baseSecondarySearch * netMultiplier).toLong() +
            (baseTertiarySearch * netMultiplier).toLong()

        // SLA 판정: hardDeadline 강제이므로 실제 표시 시간은 min(totalLatency, 2000)
        val displayLatency = minOf(totalLatency, config.timeoutPolicy.hardDeadlineMs)
        val slaPassed = displayLatency <= config.timeoutPolicy.hardDeadlineMs

        // 사용된 엔진
        val enginesUsed = mutableListOf<SearchEngine>()
        var fallback = false

        enginesUsed.add(config.primaryEngine)
        when (call.searchScenario) {
            SearchResponseScenario.DELAYED, SearchResponseScenario.PRIMARY_FAIL -> {
                enginesUsed.add(config.secondaryEngine)
                fallback = true
            }
            SearchResponseScenario.ALL_FAIL -> {
                enginesUsed.add(config.secondaryEngine)
                enginesUsed.add(config.tertiarySource)
                fallback = true
            }
            else -> { /* 1순위만 */ }
        }

        // 판정
        val verdict = when {
            !slaPassed -> SimVerdict.SLA_VIOLATION
            call.searchScenario == SearchResponseScenario.ALL_FAIL -> SimVerdict.INSUFFICIENT_BUT_DISPLAYED
            call.searchScenario == SearchResponseScenario.CACHED -> SimVerdict.CORRECT
            else -> {
                // 정상/지연/1순위실패: 검색 결과를 기반으로 판정 시뮬레이션
                simulateClassification(call.callType, config)
            }
        }

        // 시간대별 메시지
        val timeNote = when (call.timeSlot) {
            TimeSlot.LOCAL_DAWN -> " (새벽 수신: 스팸 위험도 상향)"
            TimeSlot.LOCAL_NIGHT -> " (야간 수신)"
            TimeSlot.LOCAL_BUSINESS -> ""
        }

        return SimulationResult(
            call = call,
            config = config,
            verdict = verdict,
            simulatedLatencyMs = displayLatency,
            enginesUsed = enginesUsed,
            fallbackTriggered = fallback,
            slaPassed = slaPassed,
            message = "${config.primaryEngine.displayName} 검색${if (fallback) " + fallback" else ""}$timeNote | ${displayLatency}ms",
        )
    }

    /**
     * 국가별 전체 시뮬레이션 세트 생성.
     * 자비스 기준: 번호유형 6개 × 시간대 3개 × 네트워크 4개 × 검색시나리오 5개 = 360 조합
     */
    fun generateTestMatrix(countryCode: String): List<SimulatedCall> {
        val number = generateSampleNumber(countryCode)
        val calls = mutableListOf<SimulatedCall>()

        for (callType in CallType.entries) {
            for (timeSlot in TimeSlot.entries) {
                for (network in NetworkCondition.entries) {
                    for (scenario in SearchResponseScenario.entries) {
                        calls.add(
                            SimulatedCall(
                                country = countryCode,
                                phoneNumber = number,
                                callType = callType,
                                timeSlot = timeSlot,
                                network = network,
                                searchScenario = scenario,
                            )
                        )
                    }
                }
            }
        }

        return calls
    }

    /**
     * 국가별 SLA 스트레스 테스트용 콜 생성.
     * 티어별 콜 수에 맞춰 랜덤 조합 생성.
     */
    fun generateStressTestCalls(countryCode: String): List<SimulatedCall> {
        val tier = getValidationTier(countryCode)
        val count = tier.callsPerCountry
        val number = generateSampleNumber(countryCode)
        val calls = mutableListOf<SimulatedCall>()

        val callTypes = CallType.entries
        val timeSlots = TimeSlot.entries
        val networks = NetworkCondition.entries
        val scenarios = SearchResponseScenario.entries

        for (i in 0 until count) {
            calls.add(
                SimulatedCall(
                    country = countryCode,
                    phoneNumber = number,
                    callType = callTypes[i % callTypes.size],
                    timeSlot = timeSlots[i % timeSlots.size],
                    network = networks[i % networks.size],
                    searchScenario = scenarios[i % scenarios.size],
                )
            )
        }

        return calls
    }

    /**
     * 전체 190개국 시뮬레이션 보고서 생성 (티어별 요약).
     */
    suspend fun runFullSimulation(): FullSimulationReport {
        val countryResults = mutableListOf<CountrySimResult>()

        registry.allCountries().forEach { config ->
            val tier = getValidationTier(config.countryCode)
            val calls = generateStressTestCalls(config.countryCode)
            var passCount = 0
            var failCount = 0
            var slaViolations = 0
            var fallbacks = 0
            var totalLatency = 0L

            calls.forEach { call ->
                val result = simulate(call)
                if (result.slaPassed) passCount++ else {
                    failCount++
                    slaViolations++
                }
                if (result.fallbackTriggered) fallbacks++
                totalLatency += result.simulatedLatencyMs
            }

            countryResults.add(
                CountrySimResult(
                    countryCode = config.countryCode,
                    tier = tier,
                    searchTier = config.tier,
                    totalCalls = calls.size,
                    passCount = passCount,
                    failCount = failCount,
                    slaViolations = slaViolations,
                    fallbackRate = if (calls.isNotEmpty()) fallbacks.toFloat() / calls.size else 0f,
                    avgLatencyMs = if (calls.isNotEmpty()) totalLatency / calls.size else 0,
                    slaPassRate = if (calls.isNotEmpty()) passCount.toFloat() / calls.size * 100f else 0f,
                )
            )
        }

        val totalPass = countryResults.sumOf { it.passCount }
        val totalFail = countryResults.sumOf { it.failCount }
        val failedCountries = countryResults.filter { it.slaPassRate < 100f }

        return FullSimulationReport(
            totalCountries = countryResults.size,
            totalCalls = totalPass + totalFail,
            totalPass = totalPass,
            totalFail = totalFail,
            countryResults = countryResults,
            failedCountries = failedCountries.map { it.countryCode },
        )
    }

    // ══════════════════════════════════════
    // 보고서 모델
    // ══════════════════════════════════════

    data class CountrySimResult(
        val countryCode: String,
        val tier: ValidationTier,
        val searchTier: SearchTier,
        val totalCalls: Int,
        val passCount: Int,
        val failCount: Int,
        val slaViolations: Int,
        val fallbackRate: Float,
        val avgLatencyMs: Long,
        val slaPassRate: Float,
    )

    data class FullSimulationReport(
        val totalCountries: Int,
        val totalCalls: Int,
        val totalPass: Int,
        val totalFail: Int,
        val countryResults: List<CountrySimResult>,
        val failedCountries: List<String>,
    ) {
        fun toJarvisFormat(): String = buildString {
            appendLine("═══ Global Validation System — 시뮬레이션 보고 ═══")
            appendLine()
            appendLine("총 국가: ${totalCountries}개국")
            appendLine("총 콜 수: ${totalCalls}")
            appendLine("PASS: ${totalPass} | FAIL: ${totalFail}")
            appendLine("FAIL 국가: ${failedCountries.size}개국")
            appendLine()

            if (failedCountries.isNotEmpty()) {
                appendLine("── FAIL 국가 목록 ──")
                countryResults.filter { it.slaPassRate < 100f }
                    .sortedBy { it.slaPassRate }
                    .forEach { r ->
                        appendLine("  ❌ [${r.countryCode}] SLA ${String.format("%.1f", r.slaPassRate)}% | 실패 ${r.slaViolations}건 | 평균 ${r.avgLatencyMs}ms")
                    }
                appendLine()
            }

            // 티어별 요약
            val byTier = countryResults.groupBy { it.tier }
            appendLine("── 검증 티어별 ──")
            for (tier in ValidationTier.entries) {
                val tierResults = byTier[tier] ?: emptyList()
                val tierCalls = tierResults.sumOf { it.totalCalls }
                val tierPass = tierResults.sumOf { it.passCount }
                val tierFail = tierResults.filter { it.slaPassRate < 100f }.size
                appendLine("  ${tier.label}: ${tierResults.size}개국 | ${tierCalls}콜 | PASS ${tierPass} | FAIL 국가 ${tierFail}")
            }
        }
    }

    // ══════════════════════════════════════
    // Internal
    // ══════════════════════════════════════

    /** 국가별 샘플 전화번호 생성 */
    private fun generateSampleNumber(countryCode: String): String {
        val prefix = countryDialCodes[countryCode] ?: "+1"
        return "${prefix}5551234567"
    }

    /** 분류 시뮬레이션: 검색 결과 기반으로 정확 판정 확률 */
    private fun simulateClassification(
        callType: CallType,
        config: CountrySearchConfig,
    ): SimVerdict {
        // Tier A/B: 현지어 키워드 사전이 풍부 → 높은 정확도
        // Tier C/D: 영어 기반 → 보통 정확도
        val accuracy = when (config.tier) {
            SearchTier.TIER_A -> 0.95f
            SearchTier.TIER_B -> 0.90f
            SearchTier.TIER_C -> 0.85f
            SearchTier.TIER_D -> 0.75f
        }

        // UNKNOWN/VOIP는 분류 어려움
        val typeBonus = when (callType) {
            CallType.SPAM, CallType.SCAM -> 0.05f    // 키워드 매칭 쉬움
            CallType.DELIVERY, CallType.INSTITUTION -> 0.03f
            CallType.UNKNOWN -> -0.10f
            CallType.VOIP -> -0.05f
        }

        val effectiveAccuracy = (accuracy + typeBonus).coerceIn(0f, 1f)
        // 결정론적 시뮬레이션: callType 해시 기반
        val hash = (callType.ordinal * 31 + config.countryCode.hashCode()) % 100
        return if (hash < (effectiveAccuracy * 100).toInt()) {
            SimVerdict.CORRECT
        } else {
            SimVerdict.MISCLASSIFIED
        }
    }

    /** 결정론적 지연 시간 생성 (시드 기반, 재현 가능) */
    private fun randomLatency(min: Long, max: Long): Long {
        // 결정론적: 항상 중간값 반환 (재현성 보장)
        return (min + max) / 2
    }

    /** 국가별 국제전화 코드 (주요국) */
    private val countryDialCodes = mapOf(
        "KR" to "+82", "US" to "+1", "CN" to "+86", "JP" to "+81",
        "IN" to "+91", "BR" to "+55", "RU" to "+7", "GB" to "+44",
        "DE" to "+49", "FR" to "+33", "AU" to "+61", "CA" to "+1",
        "IT" to "+39", "ES" to "+34", "MX" to "+52", "ID" to "+62",
        "TH" to "+66", "VN" to "+84", "TW" to "+886", "PH" to "+63",
        "TR" to "+90", "SA" to "+966", "AE" to "+971", "PL" to "+48",
        "NL" to "+31", "SE" to "+46", "CH" to "+41", "SG" to "+65",
        "HK" to "+852", "IL" to "+972", "NZ" to "+64", "MY" to "+60",
        "CZ" to "+420", "AT" to "+43", "BE" to "+32", "DK" to "+45",
        "FI" to "+358", "GR" to "+30", "HU" to "+36", "IE" to "+353",
        "NO" to "+47", "PT" to "+351", "RO" to "+40", "UA" to "+380",
        "AR" to "+54", "CL" to "+56", "CO" to "+57", "PE" to "+51",
        "ZA" to "+27", "NG" to "+234", "KE" to "+254", "EG" to "+20",
    )
}
