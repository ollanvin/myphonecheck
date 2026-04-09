package app.myphonecheck.mobile.feature.countryconfig

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import java.util.Locale

/**
 * Implementation of CountryConfigProvider with built-in configurations.
 *
 * Supports:
 * - Korea (KR)
 * - USA (US)
 * - Japan (JP)
 * - China (CN)
 * - Default (English fallback)
 *
 * Country detection via:
 * 1. SIM card country (TelephonyManager)
 * 2. Network country (TelephonyManager)
 * 3. System locale
 * 4. Fallback to US
 */
/**
 * 191개국 글로벌 서비스를 위한 CountryConfig 제공자.
 *
 * 전략:
 * 1. 명시적 설정 국가 (KR, US, JP, CN): 전용 키워드 사전 + 검색 프로바이더
 * 2. 미지원 국가: 동적 fallback config 생성
 *    - libphonenumber에서 phonePrefix 자동 추출
 *    - 영어 키워드 사전 (글로벌 공통)
 *    - Google 기반 검색 프로바이더
 *    - 영어 UI 문자열
 *
 * 키워드 사전 확장 전략:
 * - V1: 영어 기본 사전으로 전 세계 커버
 * - V2: 주요 언어권(ES, FR, DE, PT, AR, HI) 네이티브 키워드 추가
 * - V3: 사용자 피드백 기반 키워드 확장
 */
class CountryConfigProviderImpl : CountryConfigProvider {

    private val configs = mapOf(
        "KR" to createKoreanConfig(),
        "US" to createUSConfig(),
        "JP" to createJapanConfig(),
        "CN" to createChinaConfig(),
    )

    /**
     * 국가 코드에 해당하는 설정 반환.
     *
     * 명시적 설정이 없는 국가는 동적 fallback config를 생성합니다.
     * 절대 하드코딩된 US config를 반환하지 않습니다.
     */
    override fun getConfig(countryCode: String): CountryConfig {
        val upper = countryCode.uppercase()
        return configs[upper] ?: createFallbackConfig(upper)
    }

    override fun getDefaultConfig(): CountryConfig {
        return createFallbackConfig("ZZ")
    }

