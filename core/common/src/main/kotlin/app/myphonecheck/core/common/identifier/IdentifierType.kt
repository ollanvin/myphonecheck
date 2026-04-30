package app.myphonecheck.core.common.identifier

/**
 * Sealed hierarchy for six-surface inputs (v2.7.0 §7).
 * FREEZE: new subclasses require MAJOR version bump.
 */
sealed class IdentifierType {

    /**
     * CallCheck input: E.164 phone number (e.g. +821012345678).
     */
    data class PhoneNumber(val e164: String) : IdentifierType() {
        init {
            require(e164.startsWith("+")) {
                "E.164 must start with +: $e164"
            }
            require(e164.length in 8..16) {
                "E.164 length must be 8..16: $e164"
            }
        }
    }

    /**
     * MessageCheck input. SMS body must not be persisted in NKB (constitution).
     */
    data class SmsMessage(
        val sender: String,
        val body: String,
    ) : IdentifierType() {
        init {
            require(sender.isNotBlank()) { "SMS sender cannot be blank" }
            require(body.length <= 1_000) {
                "SMS body exceeds 1000 chars"
            }
        }
    }

    /**
     * MicCheck / CameraCheck: watched app. This app does not hold the permission itself.
     */
    data class AppPackage(
        val packageName: String,
        val permission: String,
    ) : IdentifierType() {
        init {
            require(packageName.contains(".")) {
                "Package name must contain dot: $packageName"
            }
            require(permission in ALLOWED_PERMISSIONS) {
                "Permission must be in whitelist: $permission"
            }
        }

        companion object {
            val ALLOWED_PERMISSIONS = setOf(
                "android.permission.RECORD_AUDIO",
                "android.permission.CAMERA",
            )
        }
    }
}
