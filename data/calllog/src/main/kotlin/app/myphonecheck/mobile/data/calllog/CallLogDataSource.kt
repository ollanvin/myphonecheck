package app.myphonecheck.mobile.data.calllog

interface CallLogDataSource {
    suspend fun getCallHistory(normalizedNumber: String): CallHistoryDetail
}
