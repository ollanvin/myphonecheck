package app.myphonecheck.mobile.backup

import com.google.gson.Gson
import app.myphonecheck.mobile.backup.crypto.AesGcmEncryptor
import app.myphonecheck.mobile.backup.crypto.BackupPassphraseKdf
import app.myphonecheck.mobile.backup.crypto.CsbV2Format
import app.myphonecheck.mobile.data.localcache.dao.PreJudgeCacheDao
import app.myphonecheck.mobile.data.localcache.dao.UserCallRecordDao
import app.myphonecheck.mobile.data.localcache.db.MyPhoneCheckDatabase
import kotlinx.coroutines.runBlocking
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 암호화된 로컬 백업 파일(.csb)을 복호화하고 DB에 복원한다.
 *
 * - **v2**: 복구 비밀번호 + PBKDF2 + AES-GCM ([CsbV2Format]).
 * - **v1**: Android Keystore AES-GCM (이 기기에서 생성한 백업만; [passphrase]는 사용하지 않음).
 */
@Singleton
class RestoreCore @Inject constructor(
    private val database: MyPhoneCheckDatabase,
    private val userCallRecordDao: UserCallRecordDao,
    private val preJudgeCacheDao: PreJudgeCacheDao,
) {
    companion object {
        private const val KEYSTORE_ALIAS = "myphonecheck_backup_key"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val GCM_TAG_LENGTH = 128
        private const val GCM_IV_LENGTH = 12
    }

    private val gson = Gson()

    /**
     * 백업 파일에서 데이터를 복원한다.
     * 기존 데이터를 모두 삭제하고 백업 데이터로 교체한다.
     *
     * @param backupFile 암호화된 백업 파일 (.csb)
     * @param passphrase v2 복원에 사용. v1 복원 시에는 무시된다.
     * @return 복원된 레코드 수 요약
     */
    suspend fun restore(backupFile: File, passphrase: CharSequence): RestoreResult {
        val fileBytes = backupFile.readBytes()

        val decrypted = if (CsbV2Format.isProbablyV2(fileBytes)) {
            val envelope = CsbV2Format.decode(fileBytes)
            val key = BackupPassphraseKdf.deriveKey(
                passphrase,
                envelope.salt,
                envelope.pbkdf2Iterations,
            )
            try {
                AesGcmEncryptor.decrypt(key, envelope.iv, envelope.ciphertext)
            } finally {
                key.fill(0)
            }
        } else {
            val iv = fileBytes.sliceArray(0 until GCM_IV_LENGTH)
            val encrypted = fileBytes.sliceArray(GCM_IV_LENGTH until fileBytes.size)

            val secretKey = getKey()
                ?: throw IllegalStateException("백업 암호화 키를 찾을 수 없습니다. 이 기기에서 생성한 백업만 복원할 수 있습니다.")

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
            cipher.doFinal(encrypted)
        }

        return restoreFromDecryptedJson(decrypted)
    }

    private fun restoreFromDecryptedJson(decrypted: ByteArray): RestoreResult {
        val json = String(decrypted, Charsets.UTF_8)
        val payload = gson.fromJson(json, BackupCore.BackupPayload::class.java)

        database.runInTransaction {
            kotlinx.coroutines.runBlocking {
                userCallRecordDao.deleteAll()
                preJudgeCacheDao.deleteAll()

                userCallRecordDao.insertAll(payload.userCallRecords)
                preJudgeCacheDao.insertAll(payload.preJudgeCacheEntries)
            }
        }

        return RestoreResult(
            userCallRecords = payload.userCallRecords.size,
            preJudgeCacheEntries = payload.preJudgeCacheEntries.size,
        )
    }

    /** Android Keystore에서 백업 키를 가져온다. */
    private fun getKey(): SecretKey? {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        return keyStore.getKey(KEYSTORE_ALIAS, null) as? SecretKey
    }

    data class RestoreResult(
        val userCallRecords: Int,
        val preJudgeCacheEntries: Int,
    )
}
