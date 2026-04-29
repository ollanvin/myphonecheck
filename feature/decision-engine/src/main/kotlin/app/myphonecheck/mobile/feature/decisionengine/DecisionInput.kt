package app.myphonecheck.mobile.feature.decisionengine

import app.myphonecheck.mobile.core.globalengine.decision.AggregatedInput
import app.myphonecheck.mobile.core.model.ActionState
import app.myphonecheck.mobile.core.model.BehaviorPatternSignal
import app.myphonecheck.mobile.core.model.DeviceEvidence
import app.myphonecheck.mobile.core.model.LocalLearningSignal
import app.myphonecheck.mobile.core.model.SearchEvidence

/**
 * v2.5.0 §10-formula-2axis 통합 입력 (Architecture v2.5.0).
 *
 * 기존 v1 자산 (deviceEvidence / searchEvidence / localLearning / behaviorPattern / actionState) 보존 +
 * v2.5.0 신규 aggregatedInput (2축: NKB + AI 검색).
 *
 * aggregatedInput=null 일 때 기존 7 카테고리 매핑 로직 그대로 (회귀 보호).
 */
data class DecisionInput(
    val deviceEvidence: DeviceEvidence? = null,
    val searchEvidence: SearchEvidence? = null,
    val localLearning: LocalLearningSignal? = null,
    val behaviorPattern: BehaviorPatternSignal? = null,
    val actionState: ActionState? = null,
    /** v2.5.0 신규: 2축 입력 (null이면 legacy 로직). */
    val aggregatedInput: AggregatedInput? = null,
)
