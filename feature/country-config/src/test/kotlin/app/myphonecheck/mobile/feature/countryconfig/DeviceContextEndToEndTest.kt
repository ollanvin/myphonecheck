package app.myphonecheck.mobile.feature.countryconfig

import app.myphonecheck.mobile.core.model.NumberSourceContext
import app.myphonecheck.mobile.core.util.PhoneNumberContextBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * E2E 기기 컨텍스트 통합 테스트.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 검증 대상: 전체 글로벌 파이프라인 (순수 JVM)                  │
 * ├──────────────────────────────────────────────────────────────┤
 * │ PhoneNumberContextBuilder                                     │
 * │   → (검색 생략, searchVariants 확인)                          │
 * │   → SignalSummaryLocalizer                                    │
 * │   → 최종 사용자 대면 텍스트                                   │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 시나리오별 검증:                                               │
 * │ 1. KR 기기 + KR 번호 + KO 언어 → 한국어 출력                │
 * │ 2. US 기기 + US 번호 + EN 언어 → 영어 출력                  │
 * │ 3. JP 기기 + JP 번호 + JA 언어 → 일본어 출력                │
 * │ 4. KR 기기 + 짧은 번호(114) + KO 언어                       │
 * │ 5. KR 기기 + KR 번호 + EN 오버라이드 → 영어 출력            │
 * │ 6. 미지원 국가 기기 + EN 폴백                                │
 * │ 7. 전체 7개 언어 × SCAM 시나리오 커버리지                    │
 * └──────────────────────────────────────────────────────────────┘
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DeviceContextEndToEndTest {

    private lateinit var phoneContextBuilder: PhoneNumberContextBuilder
    private lateinit var localizer: SignalSummaryLocalizer

    @Before
    fun setUp() {
        phoneContextBuilder = PhoneNumberContextBuilder()
        localizer = SignalSummaryLocalizer()
    }

    // ═══════════════════════════════════════════════════════════
    // 시나리오 1: KR 기기 + KR 번호 + KO 언어
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KR device - scam call - Korean output`() {
        // 1. 번호 문맥 구성
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "02-888-1234",
            deviceCountryCode = "KR",
            sourceContext = NumberSourceContext.INCOMING_CALL,
        )

        // 번호 문맥 검증
        assertEquals("02-888-1234", phoneCtx.rawNumber)
        assertTrue(phoneCtx.isParseable)
        assertNotNull(phoneCtx.deviceCanonicalNumber)
        assertTrue(phoneCtx.searchVariants.isNotEmpty())

        // 2. 언어 결정 (시뮬레이션: KR 기기 → KO)
        val language = SupportedLanguage.fromCodeOrDefault("ko")
        assertEquals(SupportedLanguage.KO, language)

        // 3. 로컬라이즈
        val result = localizer.localize(
            intensityKey = "DANGER",
            categoryKey = "SCAM_RISK_HIGH",
            context = contextForLanguage(language),
        )
        assertEquals("사기/피싱 위험 — 수신 위험", result)
    }

    @Test
    fun `KR device - delivery call with entity - Korean output`() {
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
            context = contextForLanguage(SupportedLanguage.KO),
            entityName = "CJ대한통운",
        )
        assertTrue(result.contains("CJ대한통운"))
        assertTrue(result.contains("배송 확인 권장"))
    }

    // ═══════════════════════════════════════════════════════════
    // 시나리오 2: US 기기 + US 번호 + EN 언어
    // ═══════════════════════════════════════════════════════════

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
            context = contextForLanguage(SupportedLanguage.EN),
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
            context = contextForLanguage(SupportedLanguage.EN),
            entityName = "IRS",
        )
        assertEquals("IRS Institution Call — Safe to Answer", result)
    }

    // ═══════════════════════════════════════════════════════════
    // 시나리오 3: JP 기기 + JP 번호 + JA 언어
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `JP device - toll-free call - Japanese output`() {
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "0120-444-113",
            deviceCountryCode = "JP",
            sourceContext = NumberSourceContext.INCOMING_CALL,
        )
        assertEquals("+81120444113", phoneCtx.deviceCanonicalNumber)

        val result = localizer.localize(
            intensityKey = "SAFE",
            categoryKey = "INSTITUTION_LIKELY",
            context = contextForLanguage(SupportedLanguage.JA),
            entityName = "NTT",
        )
        assertTrue(result.contains("NTT"))
        assertTrue(result.contains("安全"))
    }

    // ═══════════════════════════════════════════════════════════
    // 시나리오 4: KR 기기 + 짧은 번호 (114)
    // ═══════════════════════════════════════════════════════════

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

        // 짧은 번호도 로컬라이즈는 동일하게 동작
        val result = localizer.localize(
            intensityKey = "SAFE",
            categoryKey = "INSTITUTION_LIKELY",
            context = contextForLanguage(SupportedLanguage.KO),
            entityName = "KT",
        )
        assertTrue(result.contains("KT"))
        assertTrue(result.contains("수신 안전"))
    }

    // ═══════════════════════════════════════════════════════════
    // 시나리오 5: KR 기기 + EN 오버라이드
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KR device - language override to EN - English output`() {
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "010-1234-5678",
            deviceCountryCode = "KR",
            sourceContext = NumberSourceContext.INCOMING_CALL,
        )
        assertEquals("KR", phoneCtx.deviceCountryCode)

        // 사용자가 딥 설정에서 EN으로 오버라이드한 경우
        val overrideLanguage = SupportedLanguage.EN

        val result = localizer.localize(
            intensityKey = "CAUTION",
            categoryKey = "SALES_SPAM_SUSPECTED",
            context = contextForLanguage(overrideLanguage),
        )
        assertEquals("Suspected Spam/Sales — Be Cautious", result)
    }

    // ═══════════════════════════════════════════════════════════
    // 시나리오 6: 미지원 국가 기기 + EN 폴백
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `unsupported country device - EN fallback language`() {
        val phoneCtx = phoneContextBuilder.build(
            rawNumber = "+33-1-42-68-53-00",
            deviceCountryCode = "FR",
            sourceContext = NumberSourceContext.INCOMING_CALL,
        )
        assertEquals("FR", phoneCtx.deviceCountryCode)

        // FR은 SupportedLanguage에 없으므로 EN 폴백
        val language = SupportedLanguage.fromCodeOrDefault("fr")
        assertEquals(SupportedLanguage.EN, language)

        val result = localizer.localize(
            intensityKey = "REFERENCE",
            categoryKey = "INSUFFICIENT_EVIDENCE",
            context = contextForLanguage(language),
        )
        assertEquals("Insufficient Evidence — For Reference", result)
    }

    // ═══════════════════════════════════════════════════════════
    // 시나리오 7: 전체 7개 언어 × SCAM 시나리오
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `all 7 languages produce non-empty scam warning`() {
        for (lang in SupportedLanguage.entries) {
            val result = localizer.localize(
                intensityKey = "DANGER",
                categoryKey = "SCAM_RISK_HIGH",
                context = contextForLanguage(lang),
            )
            assertTrue(
                "Language ${lang.code} should produce non-empty scam warning",
                result.isNotEmpty()
            )
            assertTrue(
                "Language ${lang.code} result should contain dash separator",
                result.contains("—")
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 시나리오 8: 다양한 sourceContext
    // ═══════════════════════════════════════════════════════════

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
