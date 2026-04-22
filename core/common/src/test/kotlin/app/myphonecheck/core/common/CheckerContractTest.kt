package app.myphonecheck.core.common

import app.myphonecheck.core.common.checker.Checker
import app.myphonecheck.core.common.checker.CheckerException
import app.myphonecheck.core.common.identifier.IdentifierType
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CheckerContractTest {

    private class EchoChecker : Checker<IdentifierType.PhoneNumber, String> {
        override suspend fun check(
            input: IdentifierType.PhoneNumber,
        ): String = input.e164
    }

    @Test
    fun `Checker is suspend`() = runTest {
        val checker = EchoChecker()
        val input = IdentifierType.PhoneNumber("+821012345678")
        checker.check(input) shouldBe "+821012345678"
    }

    @Test
    fun `CheckerException carries reason`() {
        val e = CheckerException(
            "timeout",
            CheckerException.Reason.NETWORK_TIMEOUT,
        )
        e.reason shouldBe CheckerException.Reason.NETWORK_TIMEOUT
    }
}
