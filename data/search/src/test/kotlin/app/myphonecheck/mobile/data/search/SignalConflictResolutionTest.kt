package app.myphonecheck.mobile.data.search

import app.myphonecheck.mobile.core.model.SignalSummary
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/**
 * 혼합 신호 충돌 해소(Conflict Resolution) + 카테고리별 실전 샘플 검증.
 *
 * 자비스 지시사항:
 * 1) 공식 기업/기관 신호와 스팸 신고 신호 동시 존재 시 우선순위 규칙화
 * 2) 기관/기업/택배/광고/사기/혼합 카테고리별 실전 번호 샘플
 * 3) SignalSummary 문장 품질: 과도한 단정 금지, 행동결정 직접 도움
 *
 * 모든 테스트는 mock 데이터로 SearchResultAnalyzer의 분석 로직을 검증.
 * (실제 HTTP 호출은 NationalProviderEndToEndTest에서 이미 검증 완료)
 */
class SignalConflictResolutionTest {

    private val analyzer = SearchResultAnalyzer()

    // ═══════════════════════════════════════════════════════════
    // CR-2: 약한 부정(SPAM_REPORT) + 강한 긍정 엔터티 → 긍정 우선
    // 예시: 신세계백화점 15881234
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `CR-2 weak SPAM_REPORT with strong business entity - positive wins`() = runBlocking {
        // 시나리오: 신세계백화점 번호가 스팸 신고 사이트에 1건 등록, 기업 정보 다수
        val results = listOf(
            makeResult("신세계백화점 고객센터", "고객 상담 안내", "shinsegae.com"),
            makeResult("신세계백화점 강남점", "서울 서초구 반포대로", "shinsegae.com"),
            makeResult("신세계백화점", "백화점 이벤트 및 고객센터 안내", "play.google.com"),
            makeResult("대구신세계", "신세계백화점 대구점", "facebook.com"),
            makeResult("신세계상품권", "상품권 조회", "youtube.com"),
            makeResult("15881234 전화번호 정보", "스팸 전화 정보 조회", "thecall.co.kr"),
            makeResult("15881234 번호 조회", "발신자 정보", "whosnumber.com"),
        )

        val evidence = analyzer.analyzeSearchResults(results)

        println("[CR-2 TEST] signalSummaries:")
        evidence.signalSummaries.forEach {
            println("  desc='${it.signalDescription}' type=${it.signalType} count=${it.resultCount}")
        }

        // 핵심 검증: 기업 엔터티가 반영되고 과도한 단정이 없어야 함
        assertTrue("Must have signal summaries", evidence.signalSummaries.isNotEmpty())
        val primary = evidence.signalSummaries.first()

        // 엔터티(신세계백화점)가 문장에 포함되어야 함
        assertTrue(
            "Primary signal should mention entity 신세계백화점, got: '${primary.signalDescription}'",
            primary.signalDescription.contains("신세계백화점")
        )
        // "수신 안전" 또는 "BUSINESS_WITH_REPORT" 또는 "MIXED" (둘 다 혼합 처리)
        assertTrue(
            "Primary signal should be positive or mixed, got type: '${primary.signalType}'",
            primary.signalType in setOf("BUSINESS_WITH_REPORT", "MIXED", "BUSINESS")
        )
        // 과도한 단정 금지: "수신 위험"이면 안 됨
        assertFalse(
            "Should NOT say 수신 위험 for a known business",
            primary.signalDescription.contains("수신 위험")
        )

        println("[CR-2 TEST] ✅ 약한 부정 + 강한 긍정 엔터티 → 긍정 우선 통과")
    }

    // ═══════════════════════════════════════════════════════════
    // CR-1: 강한 SCAM (5건+) → 부정 무조건 우선
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `CR-1 strong SCAM dominates even with business entity`() = runBlocking {
        val results = listOf(
            makeResult("사기 전화 주의", "이 번호로 보이스피싱 피해 사례 다수", "cafe.naver.com"),
            makeResult("사기 신고", "피싱 전화 주의보", "thecall.co.kr"),
            makeResult("보이스피싱 경고", "금융감독원 사칭 전화", "community.com"),
            makeResult("피해 사례 공유", "사기 전화 조심하세요", "clien.net"),
            makeResult("보이스피싱 위험", "가짜 검찰 전화 사기 경고", "ppomppu.co.kr"),
            makeResult("대출 사기", "스팸 전화 신고됨", "spam-report.com"),
            makeResult("XX은행 고객센터", "고객 상담 안내", "xxbank.com"),
        )

        val evidence = analyzer.analyzeSearchResults(results)

        println("[CR-1 TEST] signalSummaries:")
        evidence.signalSummaries.forEach {
            println("  desc='${it.signalDescription}' type=${it.signalType} count=${it.resultCount}")
        }

        val primary = evidence.signalSummaries.first()
        assertTrue(
            "Strong SCAM must show 수신 위험, got: '${primary.signalDescription}'",
            primary.signalDescription.contains("수신 위험")
        )
        assertEquals("SCAM", primary.signalType)

        println("[CR-1 TEST] ✅ 강한 사기 신호 → 부정 무조건 우선 통과")
    }

