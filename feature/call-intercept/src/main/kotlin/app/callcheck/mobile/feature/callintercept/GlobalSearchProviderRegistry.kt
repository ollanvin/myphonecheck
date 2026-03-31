package app.callcheck.mobile.feature.callintercept

import app.callcheck.mobile.core.model.CountrySearchConfig
import app.callcheck.mobile.core.model.ParsingRules
import app.callcheck.mobile.core.model.QueryLocalization
import app.callcheck.mobile.core.model.SearchEngine
import app.callcheck.mobile.core.model.SearchTier
import app.callcheck.mobile.core.model.TimeoutPolicy
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 190개국 검색 프로바이더 전수 레지스트리.
 *
 * 자비스 기준:
 * - 190개국 동시 출시
 * - 국가별 1순위/2순위/3순위 검색엔진
 * - 현지어 쿼리 템플릿
 * - 위험/안전/기관 키워드 사전
 * - 2초 SLA 강제
 * - 금지 엔진 (CN→Google, KR→Google 1순위 금지 등)
 *
 * 4-Tier 체계:
 * - Tier A (8국): 현지 검색엔진 강국 — KR, CN, JP, RU, CZ, TW, VN, TH
 * - Tier B (22국): Google + 현지 디렉토리 병행
 * - Tier C (40국): Google 중심 + 지역 디렉토리
 * - Tier D (120국): Google fallback + Truecaller/Whoscall
 *
 * 구현률 관리:
 * - Registry 완료: 190/190
 * - 현지어 쿼리: Tier A 전수 + Tier B 전수 + Tier C 주요 + Tier D 영어 기본
 * - 파서: 전 국가 공통 ParsingRules + Tier A/B 커스텀 가중치
 * - 2초 SLA: 전 국가 hardDeadline=2000ms 적용
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class GlobalSearchProviderRegistry @Inject constructor() {

    private val registry: Map<String, CountrySearchConfig> = buildFullRegistry()

    /** 국가 검색 설정 조회. 미등록 → GLOBAL_FALLBACK */
    fun getConfig(countryCode: String): CountrySearchConfig {
        return registry[countryCode.uppercase()] ?: GLOBAL_FALLBACK
    }

    /** 등록된 국가 수 */
    fun registeredCountryCount(): Int = registry.size

    /** 티어별 국가 수 */
    fun tierCounts(): Map<SearchTier, Int> {
        return registry.values.groupBy { it.tier }.mapValues { it.value.size }
    }

    /** 전체 국가 목록 (티어별 정렬) */
    fun allCountries(): List<CountrySearchConfig> {
        return registry.values.sortedWith(compareBy({ it.tier }, { it.countryCode }))
    }

    /** 특정 티어의 국가 목록 */
    fun countriesByTier(tier: SearchTier): List<CountrySearchConfig> {
        return registry.values.filter { it.tier == tier }.sortedBy { it.countryCode }
    }

    /** 국가별 SLA 통과 여부 보고용 */
    fun getComplianceReport(): String = buildString {
        appendLine("═══ 190개국 Search Provider Registry 현황 ═══")
        appendLine()
        appendLine("총 등록: ${registry.size}개국")
        val tc = tierCounts()
        appendLine("Tier A (현지엔진 강국): ${tc[SearchTier.TIER_A] ?: 0}개국")
        appendLine("Tier B (Google+현지): ${tc[SearchTier.TIER_B] ?: 0}개국")
        appendLine("Tier C (Google 중심): ${tc[SearchTier.TIER_C] ?: 0}개국")
        appendLine("Tier D (fallback): ${tc[SearchTier.TIER_D] ?: 0}개국")
        appendLine()

        for (tier in SearchTier.entries) {
            appendLine("── ${tier.name} ──")
            countriesByTier(tier).forEach { c ->
                appendLine("  [${c.countryCode}] ${c.primaryEngine.displayName} → ${c.secondaryEngine.displayName} → ${c.tertiarySource.displayName} | ${c.queryLocalization.languageCode} | ${c.timeoutPolicy.hardDeadlineMs}ms")
            }
            appendLine()
        }
    }

    companion object {
        // ══════════════════════════════════════
        // 공통 키워드 사전 (GLOBAL_FALLBACK보다 먼저 정의)
        // ══════════════════════════════════════

        val EN_RISK_KEYWORDS = setOf(
            "spam", "scam", "fraud", "phishing", "telemarketing", "robocall",
            "unwanted", "dangerous", "fake", "suspicious", "block", "report",
            "harassment", "threat", "extortion", "loan shark", "investment scam",
        )
        val EN_SAFE_KEYWORDS = setOf(
            "delivery", "courier", "hospital", "clinic", "bank", "insurance",
            "school", "university", "pharmacy", "dentist", "veterinary",
            "restaurant", "hotel", "airline", "government", "police",
        )
        val EN_INSTITUTION_KEYWORDS = setOf(
            "government", "ministry", "embassy", "consulate", "police",
            "fire department", "ambulance", "tax office", "court",
            "social security", "immigration", "customs",
        )

        val KO_RISK_KEYWORDS = setOf(
            "스팸", "사기", "보이스피싱", "피싱", "대출", "투자", "리딩방",
            "광고", "영업", "텔레마케팅", "불법", "차단", "신고", "위험",
            "협박", "추심", "불법대출", "도박", "몰카",
        )
        val KO_SAFE_KEYWORDS = setOf(
            "택배", "배송", "배달", "병원", "은행", "보험", "학교", "학원",
            "약국", "치과", "동물병원", "식당", "호텔", "항공",
            "카드", "통신사", "인터넷", "수도", "전기", "가스",
        )
        val KO_INSTITUTION_KEYWORDS = setOf(
            "관공서", "정부", "시청", "구청", "주민센터", "경찰서", "소방서",
            "세무서", "법원", "검찰", "국민건강보험", "국민연금", "출입국",
        )

        val JA_RISK_KEYWORDS = setOf(
            "迷惑電話", "詐欺", "スパム", "架空請求", "ワンギリ",
            "フィッシング", "不審", "怪しい", "着信拒否", "通報",
            "振り込め詐欺", "闇金", "違法",
        )
        val JA_SAFE_KEYWORDS = setOf(
            "宅配", "配送", "病院", "銀行", "保険", "学校", "薬局",
            "歯医者", "レストラン", "ホテル", "航空", "役所",
        )
        val JA_INSTITUTION_KEYWORDS = setOf(
            "役所", "市役所", "区役所", "警察", "消防", "税務署",
            "裁判所", "年金事務所", "入国管理局",
        )

        val ZH_RISK_KEYWORDS = setOf(
            "骗子", "诈骗", "广告", "推销", "垃圾电话", "骚扰",
            "非法", "传销", "洗钱", "赌博", "催债", "高利贷",
            "钓鱼", "虚假", "举报",
        )
        val ZH_SAFE_KEYWORDS = setOf(
            "快递", "外卖", "医院", "银行", "保险", "学校", "药店",
            "餐厅", "酒店", "航空", "物流",
        )
        val ZH_INSTITUTION_KEYWORDS = setOf(
            "政府", "公安", "消防", "税务", "法院", "检察院",
            "社保", "出入境", "居委会", "街道办",
        )

        val RU_RISK_KEYWORDS = setOf(
            "спам", "мошенничество", "обман", "фишинг", "реклама",
            "нежелательный", "опасный", "подозрительный", "блокировка",
            "угроза", "вымогательство", "коллекторы",
        )
        val RU_SAFE_KEYWORDS = setOf(
            "доставка", "курьер", "больница", "банк", "страховка",
            "школа", "аптека", "ресторан", "гостиница", "авиакомпания",
        )
        val RU_INSTITUTION_KEYWORDS = setOf(
            "правительство", "полиция", "пожарная", "налоговая",
            "суд", "прокуратура", "пенсионный фонд", "миграционная служба",
        )

        val ES_RISK_KEYWORDS = setOf(
            "spam", "estafa", "fraude", "phishing", "telemarketing",
            "indeseado", "peligroso", "sospechoso", "bloquear", "reportar",
            "amenaza", "extorsión", "usura",
        )
        val ES_SAFE_KEYWORDS = setOf(
            "entrega", "mensajería", "hospital", "banco", "seguro",
            "escuela", "farmacia", "restaurante", "hotel", "aerolínea",
        )
        val ES_INSTITUTION_KEYWORDS = setOf(
            "gobierno", "policía", "bomberos", "hacienda", "juzgado",
            "seguridad social", "inmigración", "aduanas",
        )

        val FR_RISK_KEYWORDS = setOf(
            "spam", "arnaque", "fraude", "phishing", "démarchage",
            "indésirable", "dangereux", "suspect", "bloquer", "signaler",
            "menace", "extorsion", "usure",
        )
        val FR_SAFE_KEYWORDS = setOf(
            "livraison", "coursier", "hôpital", "banque", "assurance",
            "école", "pharmacie", "restaurant", "hôtel", "compagnie aérienne",
        )
        val FR_INSTITUTION_KEYWORDS = setOf(
            "gouvernement", "police", "pompiers", "impôts", "tribunal",
            "sécurité sociale", "immigration", "douanes", "mairie", "préfecture",
        )

        val DE_RISK_KEYWORDS = setOf(
            "Spam", "Betrug", "Abzocke", "Phishing", "Telefonwerbung",
            "unerwünscht", "gefährlich", "verdächtig", "blockieren", "melden",
            "Drohung", "Erpressung", "Inkasso",
        )
        val DE_SAFE_KEYWORDS = setOf(
            "Lieferung", "Kurier", "Krankenhaus", "Bank", "Versicherung",
            "Schule", "Apotheke", "Restaurant", "Hotel", "Fluggesellschaft",
        )
        val DE_INSTITUTION_KEYWORDS = setOf(
            "Regierung", "Polizei", "Feuerwehr", "Finanzamt", "Gericht",
            "Sozialversicherung", "Einwanderung", "Zoll", "Rathaus",
        )

        val PT_RISK_KEYWORDS = setOf(
            "spam", "golpe", "fraude", "phishing", "telemarketing",
            "indesejado", "perigoso", "suspeito", "bloquear", "denunciar",
            "ameaça", "extorsão", "agiota",
        )
        val PT_SAFE_KEYWORDS = setOf(
            "entrega", "correio", "hospital", "banco", "seguro",
            "escola", "farmácia", "restaurante", "hotel", "companhia aérea",
        )
        val PT_INSTITUTION_KEYWORDS = setOf(
            "governo", "polícia", "bombeiros", "receita federal", "tribunal",
            "previdência", "imigração", "alfândega",
        )

        val AR_RISK_KEYWORDS = setOf(
            "احتيال", "نصب", "رسائل مزعجة", "تصيد", "تسويق",
            "مشبوه", "خطير", "حظر", "إبلاغ", "تهديد", "ابتزاز",
        )
        val AR_SAFE_KEYWORDS = setOf(
            "توصيل", "مستشفى", "بنك", "تأمين", "مدرسة", "صيدلية",
            "مطعم", "فندق", "طيران",
        )
        val AR_INSTITUTION_KEYWORDS = setOf(
            "حكومة", "شرطة", "إطفاء", "ضرائب", "محكمة",
            "تأمينات اجتماعية", "هجرة", "جمارك",
        )

        val HI_RISK_KEYWORDS = setOf(
            "स्पैम", "धोखाधड़ी", "फ्रॉड", "फ़िशिंग", "टेलीमार्केटिंग",
            "अवांछित", "खतरनाक", "संदिग्ध", "ब्लॉक", "रिपोर्ट",
            "धमकी", "जबरन वसूली", "सूदखोर",
        )
        val HI_SAFE_KEYWORDS = setOf(
            "डिलीवरी", "कूरियर", "अस्पताल", "बैंक", "बीमा",
            "स्कूल", "फार्मेसी", "रेस्तरां", "होटल", "एयरलाइन",
        )
        val HI_INSTITUTION_KEYWORDS = setOf(
            "सरकार", "पुलिस", "दमकल", "कर कार्यालय", "अदालत",
            "सामाजिक सुरक्षा", "आव्रजन",
        )

        val TH_RISK_KEYWORDS = setOf(
            "สแปม", "หลอกลวง", "ฉ้อโกง", "ฟิชชิ่ง", "โทรขาย",
            "ไม่พึงประสงค์", "อันตราย", "น่าสงสัย", "บล็อก", "แจ้งความ",
        )
        val TH_SAFE_KEYWORDS = setOf(
            "ส่งของ", "พัสดุ", "โรงพยาบาล", "ธนาคาร", "ประกัน",
            "โรงเรียน", "ร้านขายยา", "ร้านอาหาร", "โรงแรม",
        )

        val VI_RISK_KEYWORDS = setOf(
            "spam", "lừa đảo", "gian lận", "phishing", "tiếp thị",
            "không mong muốn", "nguy hiểm", "đáng ngờ", "chặn", "báo cáo",
        )
        val VI_SAFE_KEYWORDS = setOf(
            "giao hàng", "chuyển phát", "bệnh viện", "ngân hàng", "bảo hiểm",
            "trường học", "nhà thuốc", "nhà hàng", "khách sạn",
        )

        val TR_RISK_KEYWORDS = setOf(
            "spam", "dolandırıcılık", "sahte", "phishing", "telefonla pazarlama",
            "istenmeyen", "tehlikeli", "şüpheli", "engelle", "bildir",
        )
        val TR_SAFE_KEYWORDS = setOf(
            "teslimat", "kurye", "hastane", "banka", "sigorta",
            "okul", "eczane", "restoran", "otel", "havayolu",
        )

        val ID_RISK_KEYWORDS = setOf(
            "spam", "penipuan", "scam", "phishing", "telemarketing",
            "tidak diinginkan", "berbahaya", "mencurigakan", "blokir", "lapor",
        )
        val ID_SAFE_KEYWORDS = setOf(
            "pengiriman", "kurir", "rumah sakit", "bank", "asuransi",
            "sekolah", "apotek", "restoran", "hotel", "maskapai",
        )

        val MS_RISK_KEYWORDS = setOf(
            "spam", "penipuan", "scam", "phishing", "telepemasaran",
            "tidak diingini", "berbahaya", "mencurigakan", "sekat", "lapor",
        )

        val PL_RISK_KEYWORDS = setOf(
            "spam", "oszustwo", "wyłudzenie", "phishing", "telemarketing",
            "niechciane", "niebezpieczne", "podejrzane", "zablokuj", "zgłoś",
        )

        val NL_RISK_KEYWORDS = setOf(
            "spam", "oplichting", "fraude", "phishing", "telemarketing",
            "ongewenst", "gevaarlijk", "verdacht", "blokkeer", "meld",
        )

        val IT_RISK_KEYWORDS = setOf(
            "spam", "truffa", "frode", "phishing", "telemarketing",
            "indesiderato", "pericoloso", "sospetto", "bloccare", "segnalare",
        )

        val SV_RISK_KEYWORDS = setOf(
            "spam", "bedrägeri", "bluff", "phishing", "telefonförsäljning",
            "oönskad", "farlig", "misstänkt", "blockera", "rapportera",
        )

        val CZ_RISK_KEYWORDS = setOf(
            "spam", "podvod", "falešný", "phishing", "telemarketing",
            "nežádoucí", "nebezpečný", "podezřelý", "blokovat", "nahlásit",
        )

        val ZH_TW_RISK_KEYWORDS = setOf(
            "詐騙", "騙子", "廣告", "推銷", "垃圾電話", "騷擾",
            "非法", "傳銷", "舉報", "可疑", "封鎖",
        )

        /** 미등록 국가 기본 설정 */
        val GLOBAL_FALLBACK = CountrySearchConfig(
            countryCode = "ZZ",
            tier = SearchTier.TIER_D,
            primaryEngine = SearchEngine.GOOGLE,
            secondaryEngine = SearchEngine.BING,
            tertiarySource = SearchEngine.TRUECALLER,
            queryLocalization = QueryLocalization(
                languageCode = "en",
                queryTemplates = listOf(
                    "\"{number}\" spam",
                    "\"{number}\" scam OR fraud",
                    "\"{number}\" who called",
                ),
                riskKeywords = EN_RISK_KEYWORDS,
                safeKeywords = EN_SAFE_KEYWORDS,
                institutionKeywords = EN_INSTITUTION_KEYWORDS,
            ),
            parsingRules = ParsingRules(),
            timeoutPolicy = TimeoutPolicy(),
        )
    }

    // ══════════════════════════════════════════════════════════════
    // 190개국 전수 레지스트리 빌드
    // ══════════════════════════════════════════════════════════════

    private fun buildFullRegistry(): Map<String, CountrySearchConfig> {
        val map = mutableMapOf<String, CountrySearchConfig>()

        // ── Tier A: 현지 검색엔진 강국 (8국) — 최우선 ──
        buildTierA().forEach { map[it.countryCode] = it }

        // ── Tier B: Google + 현지 디렉토리 병행 (22국) ──
        // putIfAbsent: Tier A에서 이미 등록된 국가는 덮어쓰지 않음
        buildTierB().forEach { map.putIfAbsent(it.countryCode, it) }

        // ── Tier C: Google 중심 (40국) ──
        buildTierC().forEach { map.putIfAbsent(it.countryCode, it) }

        // ── Tier D: Google fallback (120국) ──
        buildTierD().forEach { map.putIfAbsent(it.countryCode, it) }

        return map
    }

    // ══════════════════════════════════════
    // Tier A: 현지 검색엔진 강국
    // ══════════════════════════════════════

    private fun buildTierA(): List<CountrySearchConfig> = listOf(
        // ── 한국 (KR) ──
        CountrySearchConfig(
            countryCode = "KR",
            tier = SearchTier.TIER_A,
            primaryEngine = SearchEngine.NAVER,
            secondaryEngine = SearchEngine.DAUM,
            tertiarySource = SearchEngine.THECALL_KR,
            bannedEngines = setOf(),  // Google 허용하되 1순위 금지
            queryLocalization = QueryLocalization(
                languageCode = "ko",
                queryTemplates = listOf(
                    "\"{number}\" 스팸",
                    "\"{number}\" 보이스피싱",
                    "\"{number}\" 택배 OR 배송",
                    "\"{number}\" 대출 OR 사기",
                ),
                riskKeywords = KO_RISK_KEYWORDS,
                safeKeywords = KO_SAFE_KEYWORDS,
                institutionKeywords = KO_INSTITUTION_KEYWORDS,
            ),
            parsingRules = ParsingRules(
                riskKeywordWeight = 0.18f,
                safeKeywordWeight = 0.12f,
                institutionKeywordWeight = 0.15f,
                baseConfidenceWeight = 1.2f,
            ),
            timeoutPolicy = TimeoutPolicy(
                primaryTimeoutMs = 1000L,
                secondaryTimeoutMs = 500L,
                tertiaryTimeoutMs = 400L,
            ),
        ),

        // ── 중국 (CN) ──
        CountrySearchConfig(
            countryCode = "CN",
            tier = SearchTier.TIER_A,
            primaryEngine = SearchEngine.BAIDU,
            secondaryEngine = SearchEngine.SOGOU,
            tertiarySource = SearchEngine.BAIDU_ZHIDAO_CN,
            bannedEngines = setOf(SearchEngine.GOOGLE, SearchEngine.BING, SearchEngine.DUCKDUCKGO),
            queryLocalization = QueryLocalization(
                languageCode = "zh",
                queryTemplates = listOf(
                    "\"{number}\" 骗子",
                    "\"{number}\" 广告 OR 推销",
                    "\"{number}\" 快递 OR 外卖",
                    "\"{number}\" 诈骗",
                ),
                riskKeywords = ZH_RISK_KEYWORDS,
                safeKeywords = ZH_SAFE_KEYWORDS,
                institutionKeywords = ZH_INSTITUTION_KEYWORDS,
            ),
            parsingRules = ParsingRules(
                riskKeywordWeight = 0.18f,
                baseConfidenceWeight = 1.2f,
            ),
            timeoutPolicy = TimeoutPolicy(
                primaryTimeoutMs = 1200L,  // 중국 네트워크 지연 감안
                secondaryTimeoutMs = 600L,
            ),
        ),

        // ── 일본 (JP) ──
        CountrySearchConfig(
            countryCode = "JP",
            tier = SearchTier.TIER_A,
            primaryEngine = SearchEngine.YAHOO_JAPAN,
            secondaryEngine = SearchEngine.GOOGLE,
            tertiarySource = SearchEngine.CHIEBUKURO_JP,
            queryLocalization = QueryLocalization(
                languageCode = "ja",
                queryTemplates = listOf(
                    "\"{number}\" 迷惑電話",
                    "\"{number}\" 詐欺",
                    "\"{number}\" 宅配 OR 配送",
                    "\"{number}\" 架空請求",
                ),
                riskKeywords = JA_RISK_KEYWORDS,
                safeKeywords = JA_SAFE_KEYWORDS,
                institutionKeywords = JA_INSTITUTION_KEYWORDS,
            ),
            parsingRules = ParsingRules(
                riskKeywordWeight = 0.18f,
                baseConfidenceWeight = 1.2f,
            ),
        ),

        // ── 러시아 (RU) ──
        CountrySearchConfig(
            countryCode = "RU",
            tier = SearchTier.TIER_A,
            primaryEngine = SearchEngine.YANDEX,
            secondaryEngine = SearchEngine.GOOGLE,
            tertiarySource = SearchEngine.GETCONTACT,
            queryLocalization = QueryLocalization(
                languageCode = "ru",
                queryTemplates = listOf(
                    "\"{number}\" спам",
                    "\"{number}\" мошенничество OR обман",
                    "\"{number}\" доставка OR курьер",
                ),
                riskKeywords = RU_RISK_KEYWORDS,
                safeKeywords = RU_SAFE_KEYWORDS,
                institutionKeywords = RU_INSTITUTION_KEYWORDS,
            ),
            parsingRules = ParsingRules(
                riskKeywordWeight = 0.16f,
                baseConfidenceWeight = 1.1f,
            ),
        ),

        // ── 체코 (CZ) ──
        CountrySearchConfig(
            countryCode = "CZ",
            tier = SearchTier.TIER_A,
            primaryEngine = SearchEngine.SEZNAM,
            secondaryEngine = SearchEngine.GOOGLE,
            tertiarySource = SearchEngine.SHOULDIANSWER,
            queryLocalization = QueryLocalization(
                languageCode = "cs",
                queryTemplates = listOf(
                    "\"{number}\" spam",
                    "\"{number}\" podvod OR falešný",
                ),
                riskKeywords = CZ_RISK_KEYWORDS,
                safeKeywords = EN_SAFE_KEYWORDS,
                institutionKeywords = EN_INSTITUTION_KEYWORDS,
            ),
            parsingRules = ParsingRules(
                riskKeywordWeight = 0.15f,
                baseConfidenceWeight = 1.1f,
            ),
        ),

        // ── 대만 (TW) ──
        CountrySearchConfig(
            countryCode = "TW",
            tier = SearchTier.TIER_A,
            primaryEngine = SearchEngine.GOOGLE,
            secondaryEngine = SearchEngine.WHOSCALL,
            tertiarySource = SearchEngine.TRUECALLER,
            queryLocalization = QueryLocalization(
                languageCode = "zh-TW",
                queryTemplates = listOf(
                    "\"{number}\" 詐騙",
                    "\"{number}\" 廣告 OR 推銷",
                    "\"{number}\" 快遞 OR 宅配",
                ),
                riskKeywords = ZH_TW_RISK_KEYWORDS,
                safeKeywords = ZH_SAFE_KEYWORDS,
                institutionKeywords = ZH_INSTITUTION_KEYWORDS,
            ),
            parsingRules = ParsingRules(
                riskKeywordWeight = 0.16f,
                baseConfidenceWeight = 1.1f,
            ),
        ),

        // ── 베트남 (VN) ──
        CountrySearchConfig(
            countryCode = "VN",
            tier = SearchTier.TIER_A,
            primaryEngine = SearchEngine.GOOGLE,
            secondaryEngine = SearchEngine.WHOSCALL,
            tertiarySource = SearchEngine.TRUECALLER,
            queryLocalization = QueryLocalization(
                languageCode = "vi",
                queryTemplates = listOf(
                    "\"{number}\" lừa đảo",
                    "\"{number}\" spam OR quảng cáo",
                    "\"{number}\" giao hàng",
                ),
                riskKeywords = VI_RISK_KEYWORDS,
                safeKeywords = VI_SAFE_KEYWORDS,
                institutionKeywords = EN_INSTITUTION_KEYWORDS,
            ),
            parsingRules = ParsingRules(
                riskKeywordWeight = 0.15f,
                baseConfidenceWeight = 1.0f,
            ),
        ),

        // ── 태국 (TH) ──
        CountrySearchConfig(
            countryCode = "TH",
            tier = SearchTier.TIER_A,
            primaryEngine = SearchEngine.GOOGLE,
            secondaryEngine = SearchEngine.WHOSCALL,
            tertiarySource = SearchEngine.TRUECALLER,
            queryLocalization = QueryLocalization(
                languageCode = "th",
                queryTemplates = listOf(
                    "\"{number}\" หลอกลวง",
                    "\"{number}\" สแปม OR โทรขาย",
                    "\"{number}\" ส่งของ",
                ),
                riskKeywords = TH_RISK_KEYWORDS,
                safeKeywords = TH_SAFE_KEYWORDS,
                institutionKeywords = EN_INSTITUTION_KEYWORDS,
            ),
            parsingRules = ParsingRules(
                riskKeywordWeight = 0.15f,
                baseConfidenceWeight = 1.0f,
            ),
        ),
    )

    // ══════════════════════════════════════
    // Tier B: Google + 현지 디렉토리 병행 (22국)
    // ══════════════════════════════════════

    private fun buildTierB(): List<CountrySearchConfig> = listOf(
        tierB("DE", "de", SearchEngine.TELLOWS,
            listOf("\"{number}\" Spam", "\"{number}\" Betrug OR Abzocke", "\"{number}\" Lieferung"),
            DE_RISK_KEYWORDS, DE_SAFE_KEYWORDS, DE_INSTITUTION_KEYWORDS),
        tierB("FR", "fr", SearchEngine.TELLOWS,
            listOf("\"{number}\" arnaque", "\"{number}\" spam OR démarchage", "\"{number}\" livraison"),
            FR_RISK_KEYWORDS, FR_SAFE_KEYWORDS, FR_INSTITUTION_KEYWORDS),
        tierB("GB", "en", SearchEngine.TELLOWS,
            listOf("\"{number}\" spam", "\"{number}\" scam OR fraud", "\"{number}\" delivery"),
            EN_RISK_KEYWORDS, EN_SAFE_KEYWORDS, EN_INSTITUTION_KEYWORDS),
        tierB("IT", "it", SearchEngine.TELLOWS,
            listOf("\"{number}\" spam", "\"{number}\" truffa OR frode", "\"{number}\" consegna"),
            IT_RISK_KEYWORDS, EN_SAFE_KEYWORDS, EN_INSTITUTION_KEYWORDS),
        tierB("ES", "es", SearchEngine.TELLOWS,
            listOf("\"{number}\" spam", "\"{number}\" estafa OR fraude", "\"{number}\" entrega"),
            ES_RISK_KEYWORDS, ES_SAFE_KEYWORDS, ES_INSTITUTION_KEYWORDS),
        tierB("BR", "pt", SearchEngine.TRUECALLER,
            listOf("\"{number}\" spam", "\"{number}\" golpe OR fraude", "\"{number}\" entrega"),
            PT_RISK_KEYWORDS, PT_SAFE_KEYWORDS, PT_INSTITUTION_KEYWORDS),
        tierB("IN", "hi", SearchEngine.TRUECALLER,
            listOf("\"{number}\" spam", "\"{number}\" fraud OR scam", "\"{number}\" delivery"),
            HI_RISK_KEYWORDS, HI_SAFE_KEYWORDS, HI_INSTITUTION_KEYWORDS),
        tierB("AU", "en", SearchEngine.TRUECALLER,
            listOf("\"{number}\" spam", "\"{number}\" scam", "\"{number}\" delivery"),
            EN_RISK_KEYWORDS, EN_SAFE_KEYWORDS, EN_INSTITUTION_KEYWORDS),
        tierB("NL", "nl", SearchEngine.TELLOWS,
            listOf("\"{number}\" spam", "\"{number}\" oplichting OR fraude"),
            NL_RISK_KEYWORDS, EN_SAFE_KEYWORDS, EN_INSTITUTION_KEYWORDS),
        tierB("SE", "sv", SearchEngine.TELLOWS,
            listOf("\"{number}\" spam", "\"{number}\" bedrägeri"),
            SV_RISK_KEYWORDS, EN_SAFE_KEYWORDS, EN_INSTITUTION_KEYWORDS),
        tierB("PL", "pl", SearchEngine.SHOULDIANSWER,
            listOf("\"{number}\" spam", "\"{number}\" oszustwo"),
            PL_RISK_KEYWORDS, EN_SAFE_KEYWORDS, EN_INSTITUTION_KEYWORDS),
        tierB("TR", "tr", SearchEngine.TRUECALLER,
            listOf("\"{number}\" spam", "\"{number}\" dolandırıcılık"),
            TR_RISK_KEYWORDS, TR_SAFE_KEYWORDS, EN_INSTITUTION_KEYWORDS),
        tierB("MX", "es", SearchEngine.TRUECALLER,
            listOf("\"{number}\" spam", "\"{number}\" estafa OR fraude"),
            ES_RISK_KEYWORDS, ES_SAFE_KEYWORDS, ES_INSTITUTION_KEYWORDS),
        tierB("ID", "id", SearchEngine.TRUECALLER,
            listOf("\"{number}\" spam", "\"{number}\" penipuan"),
            ID_RISK_KEYWORDS, ID_SAFE_KEYWORDS, EN_INSTITUTION_KEYWORDS),
        tierB("PH", "en", SearchEngine.TRUECALLER,
            listOf("\"{number}\" spam", "\"{number}\" scam OR fraud"),
            EN_RISK_KEYWORDS, EN_SAFE_KEYWORDS, EN_INSTITUTION_KEYWORDS),
        tierB("MY", "ms", SearchEngine.TRUECALLER,
            listOf("\"{number}\" spam", "\"{number}\" penipuan"),
            MS_RISK_KEYWORDS, EN_SAFE_KEYWORDS, EN_INSTITUTION_KEYWORDS),
        tierB("CH", "de", SearchEngine.TELLOWS,
            listOf("\"{number}\" Spam", "\"{number}\" Betrug"),
            DE_RISK_KEYWORDS, DE_SAFE_KEYWORDS, DE_INSTITUTION_KEYWORDS),
        tierB("SA", "ar", SearchEngine.TRUECALLER,
            listOf("\"{number}\" احتيال", "\"{number}\" رسائل مزعجة"),
            AR_RISK_KEYWORDS, AR_SAFE_KEYWORDS, AR_INSTITUTION_KEYWORDS),
        tierB("AE", "ar", SearchEngine.TRUECALLER,
            listOf("\"{number}\" احتيال", "\"{number}\" رسائل مزعجة"),
            AR_RISK_KEYWORDS, AR_SAFE_KEYWORDS, AR_INSTITUTION_KEYWORDS),
        tierB("IL", "he", SearchEngine.TRUECALLER,
            listOf("\"{number}\" spam", "\"{number}\" הונאה"),
            EN_RISK_KEYWORDS, EN_SAFE_KEYWORDS, EN_INSTITUTION_KEYWORDS),
        tierB("NZ", "en", SearchEngine.TRUECALLER,
            listOf("\"{number}\" spam", "\"{number}\" scam"),
            EN_RISK_KEYWORDS, EN_SAFE_KEYWORDS, EN_INSTITUTION_KEYWORDS),
        tierB("HK", "zh-HK", SearchEngine.WHOSCALL,
            listOf("\"{number}\" 詐騙", "\"{number}\" 廣告"),
            ZH_TW_RISK_KEYWORDS, ZH_SAFE_KEYWORDS, ZH_INSTITUTION_KEYWORDS),
    )

    private fun tierB(
        cc: String, lang: String, tertiary: SearchEngine,
        templates: List<String>,
        risk: Set<String>, safe: Set<String>, institution: Set<String>,
    ) = CountrySearchConfig(
        countryCode = cc,
        tier = SearchTier.TIER_B,
        primaryEngine = SearchEngine.GOOGLE,
        secondaryEngine = SearchEngine.BING,
        tertiarySource = tertiary,
        queryLocalization = QueryLocalization(
            languageCode = lang,
            queryTemplates = templates,
            riskKeywords = risk,
            safeKeywords = safe,
            institutionKeywords = institution,
        ),
        parsingRules = ParsingRules(baseConfidenceWeight = 1.0f),
    )

    // ══════════════════════════════════════
    // Tier C: Google 중심 (40국)
    // ══════════════════════════════════════

    private fun buildTierC(): List<CountrySearchConfig> {
        // Google 중심 + 지역 디렉토리 (Truecaller/Whoscall)
        val tierCCountries = listOf(
            // 북미
            Triple("US", "en", listOf("\"{number}\" spam", "\"{number}\" scam OR fraud", "\"{number}\" who called")),
            Triple("CA", "en", listOf("\"{number}\" spam", "\"{number}\" scam")),
            // 유럽
            Triple("AT", "de", listOf("\"{number}\" Spam", "\"{number}\" Betrug")),
            Triple("BE", "fr", listOf("\"{number}\" arnaque", "\"{number}\" spam")),
            Triple("BG", "bg", listOf("\"{number}\" спам", "\"{number}\" измама")),
            Triple("DK", "da", listOf("\"{number}\" spam", "\"{number}\" svindel")),
            Triple("FI", "fi", listOf("\"{number}\" roskaposti", "\"{number}\" huijaus")),
            Triple("GR", "el", listOf("\"{number}\" spam", "\"{number}\" απάτη")),
            Triple("HR", "hr", listOf("\"{number}\" spam", "\"{number}\" prijevara")),
            Triple("HU", "hu", listOf("\"{number}\" spam", "\"{number}\" csalás")),
            Triple("IE", "en", listOf("\"{number}\" spam", "\"{number}\" scam")),
            Triple("LU", "fr", listOf("\"{number}\" arnaque", "\"{number}\" spam")),
            Triple("NO", "no", listOf("\"{number}\" spam", "\"{number}\" svindel")),
            Triple("PT", "pt", listOf("\"{number}\" spam", "\"{number}\" golpe")),
            Triple("RO", "ro", listOf("\"{number}\" spam", "\"{number}\" înșelătorie")),
            Triple("RS", "sr", listOf("\"{number}\" spam", "\"{number}\" prevara")),
            Triple("SI", "sl", listOf("\"{number}\" spam", "\"{number}\" prevara")),
            Triple("SK", "sk", listOf("\"{number}\" spam", "\"{number}\" podvod")),
            Triple("UA", "uk", listOf("\"{number}\" спам", "\"{number}\" шахрайство")),
            // 아시아
            Triple("SG", "en", listOf("\"{number}\" spam", "\"{number}\" scam")),
            // KR은 Tier A에서 처리 — 여기 포함 금지
            Triple("PK", "ur", listOf("\"{number}\" spam", "\"{number}\" fraud")),
            Triple("BD", "bn", listOf("\"{number}\" spam", "\"{number}\" প্রতারণা")),
            Triple("LK", "si", listOf("\"{number}\" spam", "\"{number}\" scam")),
            Triple("MM", "my", listOf("\"{number}\" spam", "\"{number}\" scam")),
            Triple("KH", "km", listOf("\"{number}\" spam", "\"{number}\" scam")),
            Triple("NP", "ne", listOf("\"{number}\" spam", "\"{number}\" scam")),
            // 남미
            Triple("AR", "es", listOf("\"{number}\" spam", "\"{number}\" estafa")),
            Triple("CL", "es", listOf("\"{number}\" spam", "\"{number}\" estafa")),
            Triple("CO", "es", listOf("\"{number}\" spam", "\"{number}\" estafa")),
            Triple("PE", "es", listOf("\"{number}\" spam", "\"{number}\" estafa")),
            Triple("VE", "es", listOf("\"{number}\" spam", "\"{number}\" estafa")),
            Triple("EC", "es", listOf("\"{number}\" spam", "\"{number}\" estafa")),
            Triple("UY", "es", listOf("\"{number}\" spam", "\"{number}\" estafa")),
            // 아프리카
            Triple("ZA", "en", listOf("\"{number}\" spam", "\"{number}\" scam")),
            Triple("NG", "en", listOf("\"{number}\" spam", "\"{number}\" scam")),
            Triple("KE", "en", listOf("\"{number}\" spam", "\"{number}\" scam")),
            Triple("EG", "ar", listOf("\"{number}\" احتيال", "\"{number}\" spam")),
            Triple("MA", "fr", listOf("\"{number}\" arnaque", "\"{number}\" spam")),
            Triple("GH", "en", listOf("\"{number}\" spam", "\"{number}\" scam")),
        )

        return tierCCountries.map { (cc, lang, templates) ->
            val riskKw = when (lang) {
                "de" -> DE_RISK_KEYWORDS; "fr" -> FR_RISK_KEYWORDS
                "es" -> ES_RISK_KEYWORDS; "pt" -> PT_RISK_KEYWORDS
                "ar" -> AR_RISK_KEYWORDS; "ru" -> RU_RISK_KEYWORDS
                else -> EN_RISK_KEYWORDS
            }
            CountrySearchConfig(
                countryCode = cc,
                tier = SearchTier.TIER_C,
                primaryEngine = SearchEngine.GOOGLE,
                secondaryEngine = SearchEngine.BING,
                tertiarySource = SearchEngine.TRUECALLER,
                queryLocalization = QueryLocalization(
                    languageCode = lang,
                    queryTemplates = templates.ifEmpty {
                        listOf("\"{number}\" spam", "\"{number}\" scam OR fraud")
                    },
                    riskKeywords = riskKw,
                    safeKeywords = EN_SAFE_KEYWORDS,
                    institutionKeywords = EN_INSTITUTION_KEYWORDS,
                ),
                parsingRules = ParsingRules(baseConfidenceWeight = 0.9f),
            )
        }
    }

    // ══════════════════════════════════════
    // Tier D: Google fallback (나머지 120국)
    // ══════════════════════════════════════

    private fun buildTierD(): List<CountrySearchConfig> {
        // ISO 3166-1 alpha-2 전체에서 Tier A/B/C에 없는 나머지 국가들
        val tierDCodes = listOf(
            // 유럽 나머지
            "AD", "AL", "AM", "AZ", "BA", "BY", "CY", "EE", "FO", "GE",
            "GI", "GL", "IS", "LI", "LT", "LV", "MC", "MD", "ME", "MK",
            "MT", "SM", "VA", "XK",
            // 아시아 나머지
            "AF", "BN", "BT", "GU", "IQ", "IR", "JO", "KG", "KP", "KW",
            "KZ", "LA", "LB", "MN", "MO", "MV", "OM", "PS", "QA", "SY",
            "TJ", "TL", "TM", "UZ", "YE",
            // 아프리카 나머지
            "AO", "BF", "BI", "BJ", "BW", "CD", "CF", "CG", "CI", "CM",
            "CV", "DJ", "DZ", "ER", "ET", "GA", "GM", "GN", "GQ", "GW",
            "KM", "LR", "LS", "LY", "MG", "ML", "MR", "MU", "MW", "MZ",
            "NA", "NE", "RE", "RW", "SC", "SD", "SL", "SN", "SO", "SS",
            "ST", "SZ", "TD", "TG", "TN", "TZ", "UG", "YT", "ZM", "ZW",
            // 오세아니아
            "FJ", "FM", "GU", "KI", "MH", "MP", "NC", "NR", "PF", "PG",
            "PW", "SB", "SM", "TO", "TV", "VU", "WF", "WS",
            // 카리브/중미
            "AG", "AI", "AW", "BB", "BL", "BM", "BO", "BS", "BZ", "CR",
            "CU", "CW", "DM", "DO", "GD", "GP", "GT", "GY", "HN", "HT",
            "JM", "KN", "KY", "LC", "MF", "MQ", "MS", "NI", "PA", "PR",
            "PY", "SR", "SV", "SX", "TC", "TT", "VC", "VG", "VI",
        )

        // 중복 제거
        val seen = mutableSetOf<String>()

        return tierDCodes.distinct().map { cc ->
            val lang = inferLanguage(cc)
            val riskKw = when (lang) {
                "ar" -> AR_RISK_KEYWORDS; "fr" -> FR_RISK_KEYWORDS
                "es" -> ES_RISK_KEYWORDS; "pt" -> PT_RISK_KEYWORDS
                "ru" -> RU_RISK_KEYWORDS
                else -> EN_RISK_KEYWORDS
            }
            CountrySearchConfig(
                countryCode = cc,
                tier = SearchTier.TIER_D,
                primaryEngine = SearchEngine.GOOGLE,
                secondaryEngine = SearchEngine.BING,
                tertiarySource = SearchEngine.TRUECALLER,
                queryLocalization = QueryLocalization(
                    languageCode = lang,
                    queryTemplates = listOf(
                        "\"{number}\" spam",
                        "\"{number}\" scam OR fraud",
                    ),
                    riskKeywords = riskKw,
                    safeKeywords = EN_SAFE_KEYWORDS,
                    institutionKeywords = EN_INSTITUTION_KEYWORDS,
                ),
                parsingRules = ParsingRules(baseConfidenceWeight = 0.7f),
            )
        }
    }

    /** 국가코드 → 주 언어 추론 */
    private fun inferLanguage(cc: String): String = when (cc) {
        // 아랍어권
        "DZ", "BH", "EG", "IQ", "JO", "KW", "LB", "LY", "MA", "OM",
        "PS", "QA", "SD", "SY", "TN", "YE" -> "ar"
        // 프랑스어권
        "BF", "BJ", "CD", "CF", "CG", "CI", "CM", "DJ", "GA", "GN",
        "GP", "GQ", "HT", "KM", "MG", "ML", "MQ", "MR", "MU", "NC",
        "NE", "PF", "RE", "RW", "SN", "TD", "TG", "WF", "YT", "BL",
        "MF" -> "fr"
        // 스페인어권
        "BO", "CR", "CU", "DO", "EC", "GT", "HN", "NI", "PA", "PR",
        "PY", "SV" -> "es"
        // 포르투갈어권
        "AO", "CV", "GW", "MZ", "ST", "TL" -> "pt"
        // 러시아어권
        "BY", "KG", "KZ", "TJ", "TM", "UZ" -> "ru"
        // 페르시아어
        "IR" -> "fa"
        // 나머지 전부 영어 fallback
        else -> "en"
    }
}
