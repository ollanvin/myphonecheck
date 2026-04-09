package app.myphonecheck.mobile.backup

import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 백업 파일의 경로, 정리, 디렉토리 관리를 담당한다.
 * 백업 파일은 공유 저장소에 보관: /storage/emulated/0/Documents/MyPhoneCheck/
 * 파일 형식: backup_YYYYMMDD.csb (MyPhoneCheck Secure Backup)
 */
@Singleton
class BackupFileManager @Inject constructor() {

    companion object {
        private const val BACKUP_DIR_NAME = "MyPhoneCheck"
        private const val FILE_EXTENSION = "csb"
        private const val MAX_BACKUPS = 5
    }

    /** 백업 디렉토리. 없으면 생성한다. */
    fun getBackupDir(): File {
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val dir = File(documentsDir, BACKUP_DIR_NAME)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    /** 새 백업 파일 경로를 생성한다. 날짜 기반 파일명. */
    fun createBackupFile(): File {
        val dateStr = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val baseFile = File(getBackupDir(), "backup_$dateStr.$FILE_EXTENSION")

        // 같은 날 여러 백업 시 넘버링
        if (!baseFile.exists()) return baseFile

        var index = 2
        while (true) {
            val numbered = File(getBackupDir(), "backup_${dateStr}_$index.$FILE_EXTENSION")
            if (!numbered.exists()) return numbered
            index++
        }
    }

    /** 기존 백업 파일 목록을 최신순으로 반환한다. */
    fun listBackups(): List<File> {
        return getBackupDir()
            .listFiles { file -> file.extension == FILE_EXTENSION }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    /** MAX_BACKUPS 초과 시 오래된 백업을 삭제한다. */
    fun pruneOldBackups() {
        val backups = listBackups()
        if (backups.size > MAX_BACKUPS) {
            backups.drop(MAX_BACKUPS).forEach { it.delete() }
        }
    }

    /** 특정 백업 파일을 삭제한다. */
    fun deleteBackup(file: File): Boolean {
        return file.delete()
    }

    /** 모든 백업 파일을 삭제한다. */
    fun deleteAllBackups() {
        listBackups().forEach { it.delete() }
    }

    /** 전체 백업 사용량(바이트)을 반환한다. */
    fun getTotalBackupSize(): Long {
        return listBackups().sumOf { it.length() }
    }
}