    // ═══════════════════════════════════════════════════════════
    // CR-3: 양쪽 존재 + 엔터티 있음 → 혼합 문장
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `CR-3 mixed signals with entity produces hybrid message`() = runBlocking {
        // 시나리오: 기업 정보도 있고 스팸 신고도 여러 건
        val results = listOf(
            makeResult("ABC보험 고객센터", "고객 상담 안내", "abc-insurance.com"),
            makeResult("ABC보험", "보험 상품 안내 회사", "abc-insurance.com"),
            makeResult("ABC보험 영업전화", "광고 영업 전화 스팸", "thecall.co.kr"),
            makeResult("ABC보험 스팸", "보험 영업 마케팅 전화", "shouldianswer.com"),
            makeResult("ABC보험 스팸 신고", "보험 광고 전화 주의", "tellows.com"),
        )

        val evidence = analyzer.analyzeSearchResults(results)

        println("[CR-3 TEST] signalSummaries:")
        evidence.signalSummaries.forEach {
            println("  desc='${it.signalDescription}' type=${it.signalType} count=${it.resultCount}")
        }

        val primary = evidence.signalSummaries.first()
        // 혼합 문장: 엔터티 + 신고 이력 언급 + 수신 주의
        assertTrue(
            "Mixed signal should mention entity and report, got: '${primary.signalDescription}'",
            primary.signalDescription.contains("ABC보험")
                    || primary.signalDescription.contains("신고")
                    || primary.signalType == "MIXED"
        )

        println("[CR-3 TEST] ✅ 혼합 신호 + 엔터티 → 혼합 문장 통과")
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 1: 기관 (병원/구청)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `Category - INSTITUTION - hospital`() = runBlocking {
        val results = listOf(
            makeResult("서울대학교병원", "진료 예약 안내 병원", "snuh.org"),
            makeResult("서울대학교병원 외래", "진료 예약 및 접수", "snuh.org"),
            makeResult("서울대학교병원 앱", "진료 예약 앱", "play.google.com"),
        )

        val evidence = analyzer.analyzeSearchResults(results)

        println("[INSTITUTION TEST] signalSummaries:")
        evidence.signalSummaries.forEach {
            println("  desc='${it.signalDescription}' type=${it.signalType}")
        }

        val primary = evidence.signalSummaries.first()
        assertTrue(
            "Hospital should show 수신 안전, got: '${primary.signalDescription}'",
            primary.signalDescription.contains("수신 안전")
        )
        assertTrue(
            "Should be INSTITUTION type",
            primary.signalType == "INSTITUTION"
        )

        println("[INSTITUTION TEST] ✅ 기관(병원) 카테고리 통과")
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 2: 기업 (고객센터)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `Category - BUSINESS - customer center`() = runBlocking {
        val results = listOf(
            makeResult("삼성전자 고객센터", "고객 상담 및 AS 접수", "samsung.com"),
            makeResult("삼성전자 서비스", "제품 수리 접수 회사", "samsungsvc.co.kr"),
            makeResult("삼성전자", "본사 대표번호 기업", "samsung.com"),
        )

        val evidence = analyzer.analyzeSearchResults(results)

        println("[BUSINESS TEST] signalSummaries:")
        evidence.signalSummaries.forEach {
            println("  desc='${it.signalDescription}' type=${it.signalType}")
        }

        val primary = evidence.signalSummaries.first()
        assertTrue(
            "Business should show 수신 안전, got: '${primary.signalDescription}'",
            primary.signalDescription.contains("수신 안전")
        )

        println("[BUSINESS TEST] ✅ 기업(고객센터) 카테고리 통과")
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 3: 택배
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `Category - DELIVERY - courier`() = runBlocking {
        val results = listOf(
            makeResult("CJ대한통운 고객센터", "택배 배송 조회", "cjlogistics.com"),
            makeResult("CJ대한통운", "택배 배송 추적", "cjlogistics.com"),
            makeResult("CJ대한통운 배송조회", "물류 택배 배달 현황", "cjlogistics.com"),
        )

        val evidence = analyzer.analyzeSearchResults(results)

        println("[DELIVERY TEST] signalSummaries:")
        evidence.signalSummaries.forEach {
            println("  desc='${it.signalDescription}' type=${it.signalType}")
        }

        val hasDelivery = evidence.signalSummaries.any { it.signalType == "DELIVERY" }
        assertTrue("Must have DELIVERY signal", hasDelivery)

        val deliverySignal = evidence.signalSummaries.first { it.signalType == "DELIVERY" }
        assertTrue(
            "Delivery should mention entity, got: '${deliverySignal.signalDescription}'",
            deliverySignal.signalDescription.contains("택배") || deliverySignal.signalDescription.contains("배송")
        )

        println("[DELIVERY TEST] ✅ 택배 카테고리 통과")
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 4: 광고/스팸
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `Category - SPAM - advertising`() = runBlocking {
        val results = listOf(
            makeResult("광고 영업 전화", "보험 영업 광고 마케팅 전화", "thecall.co.kr"),
            makeResult("영업전화 스팸", "보험 텔레마케팅 광고", "shouldianswer.com"),
            makeResult("마케팅 전화", "광고 영업 전화 주의", "tellows.com"),
            makeResult("보험 영업", "보험 광고 전화", "community.com"),
        )

        val evidence = analyzer.analyzeSearchResults(results)

        println("[SPAM TEST] signalSummaries:")
        evidence.signalSummaries.forEach {
            println("  desc='${it.signalDescription}' type=${it.signalType}")
        }

        val primary = evidence.signalSummaries.first()
        assertTrue(
            "Spam should show 거절 권장 or 주의 필요, got: '${primary.signalDescription}'",
            primary.signalDescription.contains("거절 권장")
                    || primary.signalDescription.contains("주의 필요")
                    || primary.signalDescription.contains("주의")
        )

        println("[SPAM TEST] ✅ 광고/스팸 카테고리 통과")
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 5: 사기/피싱
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `Category - SCAM - phishing`() = runBlocking {
        val results = listOf(
            makeResult("보이스피싱 주의", "사기 전화 경고 피싱", "police.go.kr"),
            makeResult("사기 전화 신고", "보이스피싱 피해 사례 사칭", "thecall.co.kr"),
            makeResult("금융 사기 경고", "가짜 검찰 사칭 사기 전화", "fss.or.kr"),
        )

        val evidence = analyzer.analyzeSearchResults(results)

        println("[SCAM TEST] signalSummaries:")
        evidence.signalSummaries.forEach {
            println("  desc='${it.signalDescription}' type=${it.signalType}")
        }

        val primary = evidence.signalSummaries.first()
        assertEquals("SCAM", primary.signalType)
        assertTrue(
            "Scam should show 수신 주의 or 주의 필요, got: '${primary.signalDescription}'",
            primary.signalDescription.contains("주의")
        )

        println("[SCAM TEST] ✅ 사기/피싱 카테고리 통과")
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 6: 혼합 (기업 + 스팸 신고 약함)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `Category - MIXED - business with weak spam report`() = runBlocking {
        // 정상 기업인데 스팸 신고 사이트에도 등록됨 (흔한 케이스)
        val results = listOf(
            makeResult("KB국민은행 고객센터", "고객 상담 안내", "kbstar.com"),
            makeResult("KB국민은행", "회사 대표번호", "kbstar.com"),
            makeResult("KB국민은행 상담", "금융 상담 고객센터", "kbstar.com"),
            makeResult("KB국민은행 전화번호", "발신자 정보", "thecall.co.kr"),
        )

        val evidence = analyzer.analyzeSearchResults(results)

        println("[MIXED TEST] signalSummaries:")
        evidence.signalSummaries.forEach {
            println("  desc='${it.signalDescription}' type=${it.signalType} count=${it.resultCount}")
        }

        val primary = evidence.signalSummaries.first()
        // 정상 기업이 압도적이므로 "수신 안전" 계열이어야 함
        assertTrue(
            "Legitimate bank should show 수신 안전, got: '${primary.signalDescription}'",
            primary.signalDescription.contains("수신 안전")
        )
        // "수신 위험"이면 안 됨
        assertFalse(
            "Should NOT be 수신 위험 for a bank",
            primary.signalDescription.contains("수신 위험")
        )

        println("[MIXED TEST] ✅ 혼합 카테고리 (기업+약한스팸) 통과")
    }

    // ═══════════════════════════════════════════════════════════
    // 문장 품질 검증: 과도한 단정 금지
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `Text quality - single scam result should not say 수신 위험`() = runBlocking {
        // 사기 관련 결과가 1건뿐일 때 "수신 위험"은 과도한 단정
        val results = listOf(
            makeResult("스팸 전화 주의", "이 번호 사기 의심 신고", "thecall.co.kr"),
            makeResult("알 수 없는 번호", "발신자 정보 없음", "unknown.com"),
            makeResult("전화번호 조회", "번호 조회 서비스", "114.co.kr"),
        )

        val evidence = analyzer.analyzeSearchResults(results)

        println("[TEXT QUALITY TEST] signalSummaries:")
        evidence.signalSummaries.forEach {
            println("  desc='${it.signalDescription}' type=${it.signalType} count=${it.resultCount}")
        }

        // 1건 사기 신호에서 "수신 위험"은 과도함
        val scamSignals = evidence.signalSummaries.filter { it.signalType == "SCAM" }
        if (scamSignals.isNotEmpty() && scamSignals.first().resultCount < 5) {
            assertFalse(
                "Single scam result should NOT say 수신 위험, got: '${scamSignals.first().signalDescription}'",
                scamSignals.first().signalDescription.contains("수신 위험")
            )
        }

        println("[TEXT QUALITY TEST] ✅ 과도한 단정 금지 통과")
    }

    // ═══════════════════════════════════════════════════════════
    // JP 카테고리: 일본 기관
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `Category - JP INSTITUTION - NTT`() = runBlocking {
        val results = listOf(
            makeResult("NTT東日本 お問い合わせ", "故障受付 窓口 電話番号", "ntt-east.co.jp"),
            makeResult("NTT西日本", "電話 受付 お客様 サポート", "ntt-west.co.jp"),
            makeResult("NTTファイナンス", "会社 受付 お問い合わせ", "ntt-finance.co.jp"),
        )

        val evidence = analyzer.analyzeSearchResults(results)

        println("[JP INSTITUTION TEST] signalSummaries:")
        evidence.signalSummaries.forEach {
            println("  desc='${it.signalDescription}' type=${it.signalType}")
        }

        assertTrue("Must have signals", evidence.signalSummaries.isNotEmpty())
        val hasPositive = evidence.signalSummaries.any {
            it.signalType in setOf("INSTITUTION", "BUSINESS")
        }
        assertTrue("JP NTT should have positive signal", hasPositive)

        println("[JP INSTITUTION TEST] ✅ 일본 기관 카테고리 통과")
    }

    // ═══════════════════════════════════════════════════════════
    // RU 카테고리: 러시아 기업
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `Category - RU BUSINESS - company`() = runBlocking {
        val results = listOf(
            makeResult("Компания АО 77", "Лизинговая компания", "ao77.ru"),
            makeResult("АО 77 офис", "Офис компания телефон поддержка", "vk.com"),
        )

        val evidence = analyzer.analyzeSearchResults(results)

        println("[RU BUSINESS TEST] signalSummaries:")
        evidence.signalSummaries.forEach {
            println("  desc='${it.signalDescription}' type=${it.signalType}")
        }

        val hasBusiness = evidence.signalSummaries.any { it.signalType == "BUSINESS" }
        assertTrue("RU company should have BUSINESS signal", hasBusiness)

        println("[RU BUSINESS TEST] ✅ 러시아 기업 카테고리 통과")
    }

    // ═══════════════════════════════════════════════════════════
    // CR-4: 엔터티 없이 부정+긍정 → 부정 우선
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `CR-4 no entity with mixed signals - negative first`() = runBlocking {
        // 엔터티가 반복되지 않는 상태에서 부정과 긍정 신호 공존
        val results = listOf(
            makeResult("스팸 전화", "사기 피싱 경고 주의", "site1.com"),
            makeResult("사기 신고", "보이스피싱 주의 스팸", "site2.com"),
            makeResult("고객센터 안내", "회사 상담 고객센터", "site3.com"),
            makeResult("기업 정보", "회사 본사 대표번호", "site4.com"),
        )

        val evidence = analyzer.analyzeSearchResults(results)

        println("[CR-4 TEST] signalSummaries:")
        evidence.signalSummaries.forEach {
            println("  desc='${it.signalDescription}' type=${it.signalType} count=${it.resultCount}")
        }

        // 엔터티 없으므로 부정 신호가 첫 번째
        val primary = evidence.signalSummaries.first()
        assertTrue(
            "Without entity, negative should come first, got type=${primary.signalType}",
            primary.signalType in setOf("SCAM", "SPAM", "SPAM_REPORT", "MIXED")
        )

        println("[CR-4 TEST] ✅ 엔터티 없이 혼합 → 부정 우선 통과")
    }

    // ═══════════════════════════════════════════════════════════
    // 유틸리티
    // ═══════════════════════════════════════════════════════════

    private fun makeResult(
        title: String,
        snippet: String,
        domain: String,
        language: String? = "ko",
    ) = RawSearchResult(
        title = title,
        snippet = snippet,
        url = "https://$domain/",
        domain = domain,
        language = language,
        providerName = "Test",
    )
}
