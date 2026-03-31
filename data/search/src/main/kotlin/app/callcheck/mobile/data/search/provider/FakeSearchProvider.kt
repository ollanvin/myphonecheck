package app.callcheck.mobile.data.search.provider

import app.callcheck.mobile.data.search.RawSearchResult
import app.callcheck.mobile.data.search.SearchProvider
import app.callcheck.mobile.data.search.SearchProviderResult
import kotlinx.coroutines.delay
import kotlin.random.Random

class FakeSearchProvider(
    override val providerName: String = "FakeSearchProvider"
) : SearchProvider {

    override suspend fun search(
        phoneNumber: String,
        countryCode: String?
    ): SearchProviderResult {
        val startTime = System.currentTimeMillis()

        // Simulate network delay (50-150ms)
        delay(Random.nextLong(50, 150))

        val results = when {
            // Delivery company pattern (starts with certain digits or keywords)
            phoneNumber.contains(Regex("^0\\d{1,2}[0-9]{2,4}$")) -> generateDeliveryResults()
            phoneNumber.endsWith("1122") -> generateSpamResults()
            phoneNumber.startsWith("02") && phoneNumber.length == 10 -> generateBusinessResults()
            phoneNumber.contains(Regex("\\d{3}")) -> generateMixedResults()
            else -> generateUnknownResults()
        }

        val responseTime = System.currentTimeMillis() - startTime

        return SearchProviderResult(
            provider = providerName,
            results = results,
            responseTimeMs = responseTime,
            success = true,
            error = null
        )
    }

    private fun generateDeliveryResults(): List<RawSearchResult> {
        return listOf(
            RawSearchResult(
                title = "CJ대한통운 배송 조회 - 택배 배송현황",
                snippet = "CJ대한통운 배송 추적 및 배송 현황을 확인하세요. 신속하고 안전한 택배 배송 서비스",
                url = "https://www.cj.co.kr/tracking",
                domain = "cj.co.kr",
                language = "ko"
            ),
            RawSearchResult(
                title = "택배 배송 조회 - 한진 로지스틱스",
                snippet = "한진택배 배송현황 조회. 편리한 택배 추적 서비스로 배송상황을 실시간으로 확인할 수 있습니다.",
                url = "https://www.hanjin.co.kr/track",
                domain = "hanjin.co.kr",
                language = "ko"
            ),
            RawSearchResult(
                title = "배송 서비스 - 우체국 택배",
                snippet = "우정사업본부의 우체국택배 배송 조회 및 요금 안내",
                url = "https://www.epost.go.kr/delivery",
                domain = "epost.go.kr",
                language = "ko"
            ),
            RawSearchResult(
                title = "롯데택배 | 배송조회",
                snippet = "롯데택배 배송 조회 시스템. 실시간 배송 현황을 확인하세요.",
                url = "https://www.lotteglobal.com/tracking",
                domain = "lotteglobal.com",
                language = "ko"
            ),
            RawSearchResult(
                title = "DHL Express Korea - Tracking",
                snippet = "Track your DHL shipments in real time. Get delivery updates and proof of delivery.",
                url = "https://www.dhl.co.kr/tracking",
                domain = "dhl.co.kr",
                language = "ko"
            )
        )
    }

    private fun generateSpamResults(): List<RawSearchResult> {
        return listOf(
            RawSearchResult(
                title = "뭐야이번호 - 스팸 신고 데이터베이스",
                snippet = "이 번호는 보이스피싱으로 신고된 악성 번호입니다. 여러 사용자가 사기 피해를 보고했습니다.",
                url = "https://www.whoyacall.com/number/01012345678",
                domain = "whoyacall.com",
                language = "ko"
            ),
            RawSearchResult(
                title = "The Call - 스팸 번호 신고 커뮤니티",
                snippet = "01012345678 - 사기 피해자들이 신고한 번호. 절대 응답하지 마세요. 개인정보 요구 시도.",
                url = "https://www.thecall.co.kr/spam/01012345678",
                domain = "thecall.co.kr",
                language = "ko"
            ),
            RawSearchResult(
                title = "보이스피싱 주의 - 01012345678 피싱 사기 패턴",
                snippet = "최근 이 번호에서 은행 직원 사칭으로 계좌 이체 시도. 경찰에 신고됨. 관련 피해 사례 100건 이상",
                url = "https://naver.com/search?q=01012345678+보이스피싱",
                domain = "naver.com",
                language = "ko"
            ),
            RawSearchResult(
                title = "[주의] 보이스피싱 사기 번호 정보 공유",
                snippet = "블라인드, 오늘의유머 등에서 신고된 악성 번호. 친구라고 사칭하며 돈 이체 요청.",
                url = "https://clien.net/service/search?q=01012345678",
                domain = "clien.net",
                language = "ko"
            ),
            RawSearchResult(
                title = "경찰청 사이버수사대 - 사기 신고 접수",
                snippet = "보이스피싱 사기 신고. 이 번호는 경찰에 신고되었으며 수사 중입니다.",
                url = "https://www.npa.go.kr/fraud/report",
                domain = "npa.go.kr",
                language = "ko"
            )
        )
    }

    private fun generateBusinessResults(): List<RawSearchResult> {
        return listOf(
            RawSearchResult(
                title = "서울특별시 중구청 대표번호 및 부서",
                snippet = "서울 중구청 본청 대표전화 및 각 부서 연락처. 민원 상담, 행정 지원",
                url = "https://junggu.seoul.go.kr/contact",
                domain = "junggu.seoul.go.kr",
                language = "ko"
            ),
            RawSearchResult(
                title = "공공기관 안내 - 중구청",
                snippet = "서울시 중구 주민센터, 보건소, 공공기관 안내",
                url = "https://www.seoul.go.kr/dept/junggu",
                domain = "seoul.go.kr",
                language = "ko"
            ),
            RawSearchResult(
                title = "삼성전자 고객센터 - 대표번호",
                snippet = "삼성전자 제품 A/S 및 고객상담 안내",
                url = "https://www.samsung.com/support/ko",
                domain = "samsung.com",
                language = "ko"
            ),
            RawSearchResult(
                title = "카카오 고객지원 센터 - 문의하기",
                snippet = "카카오 서비스 관련 고객 문의 및 피드백",
                url = "https://www.kakao.com/support",
                domain = "kakao.com",
                language = "ko"
            ),
            RawSearchResult(
                title = "병원 안내 - 서울대학교병원",
                snippet = "신뢰할 수 있는 의료 서비스. 진료 예약 및 상담 전화",
                url = "https://www.snubh.org/contact",
                domain = "snubh.org",
                language = "ko"
            )
        )
    }

    private fun generateMixedResults(): List<RawSearchResult> {
        return listOf(
            RawSearchResult(
                title = "홈쇼핑 영업 전화 주의",
                snippet = "이 번호는 홈쇼핑 채널의 영업 전화로 보고되었습니다. 광고 및 상품 판매 목적",
                url = "https://www.who-calls.com/number",
                domain = "who-calls.com",
                language = "ko"
            ),
            RawSearchResult(
                title = "이동통신사 고객센터",
                snippet = "SKT 고객 상담 및 요금제 안내",
                url = "https://www.skt.co.kr/support",
                domain = "skt.co.kr",
                language = "ko"
            ),
            RawSearchResult(
                title = "보험 대출 영업 전화 - 주의",
                snippet = "금리 관련 문의 전화로 보고됨. 구매 강요 패턴 신고 다수",
                url = "https://naver.com/search?q=금리+대출+영업전화",
                domain = "naver.com",
                language = "ko"
            ),
            RawSearchResult(
                title = "신용카드 가입 제안 전화",
                snippet = "신용카드 가입 및 혜택 안내 전화. 정당한 금융기관 전화",
                url = "https://www.creditcard-info.co.kr",
                domain = "creditcard-info.co.kr",
                language = "ko"
            )
        )
    }

    private fun generateUnknownResults(): List<RawSearchResult> {
        return listOf(
            RawSearchResult(
                title = "검색 결과 없음 - 통화 기록 부족",
                snippet = "이 번호에 대한 정보가 많지 않습니다.",
                url = "https://www.who-calls.com/unknown",
                domain = "who-calls.com",
                language = "ko"
            )
        )
    }
}
