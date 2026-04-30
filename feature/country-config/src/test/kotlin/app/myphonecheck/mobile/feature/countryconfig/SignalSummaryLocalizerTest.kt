package app.myphonecheck.mobile.feature.countryconfig

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * SignalSummaryLocalizer 단위 테스트 — 영문 단일 (헌법 §9-1).
 *
 * 다국어 expectation / Robolectric Locale switching 영구 금지.
 * 모든 출력은 영문 hardcoded.
 */
class SignalSummaryLocalizerTest {

    private lateinit var localizer: SignalSummaryLocalizer

    @Before
    fun setUp() {
        localizer = SignalSummaryLocalizer()
    }

    // ═══════════════════════════════════════════════════════════
    // 1. Intensity 로컬라이즈 — 영문 단일
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `intensity SAFE returns Safe to Answer`() {
        assertEquals("Safe to Answer", localizer.localizeIntensity("SAFE"))
    }

    @Test
    fun `intensity DANGER returns High Risk`() {
        assertEquals("High Risk", localizer.localizeIntensity("DANGER"))
    }

    @Test
    fun `intensity REJECT returns Reject Recommended`() {
        assertEquals("Reject Recommended", localizer.localizeIntensity("REJECT"))
    }

    @Test
    fun `intensity CAUTION returns Be Cautious`() {
        assertEquals("Be Cautious", localizer.localizeIntensity("CAUTION"))
    }

    @Test
    fun `intensity VERIFY returns Verify Recommended`() {
        assertEquals("Verify Recommended", localizer.localizeIntensity("VERIFY"))
    }

    @Test
    fun `all 7 intensity keys produce non-key output`() {
        val keys = listOf("SAFE", "REFERENCE", "CAUTION_LIGHT", "CAUTION", "DANGER", "REJECT", "VERIFY")
        for (key in keys) {
            val result = localizer.localizeIntensity(key)
            assertFalse(
                "Intensity $key should produce localized output (got key back)",
                result == key,
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 2. Category 로컬라이즈 — 영문 단일
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `category SCAM_RISK_HIGH returns Scam Phishing Risk`() {
        assertEquals("Scam/Phishing Risk", localizer.localizeCategory("SCAM_RISK_HIGH"))
    }

    @Test
    fun `category DELIVERY_LIKELY without entity`() {
        assertEquals("Delivery Call", localizer.localizeCategory("DELIVERY_LIKELY"))
    }

    @Test
    fun `all 7 category keys produce non-key output`() {
        val keys = listOf(
            "KNOWN_CONTACT", "BUSINESS_LIKELY", "DELIVERY_LIKELY",
            "INSTITUTION_LIKELY", "SALES_SPAM_SUSPECTED", "SCAM_RISK_HIGH",
            "INSUFFICIENT_EVIDENCE",
        )
        for (key in keys) {
            val result = localizer.localizeCategory(key)
            assertFalse(
                "Category $key should produce localized output (got key back)",
                result == key,
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 3. Entity 치환
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `category with entity includes entity name`() {
        val result = localizer.localizeCategory("INSTITUTION_LIKELY", "IRS")
        assertTrue(result.contains("IRS"))
    }

    @Test
    fun `category with multi-word entity includes full name`() {
        val result = localizer.localizeCategory("BUSINESS_LIKELY", "Samsung Electronics")
        assertTrue(result.contains("Samsung Electronics"))
    }

    // ═══════════════════════════════════════════════════════════
    // 4. Entity null 시 깔끔한 텍스트
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `category without entity removes placeholder cleanly`() {
        val result = localizer.localizeCategory("BUSINESS_LIKELY")
        assertFalse(result.contains("{entity}"))
        assertFalse(result.startsWith(" "))
    }

    @Test
    fun `category DELIVERY without entity has no extra spaces`() {
        val result = localizer.localizeCategory("DELIVERY_LIKELY")
        assertFalse(result.contains("{entity}"))
        assertFalse(result.startsWith(" "))
    }

    // ═══════════════════════════════════════════════════════════
    // 5. 미지원 키 → 키 그대로 반환
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `unknown intensity key returns key as-is`() {
        assertEquals("UNKNOWN_KEY", localizer.localizeIntensity("UNKNOWN_KEY"))
    }

    @Test
    fun `unknown category key returns key as-is`() {
        assertEquals("UNKNOWN_CATEGORY", localizer.localizeCategory("UNKNOWN_CATEGORY"))
    }

    // ═══════════════════════════════════════════════════════════
    // 6. localize() 조합 동작
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `localize combines category and intensity with em dash`() {
        val result = localizer.localize(
            intensityKey = "DANGER",
            categoryKey = "SCAM_RISK_HIGH",
        )
        assertEquals("Scam/Phishing Risk — High Risk", result)
    }

    @Test
    fun `localize with entity produces full sentence`() {
        val result = localizer.localize(
            intensityKey = "SAFE",
            categoryKey = "INSTITUTION_LIKELY",
            entityName = "IRS",
        )
        assertEquals("IRS Institution Call — Safe to Answer", result)
    }

    @Test
    fun `localize SALES_SPAM_SUSPECTED with CAUTION`() {
        val result = localizer.localize(
            intensityKey = "CAUTION",
            categoryKey = "SALES_SPAM_SUSPECTED",
        )
        assertEquals("Suspected Spam/Sales — Be Cautious", result)
    }

    @Test
    fun `localize INSUFFICIENT_EVIDENCE with REFERENCE`() {
        val result = localizer.localize(
            intensityKey = "REFERENCE",
            categoryKey = "INSUFFICIENT_EVIDENCE",
        )
        assertEquals("Insufficient Evidence — For Reference", result)
    }
}
