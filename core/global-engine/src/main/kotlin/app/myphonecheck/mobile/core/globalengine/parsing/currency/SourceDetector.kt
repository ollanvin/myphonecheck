package app.myphonecheck.mobile.core.globalengine.parsing.currency

import app.myphonecheck.mobile.core.globalengine.parsing.currency.learning.SourceLabelCache
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 글로벌 파싱 엔진 — 소스 식별 (Architecture v2.0.0 §27-3-1 + §30 :core:global-engine).
 *
 * Stage 2-001 마이그레이션: :feature:card-check/parser/SourceDetector.kt → 본 위치 (기능 동일).
 * extractor 타입 PatternExtractor → CurrencyAmountParser (Stage 2-001 클래스명 정정).
 *
 * 발신자별 결제 패턴 점수화 + 사용자 라벨 캐시.
 * 시드 데이터 0 — 모든 라벨은 사용자 확인 결과로만 형성.
 */
@Singleton
class SourceDetector @Inject constructor(
    private val labelCache: SourceLabelCache,
    private val extractor: CurrencyAmountParser,
) {

    suspend fun classify(sourceId: String, body: String): ClassificationResult {
        val knownLabel = labelCache.find(sourceId)
        if (knownLabel != null) {
            return ClassificationResult.Known(knownLabel)
        }

        val score = computeScore(body)
        return when {
            score >= HIGH_THRESHOLD -> ClassificationResult.Suspect(score)
            score >= MEDIUM_THRESHOLD -> ClassificationResult.Suspect(score)
            else -> ClassificationResult.Unknown
        }
    }

    private fun computeScore(body: String): Int {
        var score = 0
        if (extractor.extractCurrencyAmount(body) != null) score += 50
        if (extractor.extractCardIdentifier(body) != null) score += 30
        if (extractor.extractMerchant(body) != null) score += 20
        return score
    }

    companion object {
        const val HIGH_THRESHOLD = 70
        const val MEDIUM_THRESHOLD = 50
    }
}

sealed class ClassificationResult {
    data class Known(val label: String) : ClassificationResult()
    data class Suspect(val score: Int) : ClassificationResult()
    object Unknown : ClassificationResult()
}
