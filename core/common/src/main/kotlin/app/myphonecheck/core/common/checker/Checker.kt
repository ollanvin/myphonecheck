package app.myphonecheck.core.common.checker

import app.myphonecheck.core.common.risk.RiskKnowledge

/**
 * Execution contract for four checkers. OUT is typically [RiskKnowledge] or List of it.
 * FREEZE: signature (generics, suspend) must not change without MAJOR.
 */
interface Checker<IN, OUT> {

    /**
     * Analyze input. Implementations throw [CheckerException] on recoverable failures.
     */
    suspend fun check(input: IN): OUT
}

/**
 * Failure while running a checker (timeout, permission, invalid input, etc.).
 */
class CheckerException(
    message: String,
    val reason: Reason,
    cause: Throwable? = null,
) : Exception(message, cause) {

    enum class Reason {
        NETWORK_TIMEOUT,
        PERMISSION_DENIED,
        INVALID_INPUT,
        NKB_READ_FAILED,
        UNKNOWN,
    }
}
