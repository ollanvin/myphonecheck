package app.myphonecheck.core.common

import app.myphonecheck.core.common.identifier.IdentifierType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class IdentifierTypeTest {

    @Test
    fun `PhoneNumber accepts valid E164`() {
        val id = IdentifierType.PhoneNumber("+821012345678")
        id.e164 shouldBe "+821012345678"
    }

    @Test
    fun `PhoneNumber rejects missing plus`() {
        shouldThrow<IllegalArgumentException> {
            IdentifierType.PhoneNumber("821012345678")
        }
    }

    @Test
    fun `PhoneNumber rejects too short`() {
        shouldThrow<IllegalArgumentException> {
            IdentifierType.PhoneNumber("+1234")
        }
    }

    @Test
    fun `SmsMessage rejects blank sender`() {
        shouldThrow<IllegalArgumentException> {
            IdentifierType.SmsMessage("", "body")
        }
    }

    @Test
    fun `SmsMessage rejects body over 1000 chars`() {
        shouldThrow<IllegalArgumentException> {
            IdentifierType.SmsMessage("+821012345678", "a".repeat(1001))
        }
    }

    @Test
    fun `AppPackage accepts RECORD_AUDIO`() {
        val id = IdentifierType.AppPackage(
            "com.example.app",
            "android.permission.RECORD_AUDIO",
        )
        id.permission shouldBe "android.permission.RECORD_AUDIO"
    }

    @Test
    fun `AppPackage accepts CAMERA`() {
        IdentifierType.AppPackage(
            "com.example.app",
            "android.permission.CAMERA",
        ).permission shouldBe "android.permission.CAMERA"
    }

    @Test
    fun `AppPackage rejects non-whitelist permission`() {
        shouldThrow<IllegalArgumentException> {
            IdentifierType.AppPackage(
                "com.example.app",
                "android.permission.READ_SMS",
            )
        }
    }

    @Test
    fun `AppPackage rejects package without dot`() {
        shouldThrow<IllegalArgumentException> {
            IdentifierType.AppPackage(
                "example",
                "android.permission.CAMERA",
            )
        }
    }
}
