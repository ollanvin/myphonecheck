package app.myphonecheck.mobile.feature.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telecom.TelecomManager
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PermissionStatus(
    val contactsPermission: Boolean = false,
    val callLogPermission: Boolean = false,
    val defaultCallerIdApp: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val settings: Flow<AppSettings> = settingsRepository.settings

    private val _permissionStatus = MutableStateFlow(PermissionStatus())
    val permissionStatus: StateFlow<PermissionStatus> = _permissionStatus.asStateFlow()

    init {
        updatePermissionStatus()
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.updateLanguage(language)
        }
    }

    fun updateCountryOverride(country: String?) {
        viewModelScope.launch {
            settingsRepository.updateCountryOverride(country)
        }
    }

    fun updateEvidenceDisplayLevel(level: String) {
        viewModelScope.launch {
            settingsRepository.updateEvidenceDisplayLevel(level)
        }
    }

    fun updatePermissionStatus() {
        _permissionStatus.value = PermissionStatus(
            contactsPermission = hasContactsPermission(),
            callLogPermission = hasCallLogPermission(),
            defaultCallerIdApp = isDefaultCallerIdApp(),
        )
    }

    fun requestContactsPermission(): Boolean {
        return !hasContactsPermission()
    }

    fun requestCallLogPermission(): Boolean {
        return !hasCallLogPermission()
    }

    fun openAppPermissionSettings() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = android.net.Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    fun setAsDefaultCallerIdApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
            telecomManager?.let {
                it.createManageBlockedNumbersIntent()?.let { intent ->
                    context.startActivity(intent)
                }
            }
        }
    }

    private fun hasContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** v4.3: READ_CALL_LOG removed — always returns false */
    private fun hasCallLogPermission(): Boolean = false

    private fun isDefaultCallerIdApp(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
            return telecomManager?.defaultDialerPackage == context.packageName
        }
        return false
    }
}
