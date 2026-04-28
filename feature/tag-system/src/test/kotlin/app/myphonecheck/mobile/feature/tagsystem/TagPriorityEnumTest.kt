package app.myphonecheck.mobile.feature.tagsystem

import app.myphonecheck.mobile.core.globalengine.decision.TagPriority
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 코어 TagPriority enum의 4 값과 순서 검증.
 * UI section 그룹 순서 정합 (REMIND_ME / PENDING / SUSPICIOUS / ARCHIVE).
 */
class TagPriorityEnumTest {

    @Test
    fun `enum has exactly 4 priorities`() {
        assertEquals(4, TagPriority.values().size)
    }

    @Test
    fun `enum order matches §32 spec`() {
        val expected = arrayOf(
            TagPriority.REMIND_ME,
            TagPriority.PENDING,
            TagPriority.SUSPICIOUS,
            TagPriority.ARCHIVE,
        )
        assertEquals(expected.toList(), TagPriority.values().toList())
    }

    @Test
    fun `valueOf round-trip preserves all values`() {
        TagPriority.values().forEach { p ->
            assertEquals(p, TagPriority.valueOf(p.name))
        }
    }
}
