package app.myphonecheck.mobile.feature.messagecheck.service

import app.myphonecheck.mobile.core.globalengine.parsing.message.MessageCategory
import app.myphonecheck.mobile.core.globalengine.parsing.message.MessageClassifier
import app.myphonecheck.mobile.core.globalengine.parsing.message.MessageParseResult
import app.myphonecheck.mobile.core.globalengine.parsing.message.SmsPatternExtractor
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 단일 메시지 스팸 후보 분석 (Architecture v2.0.0 §22).
 *
 * 코어 SmsPatternExtractor + MessageClassifier만 사용 — 자체 정규식 0.
 * 결정권 중앙집중 금지 (헌법 §3) — 본 서비스는 후보 노출만, 차단·삭제는 사용자.
 */
@Singleton
class SpamDetectionService @Inject constructor(
    private val patternExtractor: SmsPatternExtractor,
    private val classifier: MessageClassifier,
    private val simContextProvider: SimContextProvider,
) {

    fun analyze(
        sender: String,
        body: String,
        simContext: SimContext = simContextProvider.resolve(),
    ): MessageParseResult {
        val features = patternExtractor.extract(sender, body, simContext)
        val category = classifier.classify(features)
        return MessageParseResult(features, category)
    }

    fun isSpamCandidate(sender: String, body: String): Boolean {
        return analyze(sender, body).category == MessageCategory.SPAM_CANDIDATE
    }
}
