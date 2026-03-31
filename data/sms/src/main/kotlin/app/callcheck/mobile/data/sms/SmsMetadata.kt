package app.callcheck.mobile.data.sms

data class SmsMetadata(
    val smsExists: Boolean = false,
    val smsCount: Int = 0,
    val smsLastAt: Long? = null,
    val hasIncoming: Boolean = false,
    val hasOutgoing: Boolean = false,
)
