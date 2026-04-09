package app.myphonecheck.mobile.feature.callintercept

interface BlocklistRepository {

    /**
     * Add a phone number to the local blocklist.
     *
     * @param phoneNumber The normalized phone number to block
     * @param reason The reason for blocking
     * @param timestamp When the block was created
     */
    suspend fun addToBlocklist(
        phoneNumber: String,
        reason: String,
        timestamp: Long
    )

    /**
     * Check if a phone number is blocked.
     *
     * @param phoneNumber The normalized phone number to check
     * @return True if the number is on the blocklist
     */
    suspend fun isBlocked(phoneNumber: String): Boolean

    /**
     * Remove a phone number from the blocklist.
     *
     * @param phoneNumber The normalized phone number to unblock
     */
    suspend fun removeFromBlocklist(phoneNumber: String)

    /**
     * Get all blocked numbers.
     *
     * @return List of blocked phone numbers
     */
    suspend fun getBlockedNumbers(): List<String>
}
