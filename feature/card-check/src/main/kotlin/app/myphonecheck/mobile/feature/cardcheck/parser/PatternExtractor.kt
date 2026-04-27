package app.myphonecheck.mobile.feature.cardcheck.parser

import java.util.Currency
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 글로벌 파싱 엔진 — 패턴 추출 (Architecture v1.9.0 §27-3-2).
 *
 * 카드사·국가·언어 무관. ICU·Unicode 표준 기반.
 *
 * 카드사·국가 분기 0:
 *  - 통화 식별: ISO 4217 3-letter 대문자 또는 글로벌 통화 기호 매핑 (Unicode \p{Sc} 보편)
 *  - 통화별 소수 자릿수: ICU Currency.defaultFractionDigits (JPY=0, USD=2, BHD=3 자동)
 *  - 카드 식별자: 4자리 끝번호 (\d{4}) + 보편 어휘 (끝자리/ending/Card)
 *  - 가맹점: 대문자 토큰 / 따옴표 안 텍스트
 *  - 시각: SMS 수신 시각(fallback). 본문 datetime 파싱은 Stage 2+
 *
 * 시드 데이터 0: 카드사 목록·국가 목록 사전 정의 안 함.
 *
 * 통화 기호 매핑 (CURRENCY_SYMBOL_TO_ISO)은 **Unicode 표준 통화 기호와 ISO 4217 코드의 일관 매핑**으로,
 * 특정 카드사·국가별 분기가 아니다. ICU Currency 표준 별칭을 활용하여 동일 효과를 기대 가능하나
 * 명시적 매핑 표가 더 안정적이다.
 */
@Singleton
class PatternExtractor @Inject constructor() {

    /** 통화 + 금액 추출. prefix·suffix 양쪽 패턴 시도. */
    fun extractCurrencyAmount(text: String): CurrencyAmount? {
        // 1) 통화 prefix + 금액 (예: "$25.50", "USD 25.50", "₩50,000", "BHD 12.500")
        val pre = PREFIX_PATTERN.matcher(text)
        if (pre.find()) {
            val currencyToken = pre.group(1) ?: ""
            val amountToken = pre.group(2) ?: ""
            buildAmount(currencyToken, amountToken)?.let { return it }
        }
        // 2) 금액 + 통화 suffix (예: "50,000원", "15,75 €", "25.50 USD")
        val suf = SUFFIX_PATTERN.matcher(text)
        if (suf.find()) {
            val amountToken = suf.group(1) ?: ""
            val currencyToken = suf.group(2) ?: ""
            buildAmount(currencyToken, amountToken)?.let { return it }
        }
        return null
    }

    /**
     * 거래 시각 — Stage 1: SMS 수신 시각(fallback). 본문 datetime 추출은 Stage 2+.
     */
    fun extractTimestamp(text: String, fallbackTime: Long): Long = fallbackTime

    /** 카드 끝자리 4자리 추출 (보편 어휘). */
    fun extractCardIdentifier(text: String): String? {
        for (pattern in CARD_LAST4_PATTERNS) {
            val m = pattern.matcher(text)
            if (m.find()) {
                return m.group(1)
            }
        }
        return null
    }

    /** 가맹점명 추출 (따옴표 우선, 그 다음 대문자 토큰). */
    fun extractMerchant(text: String): String? {
        val q = QUOTED_MERCHANT_PATTERN.matcher(text)
        if (q.find()) {
            val candidate = q.group(1)?.trim().orEmpty()
            if (candidate.length in 2..40) return candidate
        }
        val u = UPPERCASE_MERCHANT_PATTERN.matcher(text)
        while (u.find()) {
            val candidate = u.group()
            // 통화 ISO 코드와 충돌 방지
            if (candidate.length in 2..40 && !ISO_4217_RE.matches(candidate) &&
                !KNOWN_CURRENCY_TOKENS.contains(candidate)
            ) {
                return candidate
            }
        }
        return null
    }

    private fun buildAmount(currencyToken: String, amountToken: String): CurrencyAmount? {
        val isoCode = resolveIsoCode(currencyToken.trim()) ?: return null
        val minor = parseAmountToMinorUnits(amountToken.trim(), isoCode) ?: return null
        return CurrencyAmount(currencyCode = isoCode, minorUnits = minor)
    }

    /**
     * 통화 토큰 → ISO 4217 코드.
     *  - 3-letter 대문자: ICU Currency.getInstance() 검증
     *  - 통화 기호·단위 토큰: 글로벌 매핑 표
     */
    private fun resolveIsoCode(token: String): String? {
        if (token.isEmpty()) return null
        if (ISO_4217_RE.matches(token)) {
            return runCatching { Currency.getInstance(token).currencyCode }.getOrNull()
        }
        return CURRENCY_SYMBOL_TO_ISO[token]
    }

