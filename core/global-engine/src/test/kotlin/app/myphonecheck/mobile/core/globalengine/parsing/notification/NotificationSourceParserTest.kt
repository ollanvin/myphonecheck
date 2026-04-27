package app.myphonecheck.mobile.core.globalengine.parsing.notification

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * NotificationSourceParser raw 입력 검증 (JVM 호환).
 *
 * StatusBarNotification overload는 인스트루먼트 환경에서 검증 — 본 테스트는 raw overload만.
 */
class NotificationSourceParserTest {

    private val parser = NotificationSourceParser()

    @Test
    fun `parseRaw retains all fields`() {
        val src = parser.parseRaw(
            packageName = "com.bank.example",
            channelId = "transactions",
            postTime = 1_700_000_000L,
            id = 42,
            tag = "txn-42",
        )
        assertEquals("com.bank.example", src.packageName)
        assertEquals("transactions", src.channelId)
        assertEquals(1_700_000_000L, src.postTime)
        assertEquals(42, src.id)
        assertEquals("txn-42", src.tag)
    }

    @Test
    fun `parseRaw allows empty channel and tag`() {
        val src = parser.parseRaw(
            packageName = "com.app",
            channelId = "",
            postTime = 0L,
            id = 0,
            tag = "",
        )
        assertEquals("", src.channelId)
        assertEquals("", src.tag)
    }

    @Test
    fun `parseRaw preserves package name for app filtering decisions`() {
        val src = parser.parseRaw("com.target", "ch", 100L, 1, "")
        assertEquals("com.target", src.packageName)
    }
}
