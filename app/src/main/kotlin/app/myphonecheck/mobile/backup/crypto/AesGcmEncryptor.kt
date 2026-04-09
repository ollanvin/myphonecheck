package app.myphonecheck.mobile.backup.crypto

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES-256-GCM 암·복호화.
 *
 * **키 제로화:** [encrypt] / [decrypt]는 전달받은 [key] 배열을 수정하지 않는다.
 * 민감한 파생 키는 **호출부에서 사용이 끝난 뒤** `key.fill(0)` 등으로 메모리를 지울 것.
 */
object AesGcmEncryptor {

    private const val GCM_TAG_BITS = 128

    data class Result(
        val iv: ByteArray,
        val ciphertext: ByteArray,
    )

    fun encrypt(key: ByteArray, plaintext: ByteArray): Result {
        val iv = ByteArray(CsbV2Format.GCM_IV_LENGTH_BYTES)
            .also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(key, "AES"),
            GCMParameterSpec(GCM_TAG_BITS, iv),
        )
        return Result(iv = iv, ciphertext = cipher.doFinal(plaintext))
    }

    fun decrypt(key: ByteArray, iv: ByteArray, ciphertext: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(key, "AES"),
            GCMParameterSpec(GCM_TAG_BITS, iv),
        )
        return cipher.doFinal(ciphertext)
    }
}
