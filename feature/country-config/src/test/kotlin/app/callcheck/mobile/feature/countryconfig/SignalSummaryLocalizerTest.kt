package app.callcheck.mobile.feature.countryconfig

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * SignalSummaryLocalizer 단위 테스트.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 검증 대상                                                     │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 1. 7개 언어 × 7개 intensity 전체 커버리지                    │
 * │ 2. 7개 언어 × 7개 category 전체 커버리지                     │
 * │ 3. entity 치환 동작                                           │
 * │ 4. entity null 시 깔끔한 텍스트 생성                          │
 * │ 5. 미지원 키 → EN 폴백                                       │
 * │ 6. localize() 조합 동작                                      │
 * └──────────────────────────────────────────────────────────────┘
 */
class SignalSummaryLocalizerTest {

    private lateinit var localizer: SignalSummaryLocalizer

    @Before
    fun setUp() {
        localizer = SignalSummaryLocalizer()
    }

    // ═══════════════════════════════════════════════════════════
    // 1. Intensity 로컬라이즈 — 전체 7개 언어
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KO intensity SAFE returns Korean text`() {
        assertEquals("수신 안전", localizer.localizeIntensity("SAFE", SupportedLanguage.KO))
    }

    @Test
    fun `KO intensity DANGER returns Korean text`() {
        assertEquals("수신 위험", localizer.localizeIntensity("DANGER", SupportedLanguage.KO))
    }

    @Test
    fun `KO intensity REJECT returns Korean text`() {
        assertEquals("거절 권장", localizer.localizeIntensity("REJECT", SupportedLanguage.KO))
    }

    @Test
    fun `EN intensity SAFE returns English text`() {
        assertEquals("Safe to Answer", localizer.localizeIntensity("SAFE", SupportedLanguage.EN))
    }

    @Test
    fun `EN intensity DANGER returns English text`() {
        assertEquals("High Risk", localizer.localizeIntensity("DANGER", SupportedLanguage.EN))
    }

    @Test
    fun `JA intensity SAFE returns Japanese text`() {
        assertEquals("安全", localizer.localizeIntensity("SAFE", SupportedLanguage.JA))
    }

    @Test
    fun `ZH intensity DANGER returns Chinese text`() {
        assertEquals("高风险", localizer.localizeIntensity("DANGER", SupportedLanguage.ZH))
    }

    @Test
    fun `RU intensity REJECT returns Russian text`() {
        assertEquals("Рекомендуется отклонить", localizer.localizeIntensity("REJECT", SupportedLanguage.RU))
    }

    @Test
    fun `ES intensity CAUTION returns Spanish text`() {
        assertEquals("Ten cuidado", localizer.localizeIntensity("CAUTION", SupportedLanguage.ES))
    }

    @Test
    fun `AR intensity SAFE returns Arabic text`() {
        assertEquals("آمن للرد", localizer.localizeIntensity("SAFE", SupportedLanguage.AR))
    }

    @Test
    fun `all 7 languages have all 7 intensity keys`() {
        val keys = listOf("SAFE", "REFERENCE", "CAUTION_LIGHT", "CAUTION", "DANGER", "REJECT", "VERIFY")
        for (lang in SupportedLanguage.entries) {
            for (key in keys) {
                val result = localizer.localizeIntensity(key, lang)
                assertFalse(
                    "Language ${lang.code} should have intensity $key (got key back)",
                    result == key
                )
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 2. Category 로컬라이즈 — 전체 7개 언어
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KO category SCAM_RISK_HIGH returns Korean text`() {
        assertEquals("사기/피싱 위험", localizer.localizeCategory("SCAM_RISK_HIGH", SupportedLanguage.KO))
    }

    @Test
    fun `EN category DELIVERY_LIKELY without entity`() {
        val result = localizer.localizeCategory("DELIVERY_LIKELY", SupportedLanguage.EN)
        assertEquals("Delivery Call", result)
    }

    @Test
    fun `all 7 languages have all 7 category keys`() {
        val keys = listOf(
            "KNOWN_CONTACT", "BUSINESS_LIKELY", "DELIVERY_LIKELY",
            "INSTITUTION_LIKELY", "SALES_SPAM_SUSPECTED", "SCAM_RISK_HIGH",
            "INSUFFICIENT_EVIDENCE"
        )
        for (lang in SupportedLanguage.entries) {
            for (key in keys) {
                val result = localizer.localizeCategory(key, lang)
                assertFalse(
                    "Language ${lang.code} should have category $key (got key back)",
                    result == key
                )
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 3. Entity 치환
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KO category with entity includes entity name`() {
        val result = localizer.localizeCategory("DELIVERY_LIKELY", SupportedLanguage.KO, "CJ대한통운")
        assertTrue(result.contains("CJ대한통운"))
    }

    @Test
    fun `EN category with entity includes entity name`() {
        val result = localizer.localizeCategory("INSTITUTION_LIKELY", SupportedLanguage.EN, "IRS")
        assertTrue(result.contains("IRS"))
    }

    @Test
    fun `JA category with entity includes entity name`() {
        val result = localizer.localizeCategory("BUSINESS_LIKELY", SupportedLanguage.JA, "ヤマト運輸")
        assertTrue(result.contains("ヤマト運輸"))
    }

    // ═══════════════════════════════════════════════════════════
    // 4. Entity null 시 깔끔한 텍스트
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `category without entity removes placeholder cleanly`() {
        val result = localizer.localizeCategory("BUSINESS_LIKELY", SupportedLanguage.KO)
        assertFalse(result.contains("{entity}"))
        assertFalse(result.startsWith(" "))
    }

    @Test
    fun `EN category without entity has no extra spaces`() {
        val result = localizer.localizeCategory("DELIVERY_LIKELY", SupportedLanguage.EN)
        assertFalse(result.contains("{entity}"))
        assertFalse(result.startsWith(" "))
    }

    // ═══════════════════════════════════════════════════════════
    // 5. 미지원 키 → EN 폴백
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `unknown intensity key returns key as-is`() {
        val result = localizer.localizeIntensity("UNKNOWN_KEY", SupportedLanguage.KO)
        assertEquals("UNKNOWN_KEY", result)
    }

    @Test
    fun `unknown category key returns key as-is`() {
        val result = localizer.localizeCategory("UNKNOWN_CATEGORY", SupportedLanguage.KO)
        assertEquals("UNKNOWN_CATEGORY", result)
    }

    // ═══════════════════════════════════════════════════════════
    // 6. localize() 조합 동작
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `localize combines category and intensity with dash`() {
        val result = localizer.localize(
            intensityKey = "DANGER",
            categoryKey = "SCAM_RISK_HIGH",
            language = SupportedLanguage.KO,
        )
        assertEquals("사기/피싱 위험 — 수신 위험", result)
    }

    @Test
    fun `localize EN with entity`() {
        val result = localizer.localize(
            intensityKey = "SAFE",
            categoryKey = "INSTITUTION_LIKELY",
            language = SupportedLanguage.EN,
            entityName = "IRS",
        )
        assertEquals("IRS Institution Call — Safe to Answer", result)
    }

    @Test
    fun `localize JA delivery with verify`() {
        val result = localizer.localize(
            intensityKey = "VERIFY",
            categoryKey = "DELIVERY_LIKELY",
            language = SupportedLanguage.JA,
            entityName = "ヤマト運輸",
        )
        assertTrue(result.contains("ヤマト運輸"))
        assertTrue(result.contains("配送確認推奨"))
    }
}
