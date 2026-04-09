package app.myphonecheck.mobile.feature.callintercept

import app.myphonecheck.mobile.core.model.LocalLearningSignal
import app.myphonecheck.mobile.core.model.UserCallAction
import app.myphonecheck.mobile.data.localcache.repository.UserCallRecordRepository
import javax.inject.Inject

/**
 * UserCallRecord → LocalLearningSignal 변환.
 *
 * Room DB에서 번호별 사용자 과거 행동을 조회하고,
 * DecisionEngine이 이해할 수 있는 LocalLearningSignal로 변환.
 *
 * 성능: Room 조회 1회 (인덱스 기반, <5ms)
 * 프라이버시: 온디바이스 전용, 서버 전송 없음.
 */
class LocalLearningProvider @Inject constructor(
    private val userCallRecordRepository: UserCallRecordRepository,
) {
    /**
     * 번호에 대한 로컬 학습 신호를 조회.
     *
     * @param canonicalNumber E.164 정규화 번호
     * @return LocalLearningSignal (기록 없으면 null)
     */
    suspend fun getSignal(canonicalNumber: String): LocalLearningSignal? {
        val record = userCallRecordRepository.findByNumber(canonicalNumber)
            ?: return null

        val lastAction = try {
            record.lastAction?.let { key ->
                UserCallAction.entries.firstOrNull { it.displayKey == key }
            }
        } catch (e: Exception) {
            null
        }

        return LocalLearningSignal(
            callCount = record.callCount,
            lastAction = lastAction,
            answeredCount = if (lastAction == UserCallAction.ANSWERED) record.callCount else 0,
            rejectedCount = if (lastAction == UserCallAction.REJECTED) record.callCount else 0,
            isBlocked = lastAction == UserCallAction.BLOCKED,
            userTag = record.tag,
        )
    }
}