    override fun detectCountry(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        // Try to get country from SIM card
        telephonyManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val simCountryIso = it.simCountryIso
                if (simCountryIso.isNotEmpty()) {
                    return simCountryIso.uppercase()
                }
            } else {
                @Suppress("DEPRECATION")
                val simCountryIso = it.simCountryIso
                if (simCountryIso.isNotEmpty()) {
                    return simCountryIso.uppercase()
                }
            }
        }

        // Try to get country from network
        telephonyManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val networkCountryIso = it.networkCountryIso
                if (networkCountryIso.isNotEmpty()) {
                    return networkCountryIso.uppercase()
                }
            } else {
                @Suppress("DEPRECATION")
                val networkCountryIso = it.networkCountryIso
                if (networkCountryIso.isNotEmpty()) {
                    return networkCountryIso.uppercase()
                }
            }
        }

        // Try to get country from system locale
        val country = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale.getDefault().country
        } else {
            @Suppress("DEPRECATION")
            Locale.getDefault().country
        }

        return if (country.isNotEmpty()) country.uppercase() else "US"
    }

    private fun createKoreanConfig(): CountryConfig {
        return CountryConfig(
            countryCode = "KR",
            language = "ko",
            phonePrefix = "+82",
            searchProviderPriority = listOf("naver", "nate", "daum"),
            keywordDictionary = KeywordDictionary(
                delivery = setOf(
                    "배송",
                    "택배",
                    "배달",
                    "쿠팡",
                    "마켓",
                    "이마트",
                    "롯데",
                    "대한통운",
                    "CJ",
                    "로젠",
                    "편의점",
                ),
                hospital = setOf(
                    "병원",
                    "의료",
                    "진료",
                    "예약",
                    "백신",
                    "건강검진",
                    "치과",
                    "약국",
                    "의사",
                    "간호",
                ),
                institution = setOf(
                    "국세청",
                    "경찰청",
                    "검찰청",
                    "법원",
                    "시청",
                    "구청",
                    "공공기관",
                    "은행",
                    "보험",
                    "교육청",
                    "학교",
                    "대학교",
                ),
                business = setOf(
                    "고객센터",
                    "서비스팀",
                    "매장",
                    "지점",
                    "가맹점",
                    "점주",
                    "사업가",
                    "영업",
                    "회사",
                    "부서",
                ),
                financeSpam = setOf(
                    "대출",
                    "금융",
                    "여신",
                    "신용",
                    "카드",
                    "이자",
                    "투자",
                    "주식",
                    "펀드",
                    "보험료",
                    "연금",
                ),
                scam = setOf(
                    "사기",
                    "피싱",
                    "보이스피싱",
                    "사칭",
                    "사건",
                    "고소",
                    "체포",
                    "위반",
                    "통장",
                    "비밀번호",
                    "카드번호",
                    "계좌",
                    "송금",
                    "입금",
                ),
                telemarketing = setOf(
                    "광고",
                    "홍보",
                    "이벤트",
                    "프로모션",
                    "할인",
                    "캠페인",
                    "설문",
                    "조사",
                    "통신판매",
                    "마케팅",
                ),
            ),
            uiStrings = createKoreanUiStrings(),
        )
    }

    private fun createUSConfig(): CountryConfig {
        return CountryConfig(
            countryCode = "US",
            language = "en",
            phonePrefix = "+1",
            searchProviderPriority = listOf("google", "truecaller", "whitepages"),
            keywordDictionary = KeywordDictionary(
                delivery = setOf(
                    "delivery",
                    "package",
                    "shipment",
                    "amazon",
                    "fedex",
                    "ups",
                    "dhl",
                    "usps",
                    "tracking",
                    "courier",
                ),
                hospital = setOf(
                    "hospital",
                    "doctor",
                    "clinic",
                    "medical",
                    "health",
                    "appointment",
                    "pharmacy",
                    "vaccine",
                    "nurse",
                    "therapy",
                ),
                institution = setOf(
                    "irs",
                    "social security",
                    "government",
                    "court",
                    "police",
                    "bank",
                    "credit union",
                    "school",
                    "university",
                    "public agency",
                ),
                business = setOf(
                    "customer service",
                    "business",
                    "office",
                    "company",
                    "sales",
                    "manager",
                    "representative",
                    "department",
                    "headquarters",
                ),
                financeSpam = setOf(
                    "loan",
                    "credit",
                    "finance",
                    "mortgage",
                    "refinance",
                    "investment",
                    "stock",
                    "insurance",
                    "debt",
                    "payment",
                ),
                scam = setOf(
                    "fraud",
                    "scam",
                    "phishing",
                    "impersonation",
                    "arrest",
                    "warrant",
                    "lawsuit",
                    "account",
                    "password",
                    "ssn",
                    "urgent",
                    "immediate",
                ),
                telemarketing = setOf(
                    "marketing",
                    "advertising",
                    "promotion",
                    "survey",
                    "research",
                    "offer",
                    "deal",
                    "limited time",
                    "call now",
                ),
            ),
            uiStrings = createEnglishUiStrings(),
        )
    }

    private fun createJapanConfig(): CountryConfig {
        return CountryConfig(
            countryCode = "JP",
            language = "ja",
            phonePrefix = "+81",
            searchProviderPriority = listOf("yahoo", "google", "line"),
            keywordDictionary = KeywordDictionary(
                delivery = setOf(
                    "配送",
                    "配達",
                    "荷物",
                    "アマゾン",
                    "ヤマト",
                    "佐川",
                    "郵便",
                    "追跡",
                ),
                hospital = setOf(
                    "病院",
                    "医者",
                    "診療",
                    "医療",
                    "健康",
                    "予約",
                    "薬局",
                    "ワクチン",
                ),
                institution = setOf(
                    "税務署",
                    "警察",
                    "裁判所",
                    "市役所",
                    "銀行",
                    "学校",
                    "大学",
                ),
                business = setOf(
                    "カスタマー",
                    "ビジネス",
                    "会社",
                    "営業",
                    "部署",
                ),
                financeSpam = setOf(
                    "ローン",
                    "クレジット",
                    "金融",
                    "投資",
                    "保険",
                    "返済",
                ),
                scam = setOf(
                    "詐欺",
                    "フィッシング",
                    "偽造",
                    "逮捕",
                    "口座",
                    "パスワード",
                    "緊急",
                ),
                telemarketing = setOf(
                    "広告",
                    "キャンペーン",
                    "セール",
                    "アンケート",
                    "調査",
                ),
            ),
            uiStrings = createEnglishUiStrings(), // Placeholder
        )
    }

    private fun createChinaConfig(): CountryConfig {
        return CountryConfig(
            countryCode = "CN",
            language = "zh",
            phonePrefix = "+86",
            searchProviderPriority = listOf("baidu", "qq", "sina"),
            keywordDictionary = KeywordDictionary(
                delivery = setOf(
                    "快递",
                    "物流",
                    "配送",
                    "包裹",
                    "顺丰",
                    "圆通",
                    "中通",
                ),
                hospital = setOf(
                    "医院",
                    "医生",
                    "诊疗",
                    "医疗",
                    "健康",
                    "挂号",
                    "药房",
                ),
                institution = setOf(
                    "税务",
                    "警察",
                    "法院",
                    "市政",
                    "银行",
                    "学校",
                    "大学",
                ),
                business = setOf(
                    "客服",
                    "业务",
                    "公司",
                    "销售",
                    "部门",
                ),
                financeSpam = setOf(
                    "贷款",
                    "信用",
                    "金融",
                    "投资",
                    "保险",
                    "还款",
                ),
                scam = setOf(
                    "诈骗",
                    "钓鱼",
                    "冒充",
                    "逮捕",
                    "账户",
                    "密码",
                    "紧急",
                ),
                telemarketing = setOf(
                    "广告",
                    "推广",
                    "折扣",
                    "调查",
                    "活动",
                ),
            ),
            uiStrings = createEnglishUiStrings(), // Placeholder
        )
    }

    /**
     * 미지원 국가를 위한 동적 fallback config 생성.
     *
     * - phonePrefix: libphonenumber에서 국가 코드 → 전화 접두사 자동 추출
     * - keywordDictionary: 영어 기본 사전 (글로벌 공통)
     * - searchProviderPriority: Google 기반 (전 세계 접근 가능)
     * - uiStrings: 영어
     *
     * @param countryCode ISO 3166-1 alpha-2 국가 코드
     */
    private fun createFallbackConfig(countryCode: String): CountryConfig {
        val phonePrefix = app.myphonecheck.mobile.core.util.PhoneNumberNormalizer
            .getPhonePrefix(countryCode) ?: "+1"

        val usConfig = createUSConfig()
        return CountryConfig(
            countryCode = countryCode,
            language = "en",
            phonePrefix = phonePrefix,
            searchProviderPriority = listOf("google", "truecaller"),
            keywordDictionary = usConfig.keywordDictionary,
            uiStrings = createEnglishUiStrings(),
        )
    }

    private fun createKoreanUiStrings(): UiStrings {
        return UiStrings(
            riskLevelSafe = "안전",
            riskLevelLow = "낮음",
            riskLevelMedium = "중간",
            riskLevelHigh = "높음",
            riskLevelCritical = "위험",
            callTypeDelivery = "배송/택배",
            callTypeHospital = "병원/의료",
            callTypeInstitution = "공공기관",
            callTypeBusiness = "사업체",
            callTypeFinanceSpam = "금융 스팸",
            callTypeScam = "보이스피싱",
            callTypeTelemarketing = "텔레마케팅",
            callTypeUnknown = "알 수 없음",
            actionAnswer = "받기",
            actionReject = "거절",
            actionBlock = "차단",
            appName = "MyPhoneCheck",
            settings = "설정",
            language = "언어",
            country = "국가",
            aboutUs = "정보",
            privacyPolicy = "개인정보 처리방침",
            termsOfService = "이용약관",
            contactUs = "피드백",
            version = "버전",
        )
    }

    private fun createEnglishUiStrings(): UiStrings {
        return UiStrings(
            riskLevelSafe = "Safe",
            riskLevelLow = "Low",
            riskLevelMedium = "Medium",
            riskLevelHigh = "High",
            riskLevelCritical = "Critical",
            callTypeDelivery = "Delivery",
            callTypeHospital = "Hospital",
            callTypeInstitution = "Institution",
            callTypeBusiness = "Business",
            callTypeFinanceSpam = "Finance Spam",
            callTypeScam = "Scam",
            callTypeTelemarketing = "Telemarketing",
            callTypeUnknown = "Unknown",
            actionAnswer = "Answer",
            actionReject = "Reject",
            actionBlock = "Block",
            appName = "MyPhoneCheck",
            settings = "Settings",
            language = "Language",
            country = "Country",
            aboutUs = "About",
            privacyPolicy = "Privacy Policy",
            termsOfService = "Terms of Service",
            contactUs = "Contact",
            version = "Version",
        )
    }
}
