package app.myphonecheck.mobile.backup

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import app.myphonecheck.mobile.backup.crypto.AesGcmEncryptor
import app.myphonecheck.mobile.backup.crypto.BackupPassphraseKdf
import app.myphonecheck.mobile.backup.crypto.CsbV2Envelope
import app.myphonecheck.mobile.backup.crypto.CsbV2Format
import app.myphonecheck.mobile.data.localcache.dao.BackupMetadataDao
import app.myphonecheck.mobile.data.localcache.dao.PreJudgeCacheDao
import app.myphonecheck.mobile.data.localcache.dao.UserCallRecordDao
import app.myphonecheck.mobile.data.localcache.entity.BackupMetadataEntity
import app.myphonecheck.mobile.data.localcache.entity.PreJudgeCacheEntry
import app.myphonecheck.mobile.data.localcache.entity.UserCallRecord
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room DB 전체를 JSON 직렬화 → 복구 비밀번호 기반 PBKDF2 + AES-256-GCM → v2 `.csb` 파일 저장.
 *
 * 저장 경로: /storage/emulated/0/Documents/MyPhoneCheck/backup_YYYYMMDD.csb
 */
@Singleton
class BackupCore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userCallRecordDao: UserCallRecordDao,
    private val preJudgeCacheDao: PreJudgeCacheDao,
    private val backupMetadataDao: BackupMetadataDao,
    private val backupFileManager: BackupFileManager,
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    companion object {
        private const val TAG = "BackupCore"
    }

    /**
     * 전체 DB를 백업한다.
     * @param safTreeUri SAF 트리 URI (유저가 선택한 클라우드/외부 저장소). null이면 디바이스만 저장.
     * @param passphrase 복구 비밀번호 (v2 백업 암호화에 사용). 비어 있으면 [IllegalArgumentException].
     * @return 생성된 백업 메타데이터
     */
    suspend fun createBackup(
        safTreeUri: Uri? = null,
        passphrase: CharSequence = "",
    ): BackupMetadataEntity {
        require(passphrase.isNotBlank()) { "복구 비밀번호가 필요합니다" }

        // 1. DB 데이터 수집
        val userCallRecords = userCallRecordDao.getAllOnce()
        val preJudgeCacheEntries = preJudgeCacheDao.getAllOnce()

        val payload = BackupPayload(
            version = 1,
            createdAt = System.currentTimeMillis(),
            userCallRecords = userCallRecords,
            preJudgeCacheEntries = preJudgeCacheEntries,
        )

        // 2. JSON 직렬화
        val json = gson.toJson(payload)
        val plainBytes = json.toByteArray(Charsets.UTF_8)

        // 3. PBKDF2 + AES-GCM → v2 `.csb` 바이너리
        val kdf = BackupPassphraseKdf.deriveKey(passphrase)
        try {
            val enc = AesGcmEncryptor.encrypt(kdf.key, plainBytes)
            val fileBytes = CsbV2Format.encode(
                CsbV2Envelope(
                    pbkdf2Iterations = kdf.iterations,
                    salt = kdf.salt,
                    iv = enc.iv,
                    ciphertext = enc.ciphertext,
                ),
            )

            val backupFile = backupFileManager.createBackupFile()
            backupFile.outputStream().use { out ->
                out.write(fileBytes)
            }

            val checksum = sha256(backupFile.readBytes())

            val metadata = BackupMetadataEntity(
                filePath = backupFile.absolutePath,
                createdAt = payload.createdAt,
                sizeBytes = backupFile.length(),
                encrypted = true,
                checksum = checksum,
            )
            val id = backupMetadataDao.insert(metadata)

            backupFileManager.pruneOldBackups()

            if (safTreeUri != null) {
                copyToSaf(safTreeUri, backupFile)
            }

            return metadata.copy(id = id)
        } finally {
            kdf.key.fill(0)
        }
    }

    /**
     * 백업 파일을 SAF 트리 URI 위치에 복사한다.
     * Google Drive / OneDrive / MYBOX 등 유저가 선택한 클라우드 저장소에 저장됨.
     */
    private fun copyToSaf(treeUri: Uri, sourceFile: File) {
        try {
            val parentDoc = DocumentFile.fromTreeUri(context, treeUri) ?: run {
                Log.w(TAG, "SAF 트리 URI에 접근할 수 없음: $treeUri")
                return
            }

            // 기존 같은 이름 파일이 있으면 삭제 후 새로 생성
            val existingFile = parentDoc.findFile(sourceFile.name)
            existingFile?.delete()

            val newDoc = parentDoc.createFile("application/octet-stream", sourceFile.name) ?: run {
                Log.w(TAG, "SAF 위치에 파일 생성 실패: ${sourceFile.name}")
                return
            }

            context.contentResolver.openOutputStream(newDoc.uri)?.use { out ->
                sourceFile.inputStream().use { input ->
                    input.copyTo(out)
                }
            }

            Log.d(TAG, "SAF 백업 저장 완료: ${newDoc.uri}")
        } catch (e: Exception) {
            Log.e(TAG, "SAF 백업 저장 실패 (디바이스 백업은 정상 완료됨)", e)
        }
    }

    /** SHA-256 해시를 hex 문자열로 반환한다. */
    private fun sha256(data: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(data).joinToString("") { "%02x".format(it) }
    }

    /**
     * 백업 페이로드 구조.
     */
    data class BackupPayload(
        val version: Int,
        val createdAt: Long,
        val userCallRecords: List<UserCallRecord>,
        val preJudgeCacheEntries: List<PreJudgeCacheEntry>,
    )
}
