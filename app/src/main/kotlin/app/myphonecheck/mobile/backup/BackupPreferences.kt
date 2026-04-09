package app.myphonecheck.mobile.backup

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.backupDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "backup_settings"
)

/**
 * 백업 관련 설정을 DataStore로 관리한다.
 *
 * SAF 트리 URI: 유저가 선택한 클라우드/외부 저장소 위치.
 * Google Drive / OneDrive / MYBOX 등 SAF를 지원하는 모든 위치에 대응.
 */
@Singleton
class BackupPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        /** SAF 트리 URI (문자열로 직렬화). 빈 문자열이면 미설정. */
        val KEY_SAF_TREE_URI = stringPreferencesKey("saf_tree_uri")
    }

    /** SAF 트리 URI Flow. null이면 미설정. */
    val safTreeUri: Flow<String?> = context.backupDataStore.data
        .map { prefs ->
            val uri = prefs[KEY_SAF_TREE_URI] ?: ""
            uri.ifBlank { null }
        }

    /** SAF 트리 URI를 저장한다. */
    suspend fun setSafTreeUri(uri: String) {
        context.backupDataStore.edit { prefs ->
            prefs[KEY_SAF_TREE_URI] = uri
        }
    }

    /** SAF 트리 URI를 초기화한다. */
    suspend fun clearSafTreeUri() {
        context.backupDataStore.edit { prefs ->
            prefs.remove(KEY_SAF_TREE_URI)
        }
    }
}
