package app.myphonecheck.mobile.core.globalengine.parsing.message

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SMS 패턴 특징 추출기 (Architecture v2.0.0 §22 + §30).
 *
 * Unicode 표준 패턴만 사용. 국가별 정규식 0 (헌법 §8조 SIM-Oriented Single Core).
 * SimContext.countryIso는 국가 힌트로만 노출 — 분류 자체는 패턴에 따른다.
 */
@Singleton
class SmsPatternExtractor @Inject constructor() {

    private val urlRegex = Regex("""https?://[^\s]+""", RegexOption.IGNORE_CASE)

    /** Unicode Currency Symbol(\p{Sc}) 다음 숫자 — 글로벌 통화 표기. */
    private val currencyRegex = Regex("""\p{Sc}\s*\d""")

    /** ISO 4217 3-letter code (KRW/USD/EUR/...) 다음 숫자 — 코드형 통화 표기. */
    private val currencyCodeRegex = Regex("""\b[A-Z]{3}\b\s*\d""")

    /** 3-6자리 숫자 발신자 — 글로벌 short code 패턴. */
    private val shortSenderRegex = Regex("""^\d{3,6}$""")

    fun extract(sender: String, body: String, simContext: SimContext): MessageFeatures {
        val urlMatches = urlRegex.findAll(body).toList()
        val hasCurrency = currencyRegex.containsMatchIn(body) ||
            currencyCodeRegex.containsMatchIn(body)
        return MessageFeatures(
            sender = sender,
            isShortSender = shortSenderRegex.matches(sender),
            hasUrl = urlMatches.isNotEmpty(),
            hasCurrencyPattern = hasCurrency,
            bodyLength = body.length,
            urlCount = urlMatches.size,
            countryHint = simContext.countryIso,
        )
    }
}
