package app.myphonecheck.core.common.engine

import app.myphonecheck.core.common.identifier.IdentifierType
import app.myphonecheck.core.common.risk.RiskKnowledge
import app.myphonecheck.core.common.risk.SearchEvidence

/**
 * DecisionEngine 계약 (§6 Three-Layer Knowledge Sourcing 연결점).
 *
 * Stage 1에서 core/engine 모듈이 이 인터페이스를 구현한다.
 * Stage 0 단계에서는 계약만 정의하고 구현 없음.
 *
 * 4 Checker는 이 계약을 통해서만 엔진에 접근한다.
 * Checker가 NKB/Search/Source Mesh를 직접 호출하는 것 금지.
 *
 * FREEZE: 메서드 시그니처 변경 금지.
 *         새 메서드 추가는 MINOR 버전.
 */
interface DecisionEngineContract {

    /**
     * 3계층 소싱 실행.
     *
     * @param identifier 분석 대상
     * @return 근거 목록 (L1 NKB → L2 Search → L3 Public DB 순서)
     * @throws CheckerException 소싱 실패 시
     */
    suspend fun sourceEvidence(
        identifier: IdentifierType,
    ): List<SearchEvidence>

    /**
     * 검색 쿼리로 근거 수집 (MicCheck/CameraCheck 앱 평판용).
     *
     * @param query 검색 쿼리 문자열
     *   (예: "WhatsApp RECORD_AUDIO privacy scandal")
     * @return 근거 목록
     * @throws CheckerException 소싱 실패 시
     */
    suspend fun search(query: String): List<SearchEvidence>

    /**
     * 근거 목록을 RiskKnowledge로 합성 (§10 Decision Engine 조합).
     *
     * @param identifier 원천 식별자
     * @param evidence sourceEvidence 결과
     * @return 분석 결과 (구현체가 Surface별 타입으로 반환)
     * @throws CheckerException 합성 실패 시
     */
    suspend fun synthesize(
        identifier: IdentifierType,
        evidence: List<SearchEvidence>,
    ): RiskKnowledge
}
