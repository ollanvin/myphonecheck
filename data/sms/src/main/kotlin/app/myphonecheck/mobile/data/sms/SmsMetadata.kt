package app.myphonecheck.mobile.data.sms

data class SmsMetadata(
    val smsExists: Boolean = false,
    val smsCount: Int = 0,
    val smsLastAt: Long? = null,
    val smsIncomingCount: Int = 0,
    val smsOutgoingCount: Int = 0,
    val hasIncoming: Boolean = false,
    val hasOutgoing: Boolean = false,
    val smsLastContent: String? = null,
)
