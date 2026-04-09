package app.myphonecheck.mobile.feature.callintercept

import app.myphonecheck.mobile.core.model.SearchEngine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 운영 서킷 브레이커.
 *
 * 자비스 기준:
 * "특정 국가에서 검색 실패율이 급증하면 자동으로 2순위/3순위로 넘기고,
 *  그래도 실패하면 최소 안전 표현으로 떨어지는 운영 모드가 필요"
 *
 * 서킷 브레이커 3-State 패턴 (빅테크 정석):
 *   CLOSED  → 정상 운영 (1순위 사용)
 *   HALF_OPEN → 1순위 부분 시도 (성공하면 CLOSED 복귀)
 *   OPEN    → 1순위 차단, 2순위/3순위만 사용
 *
 * 추가 Emergency Mode:
 *   2순위/3순위도 실패 → 최소 안전 표현 강제 표시
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class OperationalCircuitBreaker @Inject constructor(
    private val launchLock: LaunchReadinessLock,
) {

    // ══════════════════════════════════════
    // 서킷 상태
    // ══════════════════════════════════════

    enum class CircuitState(val label: String) {
        /** 정상: 1순위 검색 사용 */
        CLOSED("정상"),
        /** 반개방: 1순위를 제한적으로 시도 (성공 시 CLOSED 복귀) */
        HALF_OPEN("부분 시도"),
        /** 개방: 1순위 차단, 2순위/3순위만 사용 */
        OPEN("1순위 차단"),
        /** 긴급: 모든 검색 실패, 최소 안전 표현만 표시 */
        EMERGENCY("긴급 모드"),
    }

    /** 국가별 서킷 상태 */
    data class CountryCircuit(
        val countryCode: String,
        val state: CircuitState,
        /** 연속 실패 횟수 */
        val consecutiveFailures: Int,
        /** 연속 성공 횟수 (HALF_OPEN에서 사용) */
        val consecutiveSuccesses: Int,
        /** 마지막 상태 변경 시각 */
        val lastStateChangeMs: Long,
        /** 현재 사용 중인 검색 엔진 (서킷에 의해 변경될 수 있음) */
        val effectivePrimaryEngine: SearchEngine,
        /** 원래 1순위 엔진 */
        val originalPrimaryEngine: SearchEngine,
    )

    /** 엔진별 서킷 상태 */
    data class EngineCircuit(
        val engine: SearchEngine,
        val state: CircuitState,
        val consecutiveFailures: Int,
        val consecutiveSuccesses: Int,
        val lastStateChangeMs: Long,
    )

    // ══════════════════════════════════════
    // 메모리 저장소
    // ══════════════════════════════════════

    private val countryCircuits = mutableMapOf<String, CountryCircuit>()
    private val engineCircuits = mutableMapOf<SearchEngine, EngineCircuit>()

    // ══════════════════════════════════════
    // 서킷 조회
    // ══════════════════════════════════════

    /** 국가별 서킷 상태 조회 */
    fun getCountryCircuit(countryCode: String): CountryCircuit {
        return countryCircuits.getOrPut(countryCode) {
            CountryCircuit(
                countryCode = countryCode,
                state = CircuitState.CLOSED,
                consecutiveFailures = 0,
                consecutiveSuccesses = 0,
                lastStateChangeMs = System.currentTimeMillis(),
                effectivePrimaryEngine = SearchEngine.GOOGLE, // 기본값, 실제로는 registry에서 가져옴
                originalPrimaryEngine = SearchEngine.GOOGLE,
            )
        }
    }

    /** 엔진별 서킷 상태 조회 */
    fun getEngineCircuit(engine: SearchEngine): EngineCircuit {
        return engineCircuits.getOrPut(engine) {
            EngineCircuit(
                engine = engine,
                state = CircuitState.CLOSED,
                consecutiveFailures = 0,
                consecutiveSuccesses = 0,
                lastStateChangeMs = System.currentTimeMillis(),
            )
        }
    }

    // ══════════════════════════════════════
    // 이벤트 처리
    // ══════════════════════════════════════

    /**
     * 검색 성공 기록.
     */
    @Synchronized
    fun recordSuccess(countryCode: String, engine: SearchEngine) {
        // 국가 서킷
        val cc = getCountryCircuit(countryCode)
        val newCc = when (cc.state) {
            CircuitState.CLOSED -> cc.copy(
                consecutiveFailures = 0,
                consecutiveSuccesses = cc.consecutiveSuccesses + 1,
            )
            CircuitState.HALF_OPEN -> {
                val newSuccesses = cc.consecutiveSuccesses + 1
                if (newSuccesses >= HALF_OPEN_SUCCESS_THRESHOLD) {
                    // HALF_OPEN → CLOSED: 1순위 복귀
                    cc.copy(
                        state = CircuitState.CLOSED,
                        consecutiveFailures = 0,
                        consecutiveSuccesses = 0,
                        lastStateChangeMs = System.currentTimeMillis(),
                        effectivePrimaryEngine = cc.originalPrimaryEngine,
                    )
                } else {
                    cc.copy(consecutiveSuccesses = newSuccesses)
                }
            }
            CircuitState.OPEN -> {
                // OPEN 상태에서도 시간 경과 후 HALF_OPEN으로 전이 가능
                val elapsed = System.currentTimeMillis() - cc.lastStateChangeMs
                if (elapsed >= OPEN_TO_HALF_OPEN_COOLDOWN_MS) {
                    cc.copy(
                        state = CircuitState.HALF_OPEN,
                        consecutiveSuccesses = 1,
                        lastStateChangeMs = System.currentTimeMillis(),
                    )
                } else {
                    cc.copy(consecutiveSuccesses = cc.consecutiveSuccesses + 1)
                }
            }
            CircuitState.EMERGENCY -> {
                // EMERGENCY에서 성공하면 HALF_OPEN으로 복귀 시도
                cc.copy(
                    state = CircuitState.HALF_OPEN,
                    consecutiveFailures = 0,
                    consecutiveSuccesses = 1,
                    lastStateChangeMs = System.currentTimeMillis(),
                )
            }
        }
        countryCircuits[countryCode] = newCc

        // 엔진 서킷
        val ec = getEngineCircuit(engine)
        val newEc = when (ec.state) {
            CircuitState.HALF_OPEN -> {
                val newSuccesses = ec.consecutiveSuccesses + 1
                if (newSuccesses >= HALF_OPEN_SUCCESS_THRESHOLD) {
                    ec.copy(state = CircuitState.CLOSED, consecutiveFailures = 0, consecutiveSuccesses = 0, lastStateChangeMs = System.currentTimeMillis())
                } else {
                    ec.copy(consecutiveSuccesses = newSuccesses, consecutiveFailures = 0)
                }
            }
            else -> ec.copy(consecutiveFailures = 0, consecutiveSuccesses = ec.consecutiveSuccesses + 1)
        }
        engineCircuits[engine] = newEc
    }

    /**
     * 검색 실패 기록.
     */
    @Synchronized
    fun recordFailure(
        countryCode: String,
        engine: SearchEngine,
        secondaryEngine: SearchEngine,
        tertiaryEngine: SearchEngine,
    ) {
        // 국가 서킷
        val cc = getCountryCircuit(countryCode)
        val newFailures = cc.consecutiveFailures + 1

        val newCc = when {
            // CLOSED/HALF_OPEN → 실패 임계값 도달 → OPEN
            cc.state in setOf(CircuitState.CLOSED, CircuitState.HALF_OPEN) &&
                newFailures >= FAILURE_THRESHOLD -> {
                cc.copy(
                    state = CircuitState.OPEN,
                    consecutiveFailures = newFailures,
                    consecutiveSuccesses = 0,
                    lastStateChangeMs = System.currentTimeMillis(),
                    effectivePrimaryEngine = secondaryEngine, // 2순위로 강등
                )
            }
            // OPEN → 추가 실패 → EMERGENCY (2순위/3순위도 실패)
            cc.state == CircuitState.OPEN && newFailures >= EMERGENCY_THRESHOLD -> {
                cc.copy(
                    state = CircuitState.EMERGENCY,
                    consecutiveFailures = newFailures,
                    consecutiveSuccesses = 0,
                    lastStateChangeMs = System.currentTimeMillis(),
                )
            }
            // 그 외: 실패 카운트만 증가
            else -> cc.copy(
                consecutiveFailures = newFailures,
                consecutiveSuccesses = 0,
            )
        }
        countryCircuits[countryCode] = newCc

        // 엔진 서킷
        val ec = getEngineCircuit(engine)
        val engineNewFailures = ec.consecutiveFailures + 1
        val newEc = if (engineNewFailures >= FAILURE_THRESHOLD) {
            ec.copy(
                state = CircuitState.OPEN,
                consecutiveFailures = engineNewFailures,
                consecutiveSuccesses = 0,
                lastStateChangeMs = System.currentTimeMillis(),
            )
        } else {
            ec.copy(consecutiveFailures = engineNewFailures, consecutiveSuccesses = 0)
        }
        engineCircuits[engine] = newEc
    }

    // ══════════════════════════════════════
    // 라우팅 결정
    // ══════════════════════════════════════

    /**
     * 현재 서킷 상태 기반 라우팅 결정.
     *
     * @return RoutingDecision — 어떤 엔진을 사용할지, 안전 표현을 바로 표시할지
     */
    fun getRoutingDecision(
        countryCode: String,
        originalPrimary: SearchEngine,
        secondary: SearchEngine,
        tertiary: SearchEngine,
        languageCode: String,
    ): RoutingDecision {
        val circuit = getCountryCircuit(countryCode)

        return when (circuit.state) {
            CircuitState.CLOSED -> RoutingDecision(
                effectiveEngines = listOf(originalPrimary, secondary, tertiary),
                mode = OperationMode.NORMAL,
                safeExpression = null,
            )
            CircuitState.HALF_OPEN -> RoutingDecision(
                effectiveEngines = listOf(originalPrimary, secondary, tertiary),
                mode = OperationMode.CAUTIOUS,
                safeExpression = null,
            )
            CircuitState.OPEN -> RoutingDecision(
                effectiveEngines = listOf(secondary, tertiary),
                mode = OperationMode.FALLBACK,
                safeExpression = null,
            )
            CircuitState.EMERGENCY -> RoutingDecision(
                effectiveEngines = emptyList(),
                mode = OperationMode.EMERGENCY,
                safeExpression = launchLock.getMinimumSafeVerdict(languageCode),
            )
        }
    }

    /** 운영 모드 */
    enum class OperationMode(val label: String) {
        NORMAL("정상 — 1순위 사용"),
        CAUTIOUS("주의 — 1순위 부분 시도"),
        FALLBACK("fallback — 2순위/3순위만"),
        EMERGENCY("긴급 — 최소 안전 표현"),
    }

    data class RoutingDecision(
        val effectiveEngines: List<SearchEngine>,
        val mode: OperationMode,
        /** EMERGENCY 모드에서 즉시 표시할 안전 표현 */
        val safeExpression: String?,
    )

    // ══════════════════════════════════════
    // 상태 보고
    // ══════════════════════════════════════

    /** 전체 서킷 상태 보고 */
    fun getCircuitReport(): CircuitReport {
        val countryStates = countryCircuits.values.toList()
        val engineStates = engineCircuits.values.toList()

        val openCountries = countryStates.filter { it.state == CircuitState.OPEN }
        val emergencyCountries = countryStates.filter { it.state == CircuitState.EMERGENCY }
        val halfOpenCountries = countryStates.filter { it.state == CircuitState.HALF_OPEN }

        return CircuitReport(
            totalCountriesMonitored = countryStates.size,
            closedCount = countryStates.count { it.state == CircuitState.CLOSED },
            halfOpenCount = halfOpenCountries.size,
            openCount = openCountries.size,
            emergencyCount = emergencyCountries.size,
            openCountries = openCountries.map { it.countryCode },
            emergencyCountries = emergencyCountries.map { it.countryCode },
            engineStates = engineStates,
        )
    }

    data class CircuitReport(
        val totalCountriesMonitored: Int,
        val closedCount: Int,
        val halfOpenCount: Int,
        val openCount: Int,
        val emergencyCount: Int,
        val openCountries: List<String>,
        val emergencyCountries: List<String>,
        val engineStates: List<EngineCircuit>,
    ) {
        fun toJarvisFormat(): String = buildString {
            appendLine("═══ Circuit Breaker 상태 ═══")
            appendLine()
            appendLine("모니터링 국가: ${totalCountriesMonitored}개국")
            appendLine("CLOSED (정상): $closedCount")
            appendLine("HALF_OPEN (부분시도): $halfOpenCount")
            appendLine("OPEN (1순위 차단): $openCount")
            appendLine("EMERGENCY (긴급): $emergencyCount")
            appendLine()

            if (openCountries.isNotEmpty()) {
                appendLine("── OPEN 국가 (1순위 차단) ──")
                openCountries.forEach { appendLine("  ⚠️ $it") }
                appendLine()
            }

            if (emergencyCountries.isNotEmpty()) {
                appendLine("── EMERGENCY 국가 (긴급 모드) ──")
                emergencyCountries.forEach { appendLine("  🔴 $it") }
                appendLine()
            }

            val problematicEngines = engineStates.filter { it.state != CircuitState.CLOSED }
            if (problematicEngines.isNotEmpty()) {
                appendLine("── 문제 엔진 ──")
                problematicEngines.forEach { e ->
                    appendLine("  ${e.engine.displayName}: ${e.state.label} | 연속실패 ${e.consecutiveFailures}")
                }
            }
        }
    }

    /** 서킷 초기화 (테스트용) */
    @Synchronized
    fun resetAll() {
        countryCircuits.clear()
        engineCircuits.clear()
    }

    companion object {
        /** 연속 실패 임계값 → OPEN 전이 */
        private const val FAILURE_THRESHOLD = 5

        /** 연속 실패 임계값 → EMERGENCY 전이 */
        private const val EMERGENCY_THRESHOLD = 10

        /** HALF_OPEN에서 CLOSED 복귀 필요 연속 성공 수 */
        private const val HALF_OPEN_SUCCESS_THRESHOLD = 3

        /** OPEN → HALF_OPEN 전이 쿨다운 (ms) — 5분 */
        private const val OPEN_TO_HALF_OPEN_COOLDOWN_MS = 5 * 60 * 1000L
    }
}
