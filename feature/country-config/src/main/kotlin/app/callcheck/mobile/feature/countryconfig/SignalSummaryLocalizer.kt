package app.callcheck.mobile.feature.countryconfig

/**
 * SignalSummary 로컬라이저 — 번역기가 아니라 언어별 템플릿 선택기.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 핵심 원칙                                                     │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 1. SearchResultAnalyzer는 언어 중립을 유지한다               │
 * │    → intensity 상수 + category enum을 반환                    │
 * │ 2. SignalSummaryLocalizer는 그 결과를 받아                    │
 * │    → 현재 언어에 맞는 템플릿을 선택한다                       │
 * │ 3. "번역"이 아니라 "선택" — 각 언어는 자체 표현 체계를 갖는다│
 * │ 4. 새 언어 추가 = 새 템플릿 맵 추가 (기존 코드 수정 없음)   │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 데이터 흐름:
 * ```
 * SearchResultAnalyzer
 *   → intensity: "SAFE" | "REFERENCE" | "CAUTION_LIGHT" | ...
 *   → category: ConclusionCategory enum
 *        │
 *        ▼
 * SignalSummaryLocalizer.localize(intensity, category, language)
 *   → 사용자 대면 로컬라이즈 텍스트
 * ```
 */
class SignalSummaryLocalizer {

    /**
     * 위험도 수준(intensity)을 현재 언어로 로컬라이즈한다.
     *
     * @param intensityKey INTENSITY_* 상수의 키 (예: "SAFE", "DANGER")
     * @param language 대상 언어
     * @return 로컬라이즈된 위험도 텍스트. 매칭 실패 시 EN 폴백.
     */
    fun localizeIntensity(
        intensityKey: String,
        language: SupportedLanguage,
    ): String {
        val templateMap = intensityTemplates[language]
            ?: intensityTemplates[SupportedLanguage.EN]!!
        return templateMap[intensityKey]
            ?: intensityTemplates[SupportedLanguage.EN]!![intensityKey]
            ?: intensityKey
    }

    /**
     * 카테고리별 문장 템플릿을 현재 언어로 로컬라이즈한다.
     *
     * @param categoryKey ConclusionCategory enum의 name (예: "SCAM_RISK_HIGH")
     * @param language 대상 언어
     * @param entityName 엔티티명 (검색에서 발견된 업체/기관명). null이면 일반형 사용.
     * @return 로컬라이즈된 문장. 매칭 실패 시 EN 폴백.
     */
    fun localizeCategory(
        categoryKey: String,
        language: SupportedLanguage,
        entityName: String? = null,
    ): String {
        val templateMap = categoryTemplates[language]
            ?: categoryTemplates[SupportedLanguage.EN]!!
        val template = templateMap[categoryKey]
            ?: categoryTemplates[SupportedLanguage.EN]!![categoryKey]
            ?: return categoryKey

        return if (entityName != null) {
            template.replace("{entity}", entityName)
        } else {
            template.replace("{entity} ", "").replace("{entity}", "")
        }
    }

    /**
     * 전체 SignalSummary 텍스트를 로컬라이즈한다.
     * intensity + category를 조합하여 최종 사용자 대면 텍스트를 생성.
     *
     * @param intensityKey INTENSITY_* 상수 키
     * @param categoryKey ConclusionCategory enum name
     * @param language 대상 언어
     * @param entityName 엔티티명 (선택)
     * @return "위험도 — 카테고리 설명" 형태의 로컬라이즈된 문장
     */
    fun localize(
        intensityKey: String,
        categoryKey: String,
        language: SupportedLanguage,
        entityName: String? = null,
    ): String {
        val intensity = localizeIntensity(intensityKey, language)
        val category = localizeCategory(categoryKey, language, entityName)
        return "$category — $intensity"
    }

