package app.myphonecheck.mobile.core.globalengine.parsing.currency.learning

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 사용자 주도 라벨링 서비스 (Architecture v2.0.0 §27-3-1 + §30 :core:global-engine).
 *
 * Stage 2-001 마이그레이션: :feature:card-check/learning/LabelingService.kt → 본 위치 (기능 동일).
 *
 * SourceDetector가 Suspect로 분류한 발신자에 대해 사용자 확인을 받아 라벨 학습.
 * 한 번 라벨링된 발신자는 이후 자동 처리 (Known).
 *
 * Stage 1-002: 단순 confirm/deny + 라벨 입력.
 * Stage 2-001 (본 PR): 코어로 이전, 기능 동일.
 * Stage 2+: 매칭 패턴 학습 강화 (ML) — 후속.
 */
@Singleton
class LabelingService @Inject constructor(
    private val cache: SourceLabelCache,
) {

    /**
     * 사용자가 발신자를 카드 결제 출처로 확인 + 라벨 지정.
     */
    suspend fun confirmAsCard(sourceId: String, label: String) {
        require(label.isNotBlank()) { "label must not be blank" }
        cache.upsert(sourceId, label.trim())
    }

    /**
     * 사용자가 발신자를 카드 결제 아님으로 분류 (라벨 캐시에서 제거).
     */
    suspend fun denyAsCard(sourceId: String) {
        cache.deleteById(sourceId)
    }
}
