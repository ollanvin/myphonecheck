package app.myphonecheck.mobile.core.globalengine.parsing.message

import org.junit.Assert.assertEquals
import org.junit.Test

class SenderRegistryTest {

    private val registry = SenderRegistry()

    private fun result(sender: String, category: MessageCategory, isShort: Boolean = false): MessageParseResult {
        val features = MessageFeatures(
            sender = sender,
            isShortSender = isShort,
            hasUrl = false,
            hasCurrencyPattern = false,
            bodyLength = 0,
            urlCount = 0,
            countryHint = "ZZ",
        )
        return MessageParseResult(features, category)
    }

    @Test
    fun `record increments count and tracks last seen`() {
        registry.record(result("1588", MessageCategory.NOTIFICATION, isShort = true), 100)
        registry.record(result("1588", MessageCategory.NOTIFICATION, isShort = true), 200)
        val profile = registry.snapshot().single()
        assertEquals(2, profile.count)
        assertEquals(200, profile.lastSeenMillis)
    }

    @Test
    fun `dominant category reflects highest count`() {
        registry.record(result("BANK", MessageCategory.NOTIFICATION), 100)
        registry.record(result("BANK", MessageCategory.PAYMENT_CANDIDATE), 200)
        registry.record(result("BANK", MessageCategory.PAYMENT_CANDIDATE), 300)
        val profile = registry.snapshot().single()
        assertEquals(MessageCategory.PAYMENT_CANDIDATE, profile.dominantCategory())
    }

    @Test
    fun `snapshot sorted by count descending`() {
        registry.record(result("a", MessageCategory.NORMAL), 1)
        registry.record(result("b", MessageCategory.NORMAL), 2)
        registry.record(result("b", MessageCategory.NORMAL), 3)
        registry.record(result("c", MessageCategory.NORMAL), 4)
        registry.record(result("c", MessageCategory.NORMAL), 5)
        registry.record(result("c", MessageCategory.NORMAL), 6)
        val sorted = registry.snapshot().map { it.sender }
        assertEquals(listOf("c", "b", "a"), sorted)
    }

    @Test
    fun `clear empties registry`() {
        registry.record(result("x", MessageCategory.NORMAL), 1)
        registry.clear()
        assertEquals(0, registry.snapshot().size)
    }
}
