package app.myphonecheck.mobile.core.util

import android.content.Context
import app.myphonecheck.mobile.core.model.DeviceNumberScanSnapshot
import app.myphonecheck.mobile.core.model.DeviceNumberScanSource
import app.myphonecheck.mobile.core.model.DevicePatternProfile
import app.myphonecheck.mobile.core.model.DevicePatternProfileScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class DevicePatternProfileBootstrapper(
    private val scanSource: DeviceNumberScanSource,
    private val scanner: DevicePatternProfileScanner = DefaultDevicePatternProfileScanner(),
) {
    suspend fun initialScan(
        defaultCountryCode: String? = null,
    ): DevicePatternProfile {
        val snapshot = DeviceNumberScanSnapshot(
            callHistoryNumbers = scanSource.recentCallHistoryNumbers(),
            smsSenderNumbers = scanSource.recentSmsSenderNumbers(),
            contactNumbers = scanSource.contactNumbers(),
            defaultCountryCode = defaultCountryCode,
        )
        val profile = scanner.scan(snapshot).copy(
            lastScannedAt = System.currentTimeMillis(),
        )
        GlobalNumberEngineProfileStore.update(profile)
        return profile
    }

    companion object {
        private val refreshInFlight = AtomicBoolean(false)
        private val refreshScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun needsRefresh(context: Context): Boolean {
            GlobalNumberEngineProfileStore.initialize(context)
            return GlobalNumberEngineProfileStore.needsRefresh()
        }

        fun requestManualRefresh(
            context: Context,
            reason: String = "manual_refresh",
        ) {
            GlobalNumberEngineProfileStore.initialize(context)
            GlobalNumberEngineProfileStore.requestManualRefresh(reason)
        }

        fun refreshIfNeededAsync(
            context: Context,
            defaultCountryCode: String? = null,
            scanner: DevicePatternProfileScanner = DefaultDevicePatternProfileScanner(),
        ) {
            GlobalNumberEngineProfileStore.initialize(context)
            if (!GlobalNumberEngineProfileStore.needsRefresh()) {
                return
            }
            if (!refreshInFlight.compareAndSet(false, true)) {
                return
            }

            val appContext = context.applicationContext
            refreshScope.launch {
                try {
                    initialScanFromDevice(
                        context = appContext,
                        defaultCountryCode = defaultCountryCode,
                        scanner = scanner,
                    )
                } finally {
                    refreshInFlight.set(false)
                }
            }
        }

        suspend fun initialScanFromDevice(
            context: Context,
            defaultCountryCode: String? = null,
            scanner: DevicePatternProfileScanner = DefaultDevicePatternProfileScanner(),
        ): DevicePatternProfile {
            GlobalNumberEngineProfileStore.initialize(context)
            val source = AndroidDeviceNumberScanSource(context.applicationContext)
            val fallbackReasons = buildList {
                if (!source.hasCallLogAccess()) add("missing_permission:READ_CALL_LOG")
                if (!source.hasContactsAccess()) add("missing_permission:READ_CONTACTS")
                if (!source.hasSmsAccess()) add("missing_permission:READ_SMS")
            }
            val activeSources = buildList {
                if (source.hasCallLogAccess()) add("call_log")
                if (source.hasContactsAccess()) add("contacts")
                if (source.hasSmsAccess()) add("sms")
            }
            val scanned = DevicePatternProfileBootstrapper(
                scanSource = source,
                scanner = scanner,
            ).initialScan(defaultCountryCode)
            val profile = scanned.copy(
                activeSources = activeSources,
                fallbackReasons = fallbackReasons,
                lastScannedAt = scanned.lastScannedAt ?: System.currentTimeMillis(),
            )
            GlobalNumberEngineProfileStore.update(profile)
            return profile
        }
    }
}
