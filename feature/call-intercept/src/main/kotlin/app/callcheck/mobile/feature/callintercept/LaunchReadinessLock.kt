package app.callcheck.mobile.feature.callintercept

import app.callcheck.mobile.core.model.SearchEngine
import app.callcheck.mobile.core.model.SearchTier
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 출시 준비 최종 잠금 장치.
 *
 * 자비스 기준: "국가별 Tier, 검색엔진 우선순위, 하드 룰, SLA 기준을 최종 상수로 고정"
 *
 * 이 클래스의 모든 값은 상수(const/val)이며, 런타임에서 변경 불가.
 * AutoPolicyAdjuster의 자동 보정도 이 하드 룰을 침범 불가.
 *
 * 잠금 대상:
 *   1. 국가별 검색엔진 하드 룰 (절대 변경 금지)
 *   2. 글로벌 SLA 상수 (2초 절대 한계)
 *   3. 최소 안전 표현 (검색 결과 0건이어도 반드시 표시)
 *   4. 국가 Tier 분류 (Tier1/2/3 검증 기준)
 *   5. 출시 PASS/FAIL 판정 기준
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class LaunchReadinessLock @Inject constructor(
    private val registry: GlobalSearchProviderRegistry,
    private val complianceValidator: CountryComplianceValidator,
) {

    // ══════════════════════════════════════════════════════════════
    // 1. 검색엔진 하드 룰 (절대 불변)
    // ══════════════════════════════════════════════════════════════

    companion object {

        // ── 글로벌 SLA 상수 ──

        /** 전 국가 공통 SLA 한계 (ms). 이 값은 절대 변경 금지. */
        const val GLOBAL_HARD_DEADLINE_MS = 2000L

        /** 번호 정규화 한계 (ms) */
        const val NORMALIZE_DEADLINE_MS = 50L

        /** 국가 라우팅 한계 (ms) */
        const val ROUTING_DEADLINE_MS = 150L

        /** 1순위 검색 기본 한계 (ms) */
        const val PRIMARY_SEARCH_DEADLINE_MS = 1200L

        /** 2순위 fallback 한계 (ms) */
        const val SECONDARY_FALLBACK_DEADLINE_MS = 1800L

        /** Early Display 시점 (ms) — 현재까지 결과 1차 표시 */
        const val EARLY_DISPLAY_MS = 1500L

        /** 캐시 히트 목표 한계 (ms) */
        const val CACHE_HIT_DEADLINE_MS = 50L

        // ── 검색엔진 하드 룰 ──

        /** 국가별 1순위 검색엔진 강제 매핑. 이 국가들은 자동 보정 불가. */
        val LOCKED_PRIMARY_ENGINES: Map<String, SearchEngine> = mapOf(
            "KR" to SearchEngine.NAVER,
            "CN" to SearchEngine.BAIDU,
            "JP" to SearchEngine.YAHOO_JAPAN,
            "RU" to SearchEngine.YANDEX,
            "CZ" to SearchEngine.SEZNAM,
        )

        /** 국가별 글로벌 금지 엔진. 이 엔진은 해당 국가에서 어떤 순위로도 사용 불가. */
        val GLOBAL_BANNED_ENGINES: Map<String, Set<SearchEngine>> = mapOf(
            "CN" to setOf(SearchEngine.GOOGLE, SearchEngine.BING, SearchEngine.DUCKDUCKGO),
        )

        /** 자동 보정 금지 국가. 이 국가들의 엔진 순서는 수동으로만 변경 가능. */
        val AUTO_ADJUST_LOCKED_COUNTRIES: Set<String> = setOf(
            "KR", "CN", "JP", "RU", "CZ",
        )

        // ── 최소 안전 표현 (검색 결과 없어도 반드시 표시) ──

        /** 결과 부족 시 표현 — "아무것도 안 보여줌" 금지 */
        val MINIMUM_SAFE_VERDICTS: Map<String, String> = mapOf(
            "ko" to "정보 부족 — 주의하여 응답하세요",
            "en" to "Insufficient data — Answer with caution",
            "ja" to "情報不足 — 注意して応答してください",
            "zh" to "信息不足 — 请谨慎接听",
            "ru" to "Недостаточно данных — Ответьте с осторожностью",
            "es" to "Datos insuficientes — Responda con precaución",
            "fr" to "Données insuffisantes — Répondez avec prudence",
            "de" to "Unzureichende Daten — Mit Vorsicht antworten",
            "pt" to "Dados insuficientes — Atenda com cautela",
            "ar" to "بيانات غير كافية — أجب بحذر",
            "hi" to "अपर्याप्त डेटा — सावधानी से उत्तर दें",
            "th" to "ข้อมูลไม่เพียงพอ — ตอบรับด้วยความระวัง",
            "vi" to "Dữ liệu không đủ — Trả lời cẩn thận",
            "tr" to "Yetersiz veri — Dikkatli cevaplayın",
            "id" to "Data tidak cukup — Jawab dengan hati-hati",
        )

        /** 스팸 의심 표현 */
        val SPAM_SUSPECTED_LABELS: Map<String, String> = mapOf(
            "ko" to "스팸 의심",
            "en" to "Spam Suspected",
            "ja" to "スパムの疑い",
            "zh" to "疑似垃圾电话",
            "ru" to "Подозрение на спам",
            "es" to "Sospecha de spam",
            "fr" to "Spam suspecté",
            "de" to "Spam-Verdacht",
            "pt" to "Suspeita de spam",
            "ar" to "اشتباه في رسائل مزعجة",
            "hi" to "स्पैम संदिग्ध",
            "th" to "สงสัยสแปม",
            "vi" to "Nghi ngờ spam",
        )

        /** 사기 의심 표현 */
        val SCAM_RISK_LABELS: Map<String, String> = mapOf(
            "ko" to "사기 위험",
            "en" to "Scam Risk",
            "ja" to "詐欺の危険",
            "zh" to "诈骗风险",
            "ru" to "Риск мошенничества",
            "es" to "Riesgo de estafa",
            "fr" to "Risque d'arnaque",
            "de" to "Betrugsrisiko",
            "pt" to "Risco de golpe",
            "ar" to "خطر احتيال",
            "hi" to "धोखाधड़ी का खतरा",
            "th" to "เสี่ยงหลอกลวง",
            "vi" to "Nguy cơ lừa đảo",
        )

        /** 기관/배송/서비스 추정 표현 */
        val LIKELY_SAFE_LABELS: Map<String, String> = mapOf(
            "ko" to "기관/서비스 추정",
            "en" to "Likely Service/Institution",
            "ja" to "機関/サービスの可能性",
            "zh" to "可能是机构/服务",
            "ru" to "Возможно учреждение/сервис",
            "es" to "Posible servicio/institución",
            "fr" to "Service/institution probable",
            "de" to "Wahrscheinlich Dienst/Institution",
            "pt" to "Provável serviço/instituição",
            "ar" to "محتمل مؤسسة/خدمة",
            "hi" to "संभावित सेवा/संस्थान",
            "th" to "น่าจะเป็นหน่วยงาน/บริการ",
            "vi" to "Có thể là dịch vụ/tổ chức",
        )

        // ── 출시 PASS/FAIL 기준 ──

        /** SLA 통과율 하한 (이하면 해당 국가 FAIL) */
        const val SLA_PASS_RATE_THRESHOLD = 95.0f

        /** 검색 실패율 상한 (이상이면 해당 국가 FAIL) */
        const val SEARCH_FAILURE_RATE_THRESHOLD = 0.10f

        /** 총 등록 국가 최소 기준 */
        const val MINIMUM_REGISTERED_COUNTRIES = 190

        /** Tier1 국가 최소 기준 */
        const val MINIMUM_TIER1_COUNTRIES = 25
    }

    // ══════════════════════════════════════════════════════════════
    // 2. 잠금 검증
    // ══════════════════════════════════════════════════════════════

    /**
     * 잠금 상태 검증 결과.
     */
    data class LockVerification(
        val registryLocked: Boolean,
        val hardRulesLocked: Boolean,
        val slaLocked: Boolean,
        val safeExpressionsLocked: Boolean,
        val issues: List<String>,
    ) {
        val allLocked: Boolean get() = registryLocked && hardRulesLocked && slaLocked && safeExpressionsLocked && issues.isEmpty()
    }

    /**
     * 전체 잠금 상태 검증.
     * 출시 전 이 검증이 통과해야만 배포 가능.
     */
    fun verifyAllLocks(): LockVerification {
        val issues = mutableListOf<String>()

        // ── Registry 잠금 ──
        val registryCount = registry.registeredCountryCount()
        val registryLocked = registryCount >= MINIMUM_REGISTERED_COUNTRIES
        if (!registryLocked) {
            issues.add("등록 국가 ${registryCount} < 최소 $MINIMUM_REGISTERED_COUNTRIES")
        }

        // ── 하드 룰 잠금 ──
        var hardRulesOk = true

        LOCKED_PRIMARY_ENGINES.forEach { (cc, requiredEngine) ->
            val config = registry.getConfig(cc)
            if (config.primaryEngine != requiredEngine) {
                hardRulesOk = false
                issues.add("[$cc] 1순위 ${config.primaryEngine.displayName} ≠ ${requiredEngine.displayName}")
            }
        }

        GLOBAL_BANNED_ENGINES.forEach { (cc, banned) ->
            val config = registry.getConfig(cc)
            banned.forEach { bannedEngine ->
                if (bannedEngine !in config.bannedEngines) {
                    hardRulesOk = false
                    issues.add("[$cc] ${bannedEngine.displayName} 금지 미설정")
                }
            }
        }

        // ── SLA 잠금 ──
        var slaOk = true
        registry.allCountries().forEach { config ->
            if (config.timeoutPolicy.hardDeadlineMs > GLOBAL_HARD_DEADLINE_MS) {
                slaOk = false
                issues.add("[${config.countryCode}] hardDeadline ${config.timeoutPolicy.hardDeadlineMs}ms > ${GLOBAL_HARD_DEADLINE_MS}ms")
            }
        }

        // ── 안전 표현 잠금 ──
        val safeExpressionsOk = MINIMUM_SAFE_VERDICTS.isNotEmpty() &&
            SPAM_SUSPECTED_LABELS.isNotEmpty() &&
            SCAM_RISK_LABELS.isNotEmpty() &&
            LIKELY_SAFE_LABELS.isNotEmpty()

        return LockVerification(
            registryLocked = registryLocked,
            hardRulesLocked = hardRulesOk,
            slaLocked = slaOk,
            safeExpressionsLocked = safeExpressionsOk,
            issues = issues,
        )
    }

    /**
     * 최종 출시 준비 판정.
     */
    fun isLaunchReady(): LaunchReadiness {
        val lockVerification = verifyAllLocks()
        val complianceReport = complianceValidator.validateAll()

        val tierCounts = registry.tierCounts()
        val tier1Count = tierCounts[SearchTier.TIER_A]?.let { it } ?: 0

        return LaunchReadiness(
            locksPassed = lockVerification.allLocked,
            compliancePassed = complianceReport.allPassed,
            totalCountries = registry.registeredCountryCount(),
            tier1Countries = tier1Count,
            lockIssues = lockVerification.issues,
            complianceFailCount = complianceReport.failCount,
            ready = lockVerification.allLocked && complianceReport.allPassed,
        )
    }

    data class LaunchReadiness(
        val locksPassed: Boolean,
        val compliancePassed: Boolean,
        val totalCountries: Int,
        val tier1Countries: Int,
        val lockIssues: List<String>,
        val complianceFailCount: Int,
        val ready: Boolean,
    ) {
        fun toJarvisFormat(): String = buildString {
            appendLine("═══ Launch Readiness 최종 판정 ═══")
            appendLine()
            appendLine("출시 준비: ${if (ready) "✅ READY" else "❌ NOT READY"}")
            appendLine()
            appendLine("잠금 검증: ${if (locksPassed) "✅ PASS" else "❌ FAIL"}")
            appendLine("컴플라이언스: ${if (compliancePassed) "✅ PASS" else "❌ FAIL (${complianceFailCount}건)"}")
            appendLine("등록 국가: ${totalCountries}개국")
            appendLine("Tier A 국가: ${tier1Countries}개국")
            appendLine()

            if (lockIssues.isNotEmpty()) {
                appendLine("── 잠금 이슈 ──")
                lockIssues.forEach { appendLine("  ⚠️ $it") }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    // 3. 언어별 안전 표현 조회
    // ══════════════════════════════════════════════════════════════

    /** 결과 부족 시 표현 (언어코드 기반, fallback: 영어) */
    fun getMinimumSafeVerdict(languageCode: String): String {
        val baseCode = languageCode.split("-").first()
        return MINIMUM_SAFE_VERDICTS[baseCode] ?: MINIMUM_SAFE_VERDICTS["en"]!!
    }

    /** 스팸 의심 표현 */
    fun getSpamLabel(languageCode: String): String {
        val baseCode = languageCode.split("-").first()
        return SPAM_SUSPECTED_LABELS[baseCode] ?: SPAM_SUSPECTED_LABELS["en"]!!
    }

    /** 사기 위험 표현 */
    fun getScamLabel(languageCode: String): String {
        val baseCode = languageCode.split("-").first()
        return SCAM_RISK_LABELS[baseCode] ?: SCAM_RISK_LABELS["en"]!!
    }

    /** 기관/서비스 추정 표현 */
    fun getLikelySafeLabel(languageCode: String): String {
        val baseCode = languageCode.split("-").first()
        return LIKELY_SAFE_LABELS[baseCode] ?: LIKELY_SAFE_LABELS["en"]!!
    }
}
