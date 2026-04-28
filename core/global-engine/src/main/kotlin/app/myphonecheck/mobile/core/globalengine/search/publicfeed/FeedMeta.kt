package app.myphonecheck.mobile.core.globalengine.search.publicfeed

/**
 * 출처 갱신 주기 (Architecture v2.1.0 §30).
 *
 * WorkManager schedule 등록 시 사용.
 */
enum class UpdateFrequency { HOURLY, DAILY, WEEKLY, ON_DEMAND }

/**
 * 출처 데이터 포맷.
 */
enum class FeedFormat { CSV, JSON_ARRAY, JSON_LINES, RSS, XML, PLAIN_TEXT }

/**
 * 출처 데이터 종류 (어떤 Surface에서 활용 가능한지).
 */
enum class FeedDataType {
    PHISHING_URL,
    MALWARE_HASH,
    PHONE_NUMBER,         // CallCheck용
    SMS_PATTERN,          // MessageCheck용
    APP_PACKAGE,          // PushCheck/MicCheck/CameraCheck
    THREAT_DESCRIPTION,
}
