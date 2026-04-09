package app.myphonecheck.mobile.backup.crypto

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * 복구 비밀번호에서 PBKDF2-HMAC-SHA256으로 AES-256 키를 파생한다.
 *
 * 호출부는 반환된 [Result.key] 사용 후 가능한 한 빨리 [ByteArray.fill] 등으로 제거하는 것을 권장한다
 * ([AesGcmEncryptor] KDoc 참고).
 */
object BackupPassphraseKdf {

    data class Result(
        val key: ByteArray,
        val salt: ByteArray,
        val iterations: Int,
    )

    private const val SALT_BYTES = 32

    /**
     * 백업 파일에 기록된 [salt]·[iterations]로 복구 비밀번호에서 키를 다시 파생한다 (v2 복원용).
     * 반환 [ByteArray]는 사용 후 호출부에서 [ByteArray.fill]로 제거할 것.
     */
    fun deriveKey(
        passphrase: CharSequence,
        salt: ByteArray,
        iterations: Int,
    ): ByteArray {
        val passwordChars = passphrase.toString().toCharArray()
        val spec = PBEKeySpec(
            passwordChars,
            salt,
            iterations,
            CsbV2Format.DERIVED_KEY_LENGTH_BYTES * 8,
        )
        return try {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            factory.generateSecret(spec).encoded
        } finally {
            passwordChars.fill('\u0000')
            spec.clearPassword()
        }
    }

    fun deriveKey(
        passphrase: CharSequence,
        iterations: Int = CsbV2Format.DEFAULT_PBKDF2_ITERATIONS,
    ): Result {
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val passwordChars = passphrase.toString().toCharArray()
        val spec = PBEKeySpec(
            passwordChars,
            salt,
            iterations,
            CsbV2Format.DERIVED_KEY_LENGTH_BYTES * 8,
        )
        return try {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val key = factory.generateSecret(spec).encoded
            Result(key = key, salt = salt, iterations = iterations)
        } finally {
            passwordChars.fill('\u0000')
            spec.clearPassword()
        }
    }
}
