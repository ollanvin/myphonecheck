package app.myphonecheck.mobile.data.localcache

import app.myphonecheck.mobile.core.model.UserCallAction
import app.myphonecheck.mobile.core.model.UserCallTag
import app.myphonecheck.mobile.data.localcache.entity.UserCallRecord
import org.junit.Assert.*
import org.junit.Test

/**
 * UserCallRecord 스키마 및 데이터 모델 검증 테스트.
 *
 * Room DB의 instrumented test는 아니지만,
 * Entity 구조, enum 매핑, 데이터 무결성을 검증한다.
 */
class UserCallRecordSchemaTest {

    @Test
    fun `Entity 기본 생성 — 필수 필드만으로 생성 가능`() {
        val record = UserCallRecord(
            canonicalNumber = "+821012345678",
            displayNumber = "010-1234-5678",
        )

        assertEquals("+821012345678", record.canonicalNumber)
        assertEquals("010-1234-5678", record.displayNumber)
        assertNull(record.tag)
        assertNull(record.memo)
        assertNull(record.lastAction)
        assertNull(record.aiRiskLevel)
        assertEquals(1, record.callCount)
        assertTrue(record.createdAt > 0)
        assertTrue(record.updatedAt > 0)
    }

    @Test
    fun `Entity 전체 필드 — 메모, 태그, 행동 모두 저장`() {
        val now = System.currentTimeMillis()
        val record = UserCallRecord(
            id = 1,
            canonicalNumber = "+821012345678",
            displayNumber = "010-1234-5678",
            tag = UserCallTag.SPAM.displayKey,
            memo = "보험 영업 전화",
            lastAction = UserCallAction.REJECTED.displayKey,
            aiRiskLevel = "HIGH",
            aiCategory = "SCAM_RISK_HIGH",
            callCount = 3,
            createdAt = now,
            updatedAt = now,
        )

        assertEquals("spam", record.tag)
        assertEquals("보험 영업 전화", record.memo)
        assertEquals("rejected", record.lastAction)
        assertEquals("HIGH", record.aiRiskLevel)
        assertEquals("SCAM_RISK_HIGH", record.aiCategory)
        assertEquals(3, record.callCount)
    }

    @Test
    fun `UserCallTag enum — 6개 태그 존재`() {
        val tags = UserCallTag.values()
        assertEquals(6, tags.size)

        assertEquals("safe", UserCallTag.SAFE.displayKey)
        assertEquals("spam", UserCallTag.SPAM.displayKey)
        assertEquals("business", UserCallTag.BUSINESS.displayKey)
        assertEquals("personal", UserCallTag.PERSONAL.displayKey)
        assertEquals("delivery", UserCallTag.DELIVERY.displayKey)
        assertEquals("custom", UserCallTag.CUSTOM.displayKey)
    }

    @Test
    fun `UserCallAction enum — 4개 행동 존재`() {
        val actions = UserCallAction.values()
        assertEquals(4, actions.size)

        assertEquals("answered", UserCallAction.ANSWERED.displayKey)
        assertEquals("rejected", UserCallAction.REJECTED.displayKey)
        assertEquals("blocked", UserCallAction.BLOCKED.displayKey)
        assertEquals("missed", UserCallAction.MISSED.displayKey)
    }

    @Test
    fun `Entity copy — callCount 증가 시뮬레이션`() {
        val original = UserCallRecord(
            canonicalNumber = "+821012345678",
            displayNumber = "010-1234-5678",
            callCount = 5,
        )

        val updated = original.copy(
            callCount = original.callCount + 1,
            lastAction = UserCallAction.ANSWERED.displayKey,
            updatedAt = System.currentTimeMillis(),
        )

        assertEquals(6, updated.callCount)
        assertEquals("answered", updated.lastAction)
        assertEquals(original.canonicalNumber, updated.canonicalNumber)
    }

    @Test
    fun `Entity — AI 판단과 사용자 판단 독립성`() {
        // AI는 HIGH RISK로 판단했지만 사용자는 SAFE로 태깅
        val record = UserCallRecord(
            canonicalNumber = "+821012345678",
            displayNumber = "010-1234-5678",
            tag = UserCallTag.SAFE.displayKey,           // 사용자: 안전
            aiRiskLevel = "HIGH",                         // AI: 위험
            aiCategory = "SCAM_RISK_HIGH",                // AI: 사기
            memo = "거래처 담당자 — AI가 잘못 판단함",
            lastAction = UserCallAction.ANSWERED.displayKey,
        )

        // AI 판단과 사용자 판단이 독립적으로 저장
        assertEquals("safe", record.tag)
        assertEquals("HIGH", record.aiRiskLevel)
        assertNotEquals(record.tag, record.aiRiskLevel?.lowercase())
    }

    @Test
    fun `차단 기록 — blocked action 저장`() {
        val record = UserCallRecord(
            canonicalNumber = "+821098765432",
            displayNumber = "010-9876-5432",
            tag = UserCallTag.SPAM.displayKey,
            memo = "사기였음 — 다시 받지 말 것",
            lastAction = UserCallAction.BLOCKED.displayKey,
            aiRiskLevel = "HIGH",
            callCount = 1,
        )

        assertEquals("blocked", record.lastAction)
        assertEquals("spam", record.tag)
        assertTrue(record.memo!!.contains("사기"))
    }
}
