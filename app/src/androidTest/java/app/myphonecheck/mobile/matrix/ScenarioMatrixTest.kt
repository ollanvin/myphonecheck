package app.myphonecheck.mobile.matrix

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Phase 4 매트릭스 검증 instrumented test 골격 (헌법 §9-6 정합).
 *
 * 17 시나리오 × 11 SIM × 4 디바이스 매트릭스 = 748 풀.
 * Latin Hypercube 샘플링으로 PR 게이트 ~10분, 야간 ~60분 (scripts/matrix/sample/).
 *
 * 본 PR은 골격만 작성한다. 시나리오 본문(actual UI flow + assertion)은 후속 PR.
 *
 * CI 호출 예:
 *   ./gradlew :app:pixel7Api34DebugAndroidTest \
 *     -Pandroid.testInstrumentationRunnerArguments.class=app.myphonecheck.mobile.matrix.ScenarioMatrixTest#testS01 \
 *     -Pandroid.testInstrumentationRunnerArguments.sim=KR
 */
@RunWith(AndroidJUnit4::class)
class ScenarioMatrixTest {

    private lateinit var sim: SimMatrixContext

    @Before
    fun resolveSimContext() {
        val args = InstrumentationRegistry.getArguments()
        val simArg = args.getString(SimMatrixContext.ARG_SIM) ?: "KR"
        sim = SimMatrixContext.fromArg(simArg)
            ?: error("Unknown SIM: $simArg. Expected one of ${SimMatrixContext.values().map { it.name }}")
        Log.i(TAG, "Matrix run start sim=${sim.name} locale=${sim.locale} mccMnc=${sim.mccMnc}")
    }

    @Test fun testS01() = runScenario(ScenarioId.S01)
    @Test fun testS02() = runScenario(ScenarioId.S02)
    @Test fun testS03() = runScenario(ScenarioId.S03)
    @Test fun testS04() = runScenario(ScenarioId.S04)
    @Test fun testS05() = runScenario(ScenarioId.S05)
    @Test fun testS06() = runScenario(ScenarioId.S06)
    @Test fun testS07() = runScenario(ScenarioId.S07)
    @Test fun testS08() = runScenario(ScenarioId.S08)
    @Test fun testS09() = runScenario(ScenarioId.S09)
    @Test fun testS10() = runScenario(ScenarioId.S10)
    @Test fun testS11() = runScenario(ScenarioId.S11)
    @Test fun testS12() = runScenario(ScenarioId.S12)
    @Test fun testS13() = runScenario(ScenarioId.S13)
    @Test fun testS14() = runScenario(ScenarioId.S14)
    @Test fun testS15() = runScenario(ScenarioId.S15)
    @Test fun testS16() = runScenario(ScenarioId.S16)
    @Test fun testS17() = runScenario(ScenarioId.S17)

    private fun runScenario(scenario: ScenarioId) {
        Log.i(TAG, "scenario=${scenario.name} surface=${scenario.surface} sim=${sim.name}")
        assertNotNull(scenario)
        assertNotNull(sim)
        // TODO Phase 4-A 후속 PR: surface UI flow + assertion 본문 작성.
        // 골격 단계 — 매트릭스 호출 경로(@Test 식별자)·SIM 컨텍스트·디바이스 분기 검증만 수행.
        assertTrue("Scenario placeholder must record context", scenario.surface.isNotEmpty())
    }

    companion object {
        private const val TAG = "PhaseMatrix"
    }
}
