package app.myphonecheck.mobile.core.globalengine.parsing.currency

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 글로벌 파싱 엔진 — 검증·정규화 (Architecture v2.0.0 §27-3-3 + §30 :core:global-engine).
 *
 * Stage 2-001 마이그레이션: :feature:card-check/parser/Validator.kt → 본 위치 (기능 동일).
 * 클래스명 Validator → CurrencyValidator (의미 명확화).
 *
 * 신뢰도 점수:
 *  - HIGH: 4 필드 모두 추출 (amount, timestamp, cardIdentifier, merchant)
 *  - MEDIUM: 2~3 필드 추출 + amount 필수
 *  - LOW: amount만 또는 일부 추출 (사용자 추가 확인 권장)
 */
@Singleton
class CurrencyValidator @Inject constructor() {

    fun validate(parsed: CardParseResult): Confidence {
        val hasAmount = parsed.amount != null
        if (!hasAmount) return Confidence.LOW

        val auxFieldsExtracted = listOf(
            parsed.timestamp != null,
            parsed.cardIdentifier != null,
            parsed.merchant != null,
        ).count { it }

        return when (auxFieldsExtracted) {
            3 -> Confidence.HIGH
            2 -> Confidence.MEDIUM
            else -> if (auxFieldsExtracted >= 1) Confidence.MEDIUM else Confidence.LOW
        }
    }
}
