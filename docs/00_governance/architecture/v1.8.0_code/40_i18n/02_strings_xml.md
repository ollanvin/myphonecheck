# 25. strings.xml 명세

**원본 출처**: v1.7.1 §25 (91줄)
**v1.8.0 Layer**: i18n
**의존**: `40_i18n/01_country_separation.md`
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/40_i18n/02_strings_xml.md`

---

# 25. strings.xml 명세

## 25-1. 다국어 자원 원칙

- **하드코딩 금지**: 코드 내 `"안녕하세요"` 등 직접 문자열 삽입 금지 (메모리 철칙)
- **strings.xml만**: `res/values/strings.xml` + `res/values-xx/strings.xml`
- **사용 방식**: `context.getString(R.string.xxx)` 또는 XML `@string/xxx`
- **파라미터**: `<string name="call_risk_medium_title">위험도: %1$s</string>` → `getString(R.string.xxx, "MEDIUM")`
- **플루랄**: `<plurals>` 사용 (국가별 단수·복수 규칙 차이 대응)
- **CDATA 금지**: HTML 삽입 대신 Kotlin에서 `SpannableString` 구성

## 25-2. 자원 카테고리

```
res/values/
├── strings.xml              # 앱 공통 문자열
├── strings_onboarding.xml   # 온보딩 슬라이드 4개
├── strings_call.xml         # CallCheck UI
├── strings_message.xml      # MessageCheck UI
├── strings_mic.xml          # MicCheck UI
├── strings_camera.xml       # CameraCheck UI
├── strings_subscription.xml # Billing UX
├── strings_settings.xml
├── strings_reasons.xml      # 이유 설명 템플릿 (한 줄 요약)
├── strings_damage.xml       # 예상 손해 템플릿
├── keywords.xml             # SearchResultAnalyzer 키워드 사전
└── categories.xml           # ConclusionCategory·DamageType 이름
```

## 25-3. locale 목록 (초기)

- `values/` (기본 = 영어)
- `values-ko/` (한국어)
- `values-ja/` (일본어)
- `values-zh/` (중국어 간체)
- `values-zh-rTW/` (중국어 번체)
- `values-es/` (스페인어)
- `values-pt/` (포르투갈어)
- `values-id/` (인도네시아어)
- `values-vi/` (베트남어)
- `values-th/` (태국어)
- `values-hi/` (힌디어)
- `values-ar/` (아랍어, RTL)
- ... (190개국까지 확장)

**Phase 5 착수 시** ko/en/ja 최소 3개 locale 완전 번역 + 나머지는 en fallback.

## 25-4. 이유 설명 템플릿 (strings_reasons.xml)

```xml
<resources>
    <string name="reason_official_reported">정부 신고 %1$d건, 사용자 신고 %2$d건</string>
    <string name="reason_user_reported">사용자 신고 %1$d건</string>
    <string name="reason_scam_keyword_hit">사기 관련 키워드 %1$d회 발견</string>
    <string name="reason_impersonation">%1$s 사칭 의심 — 공식 도메인 아님</string>
    <string name="reason_suspicious_url">의심 URL: %1$s</string>
    <string name="reason_new_number">신규 번호 — 정보 부족</string>
    <string name="reason_contact_registered">연락처에 저장된 번호</string>
    <string name="reason_high_confidence">높은 신뢰도 (%1$.0f%%) 판단</string>
    <string name="reason_ambiguous">판단 불확실 (상위 후보 여러 개)</string>
    <string name="reason_stale_knowledge">최근 데이터 없음, 이전 판단 기준</string>
    <string name="reason_offline_mode">오프라인 모드 — 저장된 정보로 판단</string>
    <!-- ... -->
</resources>
```

## 25-5. 키워드 사전 (keywords.xml, 한국어 예시)

```xml
<resources>
    <string-array name="scam_keywords">
        <item>보이스피싱</item>
        <item>대포폰</item>
        <item>사기</item>
        <item>사칭</item>
        <item>피싱</item>
        <item>금융사기</item>
        <!-- ... 200건 초기값 -->
    </string-array>
    <string-array name="ad_keywords">
        <item>광고</item>
        <item>할인</item>
        <item>프로모션</item>
        <!-- ... 150건 초기값 -->
    </string-array>
    <!-- ... -->
</resources>
```

---
