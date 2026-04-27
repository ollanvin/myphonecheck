package app.myphonecheck.mobile.core.globalengine.parsing.message

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 발신자 인벤토리 — 빈도/마지막 수신 시각/카테고리 분포 (Architecture v2.0.0 §22).
 *
 * Stage 2-003: 인메모리 캐시 (영구 저장 안 함, 헌법 §2 In-Bound Zero).
 * 영구 저장은 후속 Stage에서 사용자 라벨링과 함께 검토.
 */
@Singleton
class SenderRegistry @Inject constructor() {

    private val store = mutableMapOf<String, SenderProfile>()

    fun record(result: MessageParseResult, timestampMillis: Long) {
        val sender = result.features.sender
        val existing = store[sender]
        store[sender] = if (existing == null) {
            SenderProfile(
                sender = sender,
                isShortSender = result.features.isShortSender,
                count = 1,
                lastSeenMillis = timestampMillis,
                categoryCounts = mapOf(result.category to 1),
            )
        } else {
            existing.copy(
                count = existing.count + 1,
                lastSeenMillis = maxOf(existing.lastSeenMillis, timestampMillis),
                categoryCounts = existing.categoryCounts + (
                    result.category to (existing.categoryCounts[result.category] ?: 0) + 1
                ),
            )
        }
    }

    fun snapshot(): List<SenderProfile> = store.values.sortedByDescending { it.count }

    fun clear() = store.clear()
}

data class SenderProfile(
    val sender: String,
    val isShortSender: Boolean,
    val count: Int,
    val lastSeenMillis: Long,
    val categoryCounts: Map<MessageCategory, Int>,
) {
    /** 가장 빈번한 카테고리 — 인벤토리 라벨 표시용. */
    fun dominantCategory(): MessageCategory =
        categoryCounts.maxByOrNull { it.value }?.key ?: MessageCategory.NORMAL
}
