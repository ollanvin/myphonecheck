package app.myphonecheck.mobile.feature.initialscan.service

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SIM 컨텍스트 스캐너 (Architecture v2.0.0 §28 + 헌법 §8조).
 *
 * 코어 SimContextProvider의 얇은 wrapper — Initial Scan orchestrator가 첫 단계로 호출.
 */
@Singleton
class SimContextScanner @Inject constructor(
    private val provider: SimContextProvider,
) {
    fun scan(): SimContext = provider.resolve()
}
