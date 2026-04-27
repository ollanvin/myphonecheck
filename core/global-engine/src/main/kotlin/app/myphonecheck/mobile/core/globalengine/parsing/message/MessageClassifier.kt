package app.myphonecheck.mobile.core.globalengine.parsing.message

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 메시지 분류기 (Architecture v2.0.0 §22 + §30).
 *
 * 규칙 기반 (Stage 2-003) — ML/학습은 후속 Stage에서 검토.
 * 분류 결과는 사용자 라벨링 prompt 기준 — 결정권 중앙집중 금지 (헌법 §3).
 */
@Singleton
class MessageClassifier @Inject constructor() {

    fun classify(features: MessageFeatures): MessageCategory {
        return when {
            features.hasCurrencyPattern && features.isShortSender ->
                MessageCategory.PAYMENT_CANDIDATE
            features.hasUrl && features.bodyLength < SHORT_BODY_THRESHOLD ->
                MessageCategory.SPAM_CANDIDATE
            features.urlCount >= MULTI_URL_SPAM_THRESHOLD ->
                MessageCategory.SPAM_CANDIDATE
            features.isShortSender ->
                MessageCategory.NOTIFICATION
            else ->
                MessageCategory.NORMAL
        }
    }

    companion object {
        /** URL 포함 + 본문이 짧으면 스팸 후보 — 양식 단순 메시지 패턴. */
        const val SHORT_BODY_THRESHOLD = 100

        /** URL 다수 포함은 발신자와 무관하게 스팸 후보. */
        const val MULTI_URL_SPAM_THRESHOLD = 2
    }
}
