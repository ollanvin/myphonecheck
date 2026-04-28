package app.myphonecheck.mobile.core.globalengine.decision

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real-time Action Engine (Architecture v2.1.0 §31).
 *
 * 수신 이벤트(Call/SMS/Push) 도착 시 50ms 이내 결정 산출.
 * 우선순위: Layer 2 차단 > Layer 2 태그(SUSPICIOUS) > Layer 2 태그(기타) > Layer 2 라벨 > Pass.
 *
 * Layer 3 통합은 후속 PR — 본 PR은 Layer 2(BlockList + Tag) 즉시 응답.
 *
 * 헌법 정합:
 *  - §1 Out-Bound Zero: 디바이스 로컬만.
 *  - §2 In-Bound Zero: 식별자(E.164/sender/packageName)만 lookup.
 *  - §3 결정 중앙집중 금지: 사용자 차단 목록·태그가 결정 입력.
 *  - §4 자가 작동: 네트워크 단절 시에도 Layer 2 캐시로 동작.
 *  - §8 SIM-Oriented: 호출 측에서 SimContext 정규화 후 E.164 전달.
 */
@Singleton
class RealTimeActionEngine @Inject constructor(
    private val blockList: BlockListRepository,
    private val tagRepo: TagRepository,
) {

    suspend fun decideForCall(e164: String): ActionDecision =
        decideWithTimeout(e164, IdentifierType.PHONE_E164, CALL_TIMEOUT_MILLIS)

    suspend fun decideForSms(sender: String): ActionDecision =
        decideWithTimeout(sender, IdentifierType.SMS_SENDER, SMS_TIMEOUT_MILLIS)

    suspend fun decideForNotification(packageName: String): ActionDecision =
        decideWithTimeout(packageName, IdentifierType.NOTIFICATION_PACKAGE, NOTIFICATION_TIMEOUT_MILLIS)

    private suspend fun decideWithTimeout(
        key: String,
        type: IdentifierType,
        timeoutMillis: Long,
    ): ActionDecision {
        val result = withTimeoutOrNull(timeoutMillis) { decideAggregated(key, type) }
        // Timeout 시 PASS — OS 기본 동작이 가장 안전 (헌법 §3 정합).
        return result ?: ActionDecision(
            action = ActionType.PASS,
            matchedSource = MatchedSource.NONE,
            tag = null,
            confidence = ActionConfidence.LOW,
        )
    }

    private suspend fun decideAggregated(key: String, type: IdentifierType): ActionDecision = coroutineScope {
        val blockedJob = async { blockList.isBlocked(key, type) }
        val tagJob = async { tagRepo.findByKey(key, type) }

        val blocked = blockedJob.await()
        if (blocked) {
            return@coroutineScope ActionDecision(
                action = ActionType.BLOCK,
                matchedSource = MatchedSource.LAYER_2_BLOCKLIST,
                tag = null,
                confidence = ActionConfidence.HIGH,
            )
        }

        val tag = tagJob.await()
        when (tag?.priority) {
            TagPriority.SUSPICIOUS -> ActionDecision(
                action = ActionType.SILENT,
                matchedSource = MatchedSource.LAYER_2_TAG,
                tag = tag.tagText,
                confidence = ActionConfidence.HIGH,
            )
            TagPriority.PENDING, TagPriority.REMIND_ME -> ActionDecision(
                action = ActionType.TAG_DISPLAY,
                matchedSource = MatchedSource.LAYER_2_TAG,
                tag = tag.tagText,
                confidence = ActionConfidence.HIGH,
            )
            TagPriority.ARCHIVE -> ActionDecision(
                action = ActionType.PASS,
                matchedSource = MatchedSource.LAYER_2_TAG,
                tag = tag.tagText,
                confidence = ActionConfidence.MEDIUM,
            )
            null -> ActionDecision(
                action = ActionType.PASS,
                matchedSource = MatchedSource.NONE,
                tag = null,
                confidence = ActionConfidence.LOW,
            )
        }
    }

    companion object {
        const val CALL_TIMEOUT_MILLIS = 50L
        const val SMS_TIMEOUT_MILLIS = 50L
        const val NOTIFICATION_TIMEOUT_MILLIS = 100L
    }
}
