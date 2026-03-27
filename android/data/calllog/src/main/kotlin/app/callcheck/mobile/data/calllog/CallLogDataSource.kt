package app.callcheck.mobile.data.calllog

interface CallLogDataSource {
    suspend fun getCallHistory(normalizedNumber: String): CallHistoryDetail
}
