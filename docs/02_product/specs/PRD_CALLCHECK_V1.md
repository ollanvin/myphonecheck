# MyPhoneCheck 1.0 — Product Requirements Document

## 1. Product Definition

**App Name:** MyPhoneCheck
**Domain:** callcheck.app
**Package:** app.myphonecheck.mobile
**Platform:** Android only (iOS excluded)
**Tagline:** "Decide before you answer"
**Price:** USD 1/month, single global plan, no ads
**Scope:** 190 countries simultaneous launch

MyPhoneCheck is an Android-only incoming-call decision app.
When an incoming phone number is NOT saved on the device, the app intercepts the call,
analyzes on-device evidence first, enriches with search-platform evidence second,
and shows a decision card within 3 seconds so the user can choose: Answer / Reject / Block.

This is NOT a caller-ID database app.
This is NOT a search app.
This is a **decision-support app**.

---

## 2. Core User Promise

"모르는 번호, 받기 전에 먼저 체크"
"Decide before you answer."

---

## 3. Non-Negotiable Rules

1. Android only
2. No central phone-number database
3. No persistent central storage of user phone history
4. Use on-device evidence FIRST
5. Use search-platform evidence only as secondary enrichment
6. UI must stay simple
7. Show ONE final summary + max 3 supporting reasons
8. Product UX target: 3 seconds
9. Never claim certainty when evidence is weak — use possibility-based wording
10. Global launch across 190 countries from day 1
11. Single global pricing: USD 1/month
12. No ad-based model
13. No B2B features

---

## 4. Decision Flow (3-Stage Engine)

### Stage 1: Saved Check
- If number is saved in contacts → show known contact name → recommend Answer

### Stage 2: Device Evidence Analysis
- If NOT saved but device history exists → "Relationship Recovery Mode"
- Analyze:
  - Outgoing call attempts by user
  - Incoming call attempts by other party
  - Answered incoming calls
  - Rejected calls
  - Missed calls
  - Successfully connected calls (with duration)
  - Short calls (< 10s)
  - Long meaningful calls (> 60s)
  - Total/average duration
  - Last interaction timestamps (by type)
  - SMS existence and last timestamp

### Stage 3: Search Platform Enrichment
- If NO device history or evidence is weak → "External Inference Mode"
- Search signals:
  - Recent search intensity (30d / 90d)
  - Keyword clustering (delivery, hospital, company, loan, spam, scam)
  - Repeated entity names (company, brand, courier)
  - Source types (official site, community, blog, news, spam-report)

---

## 5. Device Evidence Model

```kotlin
data class DeviceEvidence(
    val isSavedContact: Boolean,
    val contactName: String?,
    val outgoingCount: Int,
    val incomingCount: Int,
    val answeredCount: Int,
    val rejectedCount: Int,
    val missedCount: Int,
    val connectedCount: Int,
    val totalDurationSec: Long,
    val avgDurationSec: Long,
    val shortCallCount: Int,   // < 10s
    val longCallCount: Int,    // > 60s
    val lastOutgoingAt: Long?,
    val lastIncomingAt: Long?,
    val lastConnectedAt: Long?,
    val lastRejectedAt: Long?,
    val lastMissedAt: Long?,
    val recentDaysContact: Int, // within last N days
    val smsExists: Boolean,
    val smsLastAt: Long?,
    val localTag: String?,
    val localMemo: String?
)
```

---

## 6. Search Evidence Model

```kotlin
data class SearchEvidence(
    val recent30dSearchIntensity: Int?,
    val recent90dSearchIntensity: Int?,
    val searchTrend: SearchTrend, // INCREASING, STABLE, LOW, NONE
    val keywordClusters: List<String>,
    val repeatedEntities: List<String>,
    val sourceTypes: List<String>,
    val topSnippets: List<String>
)
```

---

## 7. Decision Output Model

```kotlin
enum class ConclusionCategory {
    KNOWN_CONTACT,
    BUSINESS_LIKELY,
    DELIVERY_LIKELY,
    INSTITUTION_LIKELY,
    SALES_SPAM_SUSPECTED,
    SCAM_RISK_HIGH,
    INSUFFICIENT_EVIDENCE
}

enum class ActionRecommendation {
    ANSWER,
    ANSWER_WITH_CAUTION,
    REJECT,
    BLOCK_REVIEW,
    HOLD
}

enum class RiskLevel {
    HIGH, MEDIUM, LOW, UNKNOWN
}

data class DecisionResult(
    val riskLevel: RiskLevel,
    val category: ConclusionCategory,
    val action: ActionRecommendation,
    val confidence: Float,      // 0.0 ~ 1.0
    val summary: String,        // one-line conclusion
    val reasons: List<String>,  // max 3
    val deviceEvidence: DeviceEvidence?,
    val searchEvidence: SearchEvidence?
)
```

---

## 8. Decision Rules (v1.0)

### KNOWN_CONTACT
- isSavedContact = true
- No spam tag
→ ANSWER

### BUSINESS_LIKELY
- Not saved
- connectedCount >= 2
- outgoingCount > 0 OR smsExists = true
- lastConnectedAt within recent period
→ ANSWER

### DELIVERY_LIKELY
- Not saved
- Low connection count (0-1) or short calls
- Search keywords: delivery / courier / shipping / logistics
- Recent search intensity exists
→ ANSWER_WITH_CAUTION