    /**
     * 금액 문자열 → 통화별 최소 단위 정수.
     *
     * 알고리즘:
     *  1. ICU Currency.defaultFractionDigits 조회 (USD=2, KRW=0, JPY=0, BHD=3, ...)
     *  2. 마지막 . 또는 , 위치 식별
     *  3. 마지막 구분자 다음 자릿수가 fractionDigits와 일치 → 소수점 분리
     *  4. 그렇지 않으면 모두 정수부 (그룹 구분자만 사용)
     *  5. 정수화 (× 10^fractionDigits)
     */
    private fun parseAmountToMinorUnits(raw: String, isoCode: String): Long? {
        val fractionDigits = runCatching {
            Currency.getInstance(isoCode).defaultFractionDigits
        }.getOrDefault(2).coerceAtLeast(0)

        val cleaned = raw
            .replace(' ', ' ') // non-breaking space
            .replace(' ', ' ') // narrow no-break space

        // 구분자 없음 (정수만)
        if (!cleaned.contains('.') && !cleaned.contains(',')) {
            val digits = cleaned.replace(" ", "")
            if (digits.isEmpty() || !digits.all { it.isDigit() }) return null
            return digits.toLong() * pow10(fractionDigits)
        }

        val lastDot = cleaned.lastIndexOf('.')
        val lastComma = cleaned.lastIndexOf(',')
        val lastSepIdx = maxOf(lastDot, lastComma)
        val tail = cleaned.substring(lastSepIdx + 1).filter { it.isDigit() }

        // 마지막 구분자 다음 자릿수가 fractionDigits와 일치 → 소수점 분리
        // fractionDigits == 0 (JPY/KRW)일 때는 그룹 구분자만 가능
        return if (fractionDigits > 0 && tail.length == fractionDigits) {
            val intPartRaw = cleaned.substring(0, lastSepIdx)
            val intPart = intPartRaw.replace(" ", "").replace(".", "").replace(",", "")
            if (intPart.isEmpty() || !intPart.all { it.isDigit() }) return null
            val whole = intPart.toLong()
            val frac = tail.padEnd(fractionDigits, '0').take(fractionDigits).toLong()
            whole * pow10(fractionDigits) + frac
        } else {
            // 모두 정수부 (그룹 구분자만 사용)
            val digits = cleaned.replace(" ", "").replace(".", "").replace(",", "")
            if (digits.isEmpty() || !digits.all { it.isDigit() }) return null
            digits.toLong() * pow10(fractionDigits)
        }
    }

    private fun pow10(n: Int): Long {
        var result = 1L
        repeat(n) { result *= 10 }
        return result
    }

    companion object {
        private val ISO_4217_RE = Regex("^[A-Z]{3}\$")

        // 글로벌 통화 기호·단위 토큰 → ISO 4217.
        // Unicode 표준 통화 기호 + 보편 별칭. 카드사·국가 분기가 아닌 통화 자체 매핑.
        private val CURRENCY_SYMBOL_TO_ISO = mapOf(
            "$" to "USD", "US$" to "USD",
            "€" to "EUR",
            "£" to "GBP",
            "¥" to "JPY", "￥" to "JPY", "円" to "JPY",
            "₩" to "KRW", "원" to "KRW",
            "₹" to "INR", "Rs" to "INR", "Rs." to "INR",
            "Rp" to "IDR",
            "₪" to "ILS",
            "₺" to "TRY",
            "₽" to "RUB", "руб" to "RUB", "руб." to "RUB",
            "₫" to "VND",
            "₴" to "UAH",
            "₦" to "NGN",
            "฿" to "THB",
            "R$" to "BRL",
            "C$" to "CAD",
            "A$" to "AUD",
            "NZ$" to "NZD",
            "HK$" to "HKD",
            "S$" to "SGD",
            "CHF" to "CHF",
            "CNY" to "CNY", "RMB" to "CNY", "元" to "CNY",
        )

        private val KNOWN_CURRENCY_TOKENS = CURRENCY_SYMBOL_TO_ISO.keys

        // prefix 패턴: 통화 prefix + 공백? + 금액
        // 통화 토큰: 통화 기호(\p{Sc}) 1자, 한자(円元), 전각 ￥, 또는 ISO 4217 / 별칭
        // 금액 토큰: 숫자 + 그룹 구분자 (. , 공백    )
        private val PREFIX_PATTERN: Pattern = Pattern.compile(
            "(US\\\$|R\\\$|C\\\$|A\\\$|NZ\\\$|HK\\\$|S\\\$|Rs\\.?|Rp|RMB|CNY|CHF|руб\\.?|" +
                "[\\p{Sc}\\u5143\\u5186\\uFFE5]|[A-Z]{3})" +
                "\\s*" +
                "(\\d(?:[\\d\\.,\\u00A0\\u202F\\s]{0,20}\\d)?)"
        )

        // suffix 패턴: 금액 + 공백? + 통화 suffix
        // 통화 suffix: 통화 기호 + 한국어/일본어/중국어 단위 (원/円/元), ISO 4217
        private val SUFFIX_PATTERN: Pattern = Pattern.compile(
            "(\\d(?:[\\d\\.,\\u00A0\\u202F\\s]{0,20}\\d)?)" +
                "\\s*" +
                "(US\\\$|R\\\$|C\\\$|A\\\$|NZ\\\$|HK\\\$|S\\\$|Rs\\.?|Rp|RMB|CNY|CHF|руб\\.?|원|元|円|" +
                "[\\p{Sc}\\uFFE5]|[A-Z]{3})"
        )

        // 카드 끝자리 4자리 (보편 어휘)
        private val CARD_LAST4_PATTERNS: List<Pattern> = listOf(
            Pattern.compile("(?i)(?:끝자리|ending in|ending|card\\s+\\*+|\\*+)\\s*(\\d{4})"),
            Pattern.compile("(?:^|\\s)\\*+(\\d{4})\\b"),
            Pattern.compile("\\b(\\d{4})\\s*(?:끝자리)"),
        )

        // 따옴표 안 가맹점 (다국어 따옴표 지원)
        private val QUOTED_MERCHANT_PATTERN: Pattern = Pattern.compile(
            "[\\\"\\u201C\\u300E\\u00AB]([^\\\"\\u201D\\u300F\\u00BB]{2,40})[\\\"\\u201D\\u300F\\u00BB]"
        )

        // 대문자 가맹점 토큰 (보편)
        private val UPPERCASE_MERCHANT_PATTERN: Pattern = Pattern.compile(
            "\\b[A-Z][A-Z0-9]{1,40}\\b"
        )
    }
}