    companion object {

        // ═══════════════════════════════════════════════════════
        // Intensity 키 상수 (SearchResultAnalyzer와 동기화)
        // ═══════════════════════════════════════════════════════

        const val KEY_SAFE = "SAFE"
        const val KEY_REFERENCE = "REFERENCE"
        const val KEY_CAUTION_LIGHT = "CAUTION_LIGHT"
        const val KEY_CAUTION = "CAUTION"
        const val KEY_DANGER = "DANGER"
        const val KEY_REJECT = "REJECT"
        const val KEY_VERIFY = "VERIFY"

        // ═══════════════════════════════════════════════════════
        // 언어별 Intensity 템플릿
        // ═══════════════════════════════════════════════════════

        private val intensityTemplates: Map<SupportedLanguage, Map<String, String>> = mapOf(
            SupportedLanguage.KO to mapOf(
                KEY_SAFE to "수신 안전",
                KEY_REFERENCE to "참고 필요",
                KEY_CAUTION_LIGHT to "주의 필요",
                KEY_CAUTION to "수신 주의",
                KEY_DANGER to "수신 위험",
                KEY_REJECT to "거절 권장",
                KEY_VERIFY to "배송 확인 권장",
            ),
            SupportedLanguage.EN to mapOf(
                KEY_SAFE to "Safe to Answer",
                KEY_REFERENCE to "For Reference",
                KEY_CAUTION_LIGHT to "Use Caution",
                KEY_CAUTION to "Be Cautious",
                KEY_DANGER to "High Risk",
                KEY_REJECT to "Reject Recommended",
                KEY_VERIFY to "Verify Delivery",
            ),
            SupportedLanguage.JA to mapOf(
                KEY_SAFE to "安全",
                KEY_REFERENCE to "参考情報",
                KEY_CAUTION_LIGHT to "注意",
                KEY_CAUTION to "要注意",
                KEY_DANGER to "危険",
                KEY_REJECT to "拒否推奨",
                KEY_VERIFY to "配送確認推奨",
            ),
            SupportedLanguage.ZH to mapOf(
                KEY_SAFE to "安全接听",
                KEY_REFERENCE to "仅供参考",
                KEY_CAUTION_LIGHT to "注意",
                KEY_CAUTION to "需注意",
                KEY_DANGER to "高风险",
                KEY_REJECT to "建议拒接",
                KEY_VERIFY to "建议确认快递",
            ),
            SupportedLanguage.RU to mapOf(
                KEY_SAFE to "Безопасно",
                KEY_REFERENCE to "К сведению",
                KEY_CAUTION_LIGHT to "Осторожно",
                KEY_CAUTION to "Будьте внимательны",
                KEY_DANGER to "Высокий риск",
                KEY_REJECT to "Рекомендуется отклонить",
                KEY_VERIFY to "Подтвердите доставку",
            ),
            SupportedLanguage.ES to mapOf(
                KEY_SAFE to "Seguro",
                KEY_REFERENCE to "Para referencia",
                KEY_CAUTION_LIGHT to "Precaución",
                KEY_CAUTION to "Ten cuidado",
                KEY_DANGER to "Alto riesgo",
                KEY_REJECT to "Rechazar recomendado",
                KEY_VERIFY to "Verificar entrega",
            ),
            SupportedLanguage.AR to mapOf(
                KEY_SAFE to "آمن للرد",
                KEY_REFERENCE to "للمرجعية",
                KEY_CAUTION_LIGHT to "احتياط",
                KEY_CAUTION to "كن حذراً",
                KEY_DANGER to "خطر عالي",
                KEY_REJECT to "يُنصح بالرفض",
                KEY_VERIFY to "تحقق من التوصيل",
            ),
        )

        // ═══════════════════════════════════════════════════════
        // 언어별 Category 문장 템플릿
        // {entity}는 엔티티명으로 치환됨
        // ═══════════════════════════════════════════════════════

        private val categoryTemplates: Map<SupportedLanguage, Map<String, String>> = mapOf(
            SupportedLanguage.KO to mapOf(
                "KNOWN_CONTACT" to "저장된 연락처",
                "BUSINESS_LIKELY" to "{entity} 거래처/업무 번호",
                "DELIVERY_LIKELY" to "{entity} 택배/배송 전화",
                "INSTITUTION_LIKELY" to "{entity} 공공기관/병원 전화",
                "SALES_SPAM_SUSPECTED" to "광고/영업 전화 의심",
                "SCAM_RISK_HIGH" to "사기/피싱 위험",
                "INSUFFICIENT_EVIDENCE" to "판단 근거 부족",
            ),
            SupportedLanguage.EN to mapOf(
                "KNOWN_CONTACT" to "Known Contact",
                "BUSINESS_LIKELY" to "{entity} Business Call",
                "DELIVERY_LIKELY" to "{entity} Delivery Call",
                "INSTITUTION_LIKELY" to "{entity} Institution Call",
                "SALES_SPAM_SUSPECTED" to "Suspected Spam/Sales",
                "SCAM_RISK_HIGH" to "Scam/Phishing Risk",
                "INSUFFICIENT_EVIDENCE" to "Insufficient Evidence",
            ),
            SupportedLanguage.JA to mapOf(
                "KNOWN_CONTACT" to "登録済み連絡先",
                "BUSINESS_LIKELY" to "{entity} ビジネス電話",
                "DELIVERY_LIKELY" to "{entity} 配送電話",
                "INSTITUTION_LIKELY" to "{entity} 公共機関電話",
                "SALES_SPAM_SUSPECTED" to "広告/営業電話の疑い",
                "SCAM_RISK_HIGH" to "詐欺/フィッシングリスク",
                "INSUFFICIENT_EVIDENCE" to "判断根拠不足",
            ),
            SupportedLanguage.ZH to mapOf(
                "KNOWN_CONTACT" to "已存联系人",
                "BUSINESS_LIKELY" to "{entity} 商业电话",
                "DELIVERY_LIKELY" to "{entity} 快递电话",
                "INSTITUTION_LIKELY" to "{entity} 公共机构电话",
                "SALES_SPAM_SUSPECTED" to "疑似广告/推销",
                "SCAM_RISK_HIGH" to "诈骗/钓鱼风险",
                "INSUFFICIENT_EVIDENCE" to "判断依据不足",
            ),
            SupportedLanguage.RU to mapOf(
                "KNOWN_CONTACT" to "Известный контакт",
                "BUSINESS_LIKELY" to "{entity} Деловой звонок",
                "DELIVERY_LIKELY" to "{entity} Звонок доставки",
                "INSTITUTION_LIKELY" to "{entity} Звонок учреждения",
                "SALES_SPAM_SUSPECTED" to "Подозрение на спам",
                "SCAM_RISK_HIGH" to "Риск мошенничества",
                "INSUFFICIENT_EVIDENCE" to "Недостаточно данных",
            ),
            SupportedLanguage.ES to mapOf(
                "KNOWN_CONTACT" to "Contacto conocido",
                "BUSINESS_LIKELY" to "{entity} Llamada comercial",
                "DELIVERY_LIKELY" to "{entity} Llamada de entrega",
                "INSTITUTION_LIKELY" to "{entity} Llamada institucional",
                "SALES_SPAM_SUSPECTED" to "Sospecha de spam",
                "SCAM_RISK_HIGH" to "Riesgo de fraude",
                "INSUFFICIENT_EVIDENCE" to "Evidencia insuficiente",
            ),
            SupportedLanguage.AR to mapOf(
                "KNOWN_CONTACT" to "جهة اتصال معروفة",
                "BUSINESS_LIKELY" to "{entity} مكالمة تجارية",
                "DELIVERY_LIKELY" to "{entity} مكالمة توصيل",
                "INSTITUTION_LIKELY" to "{entity} مكالمة مؤسسة",
                "SALES_SPAM_SUSPECTED" to "اشتباه في إعلانات",
                "SCAM_RISK_HIGH" to "خطر احتيال",
                "INSUFFICIENT_EVIDENCE" to "أدلة غير كافية",
            ),
        )
    }
}
