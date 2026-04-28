package app.myphonecheck.mobile.core.globalengine.decision

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RealTimeActionEngineTest {

    private class FakeBlockList(private val blocked: Set<Pair<String, IdentifierType>>) : BlockListRepository {
        override suspend fun isBlocked(key: String, type: IdentifierType): Boolean =
            (key to type) in blocked
        override suspend fun add(key: String, type: IdentifierType, source: String) = Unit
        override suspend fun remove(key: String, type: IdentifierType) = Unit
        override suspend fun listAll(): List<BlockedIdentifier> = emptyList()
    }

    private class FakeTagRepo(private val record: TagRecord?) : TagRepository {
        override suspend fun findByKey(key: String, type: IdentifierType): TagRecord? = record
    }

    private class SlowBlockList(private val delayMillis: Long) : BlockListRepository {
        override suspend fun isBlocked(key: String, type: IdentifierType): Boolean {
            delay(delayMillis)
            return true
        }
        override suspend fun add(key: String, type: IdentifierType, source: String) = Unit
        override suspend fun remove(key: String, type: IdentifierType) = Unit
        override suspend fun listAll(): List<BlockedIdentifier> = emptyList()
    }

    @Test
    fun `BlockList match returns BLOCK with HIGH confidence`() = runTest {
        val engine = RealTimeActionEngine(
            blockList = FakeBlockList(setOf("+821012345678" to IdentifierType.PHONE_E164)),
            tagRepo = FakeTagRepo(null),
        )
        val decision = engine.decideForCall("+821012345678")
        assertEquals(ActionType.BLOCK, decision.action)
        assertEquals(MatchedSource.LAYER_2_BLOCKLIST, decision.matchedSource)
        assertEquals(ActionConfidence.HIGH, decision.confidence)
    }

    @Test
    fun `Tag SUSPICIOUS returns SILENT`() = runTest {
        val engine = RealTimeActionEngine(
            blockList = FakeBlockList(emptySet()),
            tagRepo = FakeTagRepo(
                TagRecord("k", IdentifierType.PHONE_E164, "조심", TagPriority.SUSPICIOUS, null),
            ),
        )
        val decision = engine.decideForCall("k")
        assertEquals(ActionType.SILENT, decision.action)
        assertEquals(MatchedSource.LAYER_2_TAG, decision.matchedSource)
        assertEquals("조심", decision.tag)
    }

    @Test
    fun `Tag REMIND_ME returns TAG_DISPLAY`() = runTest {
        val engine = RealTimeActionEngine(
            blockList = FakeBlockList(emptySet()),
            tagRepo = FakeTagRepo(
                TagRecord("k", IdentifierType.PHONE_E164, "리마인드", TagPriority.REMIND_ME, null),
            ),
        )
        val decision = engine.decideForCall("k")
        assertEquals(ActionType.TAG_DISPLAY, decision.action)
        assertEquals("리마인드", decision.tag)
    }

    @Test
    fun `Tag PENDING returns TAG_DISPLAY`() = runTest {
        val engine = RealTimeActionEngine(
            blockList = FakeBlockList(emptySet()),
            tagRepo = FakeTagRepo(
                TagRecord("k", IdentifierType.SMS_SENDER, "확인필요", TagPriority.PENDING, null),
            ),
        )
        val decision = engine.decideForSms("k")
        assertEquals(ActionType.TAG_DISPLAY, decision.action)
    }

    @Test
    fun `Tag ARCHIVE returns PASS but with tag set`() = runTest {
        val engine = RealTimeActionEngine(
            blockList = FakeBlockList(emptySet()),
            tagRepo = FakeTagRepo(
                TagRecord("k", IdentifierType.PHONE_E164, "보관", TagPriority.ARCHIVE, null),
            ),
        )
        val decision = engine.decideForCall("k")
        assertEquals(ActionType.PASS, decision.action)
        assertEquals(MatchedSource.LAYER_2_TAG, decision.matchedSource)
        assertEquals("보관", decision.tag)
    }

    @Test
    fun `No match returns PASS with NONE source`() = runTest {
        val engine = RealTimeActionEngine(
            blockList = FakeBlockList(emptySet()),
            tagRepo = FakeTagRepo(null),
        )
        val decision = engine.decideForCall("+821099999999")
        assertEquals(ActionType.PASS, decision.action)
        assertEquals(MatchedSource.NONE, decision.matchedSource)
    }

    @Test
    fun `BlockList outranks Tag SUSPICIOUS`() = runTest {
        val engine = RealTimeActionEngine(
            blockList = FakeBlockList(setOf("k" to IdentifierType.PHONE_E164)),
            tagRepo = FakeTagRepo(
                TagRecord("k", IdentifierType.PHONE_E164, "조심", TagPriority.SUSPICIOUS, null),
            ),
        )
        val decision = engine.decideForCall("k")
        assertEquals(ActionType.BLOCK, decision.action)
    }

    @Test
    fun `Slow repo exceeding 50ms triggers PASS fallback`() = runTest {
        // 60ms delay > 50ms timeout → withTimeoutOrNull null → PASS fallback.
        val engine = RealTimeActionEngine(
            blockList = SlowBlockList(60L),
            tagRepo = FakeTagRepo(null),
        )
        val decision = engine.decideForCall("any")
        assertEquals(ActionType.PASS, decision.action)
        assertEquals(MatchedSource.NONE, decision.matchedSource)
        assertEquals(ActionConfidence.LOW, decision.confidence)
    }

    @Test
    fun `decideForSms uses SMS_SENDER type`() = runTest {
        val engine = RealTimeActionEngine(
            blockList = FakeBlockList(setOf("1588" to IdentifierType.SMS_SENDER)),
            tagRepo = FakeTagRepo(null),
        )
        val decision = engine.decideForSms("1588")
        assertEquals(ActionType.BLOCK, decision.action)
    }

    @Test
    fun `decideForNotification uses NOTIFICATION_PACKAGE type`() = runTest {
        val engine = RealTimeActionEngine(
            blockList = FakeBlockList(setOf("com.spam.app" to IdentifierType.NOTIFICATION_PACKAGE)),
            tagRepo = FakeTagRepo(null),
        )
        val decision = engine.decideForNotification("com.spam.app")
        assertEquals(ActionType.BLOCK, decision.action)
    }

    @Test
    fun `Mismatched IdentifierType does not trigger block`() = runTest {
        // BlockList에 PHONE_E164 = "k" 등록되어 있지만 SMS_SENDER로 조회 → 매칭 안 됨.
        val engine = RealTimeActionEngine(
            blockList = FakeBlockList(setOf("k" to IdentifierType.PHONE_E164)),
            tagRepo = FakeTagRepo(null),
        )
        val decision = engine.decideForSms("k")
        assertEquals(ActionType.PASS, decision.action)
    }

    @Test
    fun `NoopTagRepository always returns null`() = runTest {
        val noop = NoopTagRepository()
        assertTrue(noop.findByKey("x", IdentifierType.PHONE_E164) == null)
    }
}
