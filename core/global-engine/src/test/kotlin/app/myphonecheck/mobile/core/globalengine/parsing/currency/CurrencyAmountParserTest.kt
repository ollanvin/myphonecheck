package app.myphonecheck.mobile.core.globalengine.parsing.currency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * 글로벌 동작 보증 다양성 테스트 (Architecture v1.9.0 §27).
 *
 * 6 패턴 다양성:
 *  1. 소수 0자릿수 통화 (KRW, JPY) — 정수만, 그룹 구분자 콤마
 *  2. 소수 2자릿수 통화 (USD) — 통화 기호 prefix, 점 소수점
 *  3. 소수 3자릿수 통화 (BHD) — ISO 코드 prefix, 점 소수점
 *  4. 통화 기호 suffix (EUR, KRW) — 콤마 소수점, 단위 토큰
 *  5. ISO 4217 코드 prefix·suffix (USD, JPY) — 영문 SMS
 *  6. 한자 단위 토큰 (CNY 元, JPY 円) + RTL 통화 기호 (ILS ₪) — 다국어
 *
 * 카드사·국가 분기 0 검증: 위 6 패턴이 동일 CurrencyAmountParser (구 PatternExtractor) 로 모두 처리됨.
 * Stage 2-001 마이그레이션: 본 테스트는 :feature:card-check/test/PatternExtractorTest.kt → 코어 이전 (회귀 0).
 */
class CurrencyAmountParserTest {

    private val extractor = CurrencyAmountParser()

    // ===== 1. 소수 0자릿수: KRW, JPY =====

    @Test
    fun `KRW with comma grouping (0 decimals) - suffix 단위 원`() {
        val text = "결제 50,000원 GS25 매장에서"
        val result = extractor.extractCurrencyAmount(text)
        assertNotNull("KRW 50,000원 should parse", result)
        assertEquals("KRW", result?.currencyCode)
        assertEquals(50_000L, result?.minorUnits)
    }

    @Test
    fun `JPY with comma grouping (0 decimals) - prefix 통화기호 ¥`() {
        val text = "決済 ¥3,000 セブン"
        val result = extractor.extractCurrencyAmount(text)
        assertNotNull("JPY ¥3,000 should parse", result)
        assertEquals("JPY", result?.currencyCode)
        assertEquals(3_000L, result?.minorUnits)
    }

    // ===== 2. 소수 2자릿수: USD =====

    @Test
    fun `USD with point decimal (2 decimals) - prefix 통화기호 $`() {
        val text = "VISA \$25.50 Starbucks ending in 1234"
        val result = extractor.extractCurrencyAmount(text)
        assertNotNull("USD \$25.50 should parse", result)
        assertEquals("USD", result?.currencyCode)
        assertEquals(2_550L, result?.minorUnits) // cents
    }

    // ===== 3. 소수 3자릿수: BHD =====

    @Test
    fun `BHD with point decimal (3 decimals) - prefix ISO 4217 코드`() {
        val text = "Bahrain Bank BHD 12.500 LULU"
        val result = extractor.extractCurrencyAmount(text)
        assertNotNull("BHD 12.500 should parse", result)
        assertEquals("BHD", result?.currencyCode)
        assertEquals(12_500L, result?.minorUnits) // 3 decimals: 12.500 = 12500 fils
    }

    // ===== 4. 통화 기호 suffix + 콤마 소수점: EUR =====

    @Test
    fun `EUR with comma decimal (2 decimals) - suffix 통화기호 €`() {
        val text = "Sparkasse 15,75 € REWE"
        val result = extractor.extractCurrencyAmount(text)
        assertNotNull("EUR 15,75 € should parse", result)
        assertEquals("EUR", result?.currencyCode)
        assertEquals(1_575L, result?.minorUnits) // cents
    }

    // ===== 5. ISO 4217 코드 표기: 영문 SMS =====

    @Test
    fun `USD with ISO 4217 code suffix - 영문 SMS`() {
        val text = "Payment 100.00 USD at Amazon"
        val result = extractor.extractCurrencyAmount(text)
        assertNotNull("100.00 USD should parse", result)
        assertEquals("USD", result?.currencyCode)
        assertEquals(10_000L, result?.minorUnits)
    }

    // ===== 6. 한자 단위 토큰 + RTL 통화 =====

    @Test
    fun `CNY with 元 한자 단위 토큰 (0 decimals fallback for grouping)`() {
        val text = "招商银行 1,234元 京东购物"
        val result = extractor.extractCurrencyAmount(text)
        assertNotNull("CNY 1,234元 should parse", result)
        assertEquals("CNY", result?.currencyCode)
        // CNY는 ICU에서 2 decimals이므로 1,234 → 123,400 fen (소수 일치 안 함, 그룹 구분자만)
        assertEquals(123_400L, result?.minorUnits)
    }

    @Test
    fun `ILS with ₪ RTL 통화 기호`() {
        // ₪ (Sheqel sign) is a Sc (Currency Symbol) in Unicode. RTL context.
        val text = "Bank Hapoalim 250.00 ₪ at SHUFERSAL"
        val result = extractor.extractCurrencyAmount(text)
        assertNotNull("ILS 250.00 ₪ should parse", result)
        assertEquals("ILS", result?.currencyCode)
        assertEquals(25_000L, result?.minorUnits)
    }

    // ===== 카드 식별자 추출 (글로벌 보편 어휘) =====

    @Test
    fun `card last4 - 한국어 끝자리`() {
        val text = "신한카드 끝자리 1234"
        val result = extractor.extractCardIdentifier(text)
        assertEquals("1234", result)
    }

    @Test
    fun `card last4 - 영문 ending in`() {
        val text = "Card ending in 5678"
        val result = extractor.extractCardIdentifier(text)
        assertEquals("5678", result)
    }

    @Test
    fun `card last4 - asterisk masking`() {
        val text = "VISA ****9012 paid"
        val result = extractor.extractCardIdentifier(text)
        assertEquals("9012", result)
    }

    // ===== 가맹점 추출 =====

    @Test
    fun `merchant - uppercase token`() {
        val text = "VISA \$50.00 STARBUCKS"
        val result = extractor.extractMerchant(text)
        // 첫 발견 대문자 토큰을 반환 (VISA가 ISO 4217은 아니므로 통과)
        assertNotNull(result)
    }
}