### INSTITUTION_LIKELY
- Search keywords: hospital / school / office / government
- Repeated entity matches institution name
→ ANSWER_WITH_CAUTION

### SALES_SPAM_SUSPECTED
- Not saved
- missedCount + rejectedCount high
- connectedCount low
- Search keywords: ad / loan / investment / sales / telemarketing
→ REJECT

### SCAM_RISK_HIGH
- Not saved
- No device history
- Search keywords: scam / phishing / fraud / 보이스피싱
- Rapid repeated attempts in short window
→ BLOCK_REVIEW

### INSUFFICIENT_EVIDENCE
- No device history
- Weak or absent search signals
→ HOLD (never auto-block on absence of evidence)

---

## 9. UI Structure

### Decision Card (main surface)

**Top:** Risk Badge (HIGH / MEDIUM / LOW / UNKNOWN)

**Center:** One-line conclusion
- Examples:
  - "거래처/업무 번호 가능성 높음"
  - "택배/배송 가능성 높음"
  - "광고/영업 의심"
  - "스팸/사기 위험 높음"
  - "판단 근거 부족"

**Evidence:** 3 supporting reasons (max)
- "과거 통화 3회 / 실제 연결 2회"
- "최근 통화 2일 전"
- "배송 관련 검색 결과 다수"

**Actions:** Answer / Reject / Block

**Expandable Detail:**
- Full device evidence breakdown
- Search evidence summary
- Disclaimer: "이 결과는 웹 검색 기반 요약이며 정확성을 보장하지 않습니다"

### Settings Screen
- Language
- Country (auto/manual)
- Evidence display level
- Privacy/permission info
- Subscription status

### Paywall Screen
- Minimal
- USD 1/month
- Single plan

---

## 10. Timing Budget

| Phase | Target | Action |
|-------|--------|--------|
| 0.0 - 0.2s | Number normalize + contact check | Instant |
| 0.2 - 0.8s | Device evidence summary | Local query |
| 0.8 - 1.8s | Search platform 1st response | Network |
| 1.8 - 2.5s | Final decision card | Merge + render |
| 2.5 - 3.0s | User decision window | Action ready |

**Rule:** Show local evidence first, enrich with search asynchronously.

---

## 11. Technical Architecture

### Modules
```
MyPhoneCheck/
├── app/
├── core/
│   ├── model/
│   ├── util/
│   └── phone/
├── feature/
│   ├── call-intercept/
│   ├── device-evidence/
│   ├── search-enrichment/
│   ├── decision-engine/
│   ├── decision-ui/
│   ├── settings/
│   ├── billing/
│   └── country-config/
├── data/
│   ├── contacts/
│   ├── calllog/
│   ├── sms/
│   ├── search/
│   └── local-cache/
└── build-logic/
```

### Tech Stack
- Language: Kotlin
- UI: Jetpack Compose
- Architecture: MVVM / Clean
- DI: Hilt
- DB: Room (local cache only)
- Phone parsing: libphonenumber
- Async: Coroutines + Flow
- Call intercept: CallScreeningService (Android Telecom)
- Billing: Google Play Billing Library
- Min SDK: 26
- Target SDK: latest stable

### Android Permissions
- READ_CONTACTS
- READ_CALL_LOG
- READ_PHONE_STATE
- INTERNET (for search enrichment)
- POST_NOTIFICATIONS (Android 13+)

**Note:** Call Log / SMS permissions require Google Play policy compliance review.

---

## 12. Global Strategy

Single app, 190 countries. Country-specific behavior via configuration only.

### Country Config Parameters
- language
- phone normalization rules (via libphonenumber)
- default search provider priority
- keyword dictionaries (localized)
- UI phrase translations
- high-risk keyword sets

---

## 13. Pricing

**Pricing**: $2.49/month (monthly only, no annual plan). Global uniform pricing. See `myphonecheck_base_architecture_v1.md §11` for latest authoritative pricing.

---

## 14. What This App Is NOT

- NOT a caller-ID database
- NOT a community spam-report platform
- NOT a Truecaller clone
- NOT a search engine
- NOT an identity detection service

**This app is:** A privacy-first, on-device-first, 3-second decision engine for incoming calls.

---

## 15. Key Differentiator

Existing apps: Block spam using central databases.
MyPhoneCheck: Help users decide — not just block spam, but also rescue important unsaved numbers.

"스팸은 막고, 거래선은 살리고, 사용자는 즉시 결정."

---

## 16. Success Criteria

1. Unknown number received → decision card appears within 3 seconds
2. User can Answer / Reject / Block from single surface
3. Device evidence is granular (not collapsed into "total calls")
4. Search enrichment adds context without blocking local evidence display
5. Works offline for local evidence (search enrichment degrades gracefully)
6. 190 countries supported via single APK
7. Monthly subscription active via Google Play Billing

---

## 17. Known Risks

1. Google Play policy for Call Log / SMS permissions — requires compliance review
2. 3-second UX under real network conditions — requires timeout handling
3. Search provider response variance — requires parallel + fallback
4. Country-specific number format edge cases — requires thorough libphonenumber usage
5. Decision accuracy for edge cases — v1.0 uses rules, ML can come later

---

*Document version: 1.0*
*Created: 2026-03-24*
*Project: MyPhoneCheck*
*Domain: callcheck.app*
