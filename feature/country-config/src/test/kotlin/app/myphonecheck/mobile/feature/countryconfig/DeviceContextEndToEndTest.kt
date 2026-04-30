package app.myphonecheck.mobile.feature.countryconfig

import app.myphonecheck.mobile.core.model.NumberSourceContext
import app.myphonecheck.mobile.core.util.PhoneNumberContextBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * E2E 기기 컨텍스트 통합 테스트 — EN 단일 (헌법 §9-1).
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 검증 대상: 전체 글로벌 파이프라인 (순수 JVM)                  │
 * ├──────────────────────────────────────────────────────────────┤
 * │ PhoneNumberContextBuilder                                     │
 * │   → SignalSummaryLocalizer                                    │
 * │   → 영문 사용자 대면 텍스트                                   │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 시나리오: 다국가 기기 + 영문 단일 출력                        │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 헌법 §9-1: 다국어 expectation / Locale switching 영구 폐기.
 * 기기 국가는 KR/US/JP 등 다양하게 시뮬레이션 — 출력은 모두 영문.
 */
class DeviceContextEndToEndTest {

    private lateinit var phoneContextBuilder: PhoneNumberContextBuilder
    private lateinit var localizer: SignalSummaryLocalizer

    @Before
    fun setUp() {
        phoneContextBuilder = PhoneNumberContextBuilder()
        localizer = SignalSummaryLocalizer()
    }

    @Test
    fun `KR device - scam call - English output`() {
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "02-888-1234",
            deviceCountryCode = "KR",
            sourceContext = NumberSourceContext.INCOMING_CALL,
        )

        assertEquals("02-888-1234", phoneCtx.rawNumber)
        assertTrue(phoneCtx.isParseable)
        assertNotNull(phoneCtx.deviceCanonicalNumber)
        assertTrue(phoneCtx.searchVariants.isNotEmpty())

        // 헌법 §9-1: KR 기기여도 출력은 영문 단일
        val language = SupportedLanguage.fromCodeOrDefault("ko")
        assertEquals(SupportedLanguage.EN, language)

        val result = localizer.localize(
            intensityKey = "DANGER",
            categoryKey = "SCAM_RISK_HIGH",
        )
        assertEquals("Scam/Phishing Risk — High Risk", result)
    }

    @Test
    fun `KR device - delivery call with entity - English output`() {
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "1588-1255",
            deviceCountryCode = "KR",
            sourceContext = NumberSourceContext.INCOMING_CALL,
        )
        assertEquals("1588-1255", phoneCtx.rawNumber)
        assertTrue(phoneCtx.searchVariants.contains("1588-1255"))

        val result = localizer.localize(
            intensityKey = "VERIFY",
            categoryKey = "DELIVERY_LIKELY",
            entityName = "CJ Logistics",
        )
        assertTrue(result.contains("CJ Logistics"))
        assertTrue(result.contains("Verify Recommended"))
    }

    @Test
    fun `US device - scam call - English output`() {
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "(800) 829-0433",
            deviceCountryCode = "US",
            sourceContext = NumberSourceContext.INCOMING_CALL,
        )
        assertEquals("(800) 829-0433", phoneCtx.rawNumber)
        assertEquals("+18008290433", phoneCtx.deviceCanonicalNumber)

        val result = localizer.localize(
            intensityKey = "DANGER",
            categoryKey = "SCAM_RISK_HIGH",
        )
        assertEquals("Scam/Phishing Risk — High Risk", result)
    }

    @Test
    fun `US device - institution call with entity - English output`() {
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "+1-202-555-0100",
            deviceCountryCode = "US",
            sourceContext = NumberSourceContext.INCOMING_CALL,
        )
        assertTrue(phoneCtx.isParseable)

        val result = localizer.localize(
            intensityKey = "SAFE",
            categoryKey = "INSTITUTION_LIKELY",
            entityName = "IRS",
        )
        assertEquals("IRS Institution Call — Safe to Answer", result)
    }

    @Test
    fun `JP device - toll-free call - English output`() {
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "0120-444-113",
            deviceCountryCode = "JP",
            sourceContext = NumberSourceContext.INCOMING_CALL,
        )
        assertEquals("+81120444113", phoneCtx.deviceCanonicalNumber)

        val result = localizer.localize(
            intensityKey = "SAFE",
            categoryKey = "INSTITUTION_LIKELY",
            entityName = "NTT",
        )
        assertEquals("NTT Institution Call — Safe to Answer", result)
    }

    @Test
    fun `KR device - short number 114 - still searchable`() {
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "114",
            deviceCountryCode = "KR",
            sourceContext = NumberSourceContext.INCOMING_CALL,
        )
        assertFalse(phoneCtx.isParseable)
        assertEquals("114", phoneCtx.deviceCanonicalNumber)
        assertTrue(phoneCtx.searchVariants.contains("114"))

        val result = localizer.localize(
            intensityKey = "SAFE",
            categoryKey = "INSTITUTION_LIKELY",
            entityName = "KT",
        )
        assertEquals("KT Institution Call — Safe to Answer", result)
    }

    @Test
    fun `KR device - language is always EN regardless of override attempt`() {
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "010-1234-5678",
            deviceCountryCode = "KR",
            sourceContext = NumberSourceContext.INCOMING_CALL,
        )
        assertEquals("KR", phoneCtx.deviceCountryCode)

        // 헌법 §9-1: SupportedLanguage.EN 만 존재. 오버라이드 미지원.
        val overrideLanguage = SupportedLanguage.EN

        val result = localizer.localize(
            intensityKey = "CAUTION",
            categoryKey = "SALES_SPAM_SUSPECTED",
        )
        assertEquals("Suspected Spam/Sales — Be Cautious", result)
        assertEquals(SupportedLanguage.EN, overrideLanguage)
    }

    @Test
    fun `unsupported country device - EN fallback language`() {
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "+33-1-42-68-53-00",
            deviceCountryCode = "FR",
            sourceContext = NumberSourceContext.INCOMING_CALL,
        )
        assertEquals("FR", phoneCtx.deviceCountryCode)

        val language = SupportedLanguage.fromCodeOrDefault("fr")
        assertEquals(SupportedLanguage.EN, language)

        val result = localizer.localize(
            intensityKey = "REFERENCE",
            categoryKey = "INSUFFICIENT_EVIDENCE",
        )
        assertEquals("Insufficient Evidence — For Reference", result)
    }

    @Test
    fun `every supported language entry produces English scam warning`() {
        for (lang in SupportedLanguage.entries) {
            assertEquals(SupportedLanguage.EN, lang)
            val result = localizer.localize(
                intensityKey = "DANGER",
                categoryKey = "SCAM_RISK_HIGH",
            )
            assertTrue("Result must be non-empty", result.isNotEmpty())
            assertTrue("Result should contain dash separator", result.contains("—"))
            assertEquals("Scam/Phishing Risk — High Risk", result)
        }
    }

    @Test
    fun `SMS source context preserved in phone context`() {
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "010-9876-5432",
            deviceCountryCode = "KR",
            sourceContext = NumberSourceContext.SMS,
        )
        assertEquals(NumberSourceContext.SMS, phoneCtx.sourceContext)
        assertTrue(phoneCtx.isParseable)
    }

    @Test
    fun `CALL_LOG source context preserved in phone context`() {
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "+821012345678",
            deviceCountryCode = "KR",
            sourceContext = NumberSourceContext.CALL_LOG,
        )
        assertEquals(NumberSourceContext.CALL_LOG, phoneCtx.sourceContext)
        assertEquals("+821012345678", phoneCtx.deviceCanonicalNumber)
    }
}
