package app.myphonecheck.mobile.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android Keystore 기반 데이터베이스 암호화 키 관리.
 *
 * 올랑방 앱팩토리 공통 모듈 — 전체 앱에서 재사용 가능.
 *
 * 키는 하드웨어 보안 모듈(TEE/StrongBox)에 저장되며,
 * 앱 외부에서 추출 불가. 루팅 환경에서도 키 자체는 보호됨.
 */
@Singleton
class DatabaseKeyProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "ollanvin_db_encryption_key"
        private const val KEY_SIZE = 256
    }

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
    }

    /**
     * DB 암호화에 사용할 키의 바이트 배열을 반환.
     * SQLCipher SupportFactory에 전달할 passphrase로 사용.
     *
     * Keystore의 AES 키를 직접 export할 수 없으므로,
     * 별도의 래핑 키로 DB passphrase를 암호화하여 SharedPreferences에 저장하고,
     * Keystore 키로 복호화하여 반환하는 구조.
     */
    fun getOrCreateDatabaseKey(): ByteArray {
        val prefs = context.getSharedPreferences("ollanvin_security", Context.MODE_PRIVATE)
        val existing = prefs.getString("encrypted_db_passphrase", null)

        if (existing != null) {
            return decryptPassphrase(existing)
        }

        // 신규 passphrase 생성 (32 bytes = 256 bit)
        val passphrase = ByteArray(32).also {
            java.security.SecureRandom().nextBytes(it)
        }

        val encrypted = encryptPassphrase(passphrase)
        prefs.edit().putString("encrypted_db_passphrase", encrypted).apply()

        return passphrase
    }

    private fun getOrCreateKeystoreKey(): SecretKey {
        val entry = keyStore.getEntry(KEY_ALIAS, null)
        if (entry is KeyStore.SecretKeyEntry) {
            return entry.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER,
        )
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    private fun encryptPassphrase(passphrase: ByteArray): String {
        val key = getOrCreateKeystoreKey()
        val cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(passphrase)
        // IV (12 bytes) + encrypted data를 Base64로 저장
        val combined = iv + encrypted
        return android.util.Base64.encodeToString(combined, android.util.Base64.NO_WRAP)
    }

    private fun decryptPassphrase(encoded: String): ByteArray {
        val combined = android.util.Base64.decode(encoded, android.util.Base64.NO_WRAP)
        val iv = combined.copyOfRange(0, 12)
        val encrypted = combined.copyOfRange(12, combined.size)

        val key = getOrCreateKeystoreKey()
        val cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")
        val spec = javax.crypto.spec.GCMParameterSpec(128, iv)
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key, spec)
        return cipher.doFinal(encrypted)
    }
}
