package app.myphonecheck.mobile.feature.cardcheck.parser

/**
 * 글로벌 파싱 엔진 출력 (Architecture v1.9.0 §27-3).
 *
 * 파이프라인: SourceDetector → PatternExtractor → Validator.
 * 카드사·국가 분기 0. 시드 데이터 0.
 */
data class CardParseResult(
    val sourceId: String,
    val amount: CurrencyAmount?,
    val timestamp: Long?,
    val cardIdentifier: String?,
    val merchant: String?,
    val source: TransactionSource,
)

data class CurrencyAmount(
    val currencyCode: String,  // ISO 4217 (USD, KRW, JPY, EUR, BHD, ...)
    val minorUnits: Long,      // 통화별 최소 단위 정수 (cents, won, fils, ...)
)

enum class TransactionSource { SMS, NOTIFICATION }

enum class Confidence { HIGH, MEDIUM, LOW }
