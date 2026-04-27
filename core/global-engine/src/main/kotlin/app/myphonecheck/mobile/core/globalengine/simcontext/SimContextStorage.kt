package app.myphonecheck.mobile.core.globalengine.simcontext

/**
 * 이전 SimContext 영구 저장 인터페이스 (Architecture v2.0.0 §29 + 헌법 §8-4).
 *
 * SimChangeDetector가 비교 입력으로 사용. 실 구현은 :data:local-cache (Room v14 SimContextSnapshot 활용).
 */
interface SimContextStorage {
    suspend fun loadPrevious(): SimContext?
    suspend fun saveCurrent(context: SimContext)
    suspend fun clear()
}
