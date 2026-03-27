# CallCheck QA 전수 매트릭스 V1

**작성일**: 2026-03-27
**대상 버전**: CallCheck 1.0
**검증 기준**: "같은 전화 → 모든 접점 결과 100% 동일. 하나라도 다르면 실패."

---

## 1. 구조적 결함 수정 (본 QA에서 발견 → 수정 완료)

### 1.1 문제

| 접점 | 이전 색상 소스 | 문제 |
|---|---|---|
| Ring (앱) | `RingState.fromRiskLevel(result.riskLevel)` | riskLevel 기반 |
| Overlay | `RingState.fromRiskLevel(it.riskLevel)` | riskLevel 기반 |
| Widget | `RingState.fromRiskLevel(item.riskLevel)` | riskLevel 기반 |
| Notification | `RingSystem.color(result.action)` | **action 기반** |

**riskLevel은 중간값**, action은 **최종 판정**. Category + RiskLevel 조합으로 action이 결정되므로, riskLevel=LOW여도 action=CAUTION이 될 수 있음. 결과: **5/10 케이스에서 접점 간 색상 불일치**.

### 1.2 수정 (action 기반 통일)

| 파일 | 변경 |
|---|---|
| `RingState.kt` | `fromAction(ActionRecommendation)` 추가 |
| `RingSystem.kt` | `emoji(action)`, `labelKo(action)`, `labelEn(action)` 추가 |
| `DecisionCardScreen.kt` | `fromRiskLevel()` → `fromAction(result.action)` |
| `CallOverlayContent.kt` | `fromRiskLevel()` → `fromAction(it.action)` |
| `RecentDecisionWidgetContent.kt` | `fromRiskLevel()` → `fromAction(item.action)`, `WidgetDecisionItem`에 action 필드 추가 |
| `DecisionNotificationManager.kt` | `emoji(result.riskLevel)` → `emoji(result.action)`, `labelKo` 동일 |

**빌드 검증**: compileDebugKotlin 성공, 에러 0개.

---

## 2. QA 전수 매트릭스 (10 케이스 x 6 접점)

### Q01: 저장 연락처 — 위험 신호 없음

- Device: isSavedContact=true / Search: 없음
- Relationship=1.0, Risk=0.0
- Category: KNOWN_CONTACT → RiskLevel: LOW → **Action: SAFE_LIKELY**
- Ring: 🟢 / Overlay: 🟢 / Notification: 🟢 / Widget: 🟢 / Label: "안전 추정" / Emoji: 🟢
- **✅ PASS — 전 접점 초록**

### Q02: 저장 연락처 + 스캠 신호 (스푸핑)

- Device: isSavedContact=true / Search: hasScamSignal=true
- Relationship=1.0, Risk=0.04 (0.40x0.1 감쇄)
- Category: **SCAM_RISK_HIGH** (스푸핑 방어) → RiskLevel: LOW → **Action: RISK_HIGH**
- Ring: 🔴 / Overlay: 🔴 / Notification: 🔴 / Widget: 🔴 / Label: "위험 높음" / Emoji: 🔴
- **✅ PASS — 전 접점 빨강** (수정 전: Ring=🟢, Notification=🔴 ❌)

### Q03: 미저장 + 발신 3회 + 장통화 (비즈니스)

- Device: outgoing=3, connected=3, longCall=1 / Search: 없음
- Relationship=0.65, Risk=0.0
- Category: BUSINESS_LIKELY → RiskLevel: LOW → **Action: SAFE_LIKELY**
- Ring: 🟢 / Overlay: 🟢 / Notification: 🟢 / Widget: 🟢 / Label: "안전 추정" / Emoji: 🟢
- **✅ PASS — 전 접점 초록**

### Q04: 미저장 + 첫 수신 + 스캠 검색

- Device: 없음 / Search: hasScamSignal=true
- Relationship=0.0, Risk=0.40
- Category: SCAM_RISK_HIGH → RiskLevel: MEDIUM → **Action: RISK_HIGH**
- Ring: 🔴 / Overlay: 🔴 / Notification: 🔴 / Widget: 🔴 / Label: "위험 높음" / Emoji: 🔴
- **✅ PASS — 전 접점 빨강**

### Q05: 미저장 + 첫 수신 + 스팸 검색

- Device: 없음 / Search: hasSpamSignal=true
- Relationship=0.0, Risk=0.25
- Category: SALES_SPAM_SUSPECTED → RiskLevel: LOW → **Action: CAUTION**
- Ring: 🟡 / Overlay: 🟡 / Notification: 🟡 / Widget: 🟡 / Label: "주의" / Emoji: 🟡
- **✅ PASS — 전 접점 노랑** (수정 전: Ring=🟢, Notification=🟡 ❌)

### Q06: 미저장 + 첫 수신 + 배달 검색

- Device: 없음 / Search: hasDeliverySignal=true
- Relationship=0.0, Risk=0.0
- Category: DELIVERY_LIKELY → RiskLevel: LOW → **Action: CAUTION**
- Ring: 🟡 / Overlay: 🟡 / Notification: 🟡 / Widget: 🟡 / Label: "주의" / Emoji: 🟡
- **✅ PASS — 전 접점 노랑** (수정 전: Ring=🟢, Notification=🟡 ❌)

### Q07: 미저장 + 수신 1회 (증거 부족)

- Device: connectedCount=1 / Search: 없음
- Relationship=0.10, Risk=0.0
- Category: INSUFFICIENT_EVIDENCE → RiskLevel: LOW → **Action: UNKNOWN**
- Ring: ⚪ / Overlay: ⚪ / Notification: ⚪ / Widget: ⚪ / Label: "판단 불가" / Emoji: ⚪
- **✅ PASS — 전 접점 회색** (수정 전: Ring=🟢, Notification=⚪ ❌)

### Q08: 미저장 + 첫 수신 + 기관 검색

- Device: 없음 / Search: hasInstitutionSignal=true
- Relationship=0.0, Risk=0.0
- Category: INSTITUTION_LIKELY → RiskLevel: LOW → **Action: CAUTION**
- Ring: 🟡 / Overlay: 🟡 / Notification: 🟡 / Widget: 🟡 / Label: "주의" / Emoji: 🟡
- **✅ PASS — 전 접점 노랑** (수정 전: Ring=🟢, Notification=🟡 ❌)

### Q09: 미저장 + 첫 수신 + 검색 결과 없음

- Device: 없음 / Search: 없음
- Relationship=0.0, Risk=0.0, hasAnyEvidence=false
- Category: INSUFFICIENT_EVIDENCE → RiskLevel: UNKNOWN → **Action: UNKNOWN**
- Ring: ⚪ / Overlay: ⚪ / Notification: ⚪ / Widget: ⚪ / Label: "판단 불가" / Emoji: ⚪
- **✅ PASS — 전 접점 회색**

### Q10: Private / Emergency / Null

- "private", "911", null → ALLOW 즉시 반환, UI 완전 무개입
- Ring: 없음 / Overlay: 없음 / Notification: 없음
- **✅ PASS — UI 무개입 정책 정상**

---

## 3. 최종 결과

| # | 케이스 | Action | 색상 | 일관성 | 결과 |
|---|---|---|---|---|---|
| Q01 | 저장 연락처 | SAFE_LIKELY | 🟢 | 6/6 | **PASS** |
| Q02 | 저장+스캠 | RISK_HIGH | 🔴 | 6/6 | **PASS** |
| Q03 | 비즈니스 | SAFE_LIKELY | 🟢 | 6/6 | **PASS** |
| Q04 | 스캠 검색 | RISK_HIGH | 🔴 | 6/6 | **PASS** |
| Q05 | 스팸 검색 | CAUTION | 🟡 | 6/6 | **PASS** |
| Q06 | 배달 검색 | CAUTION | 🟡 | 6/6 | **PASS** |
| Q07 | 증거 부족 | UNKNOWN | ⚪ | 6/6 | **PASS** |
| Q08 | 기관 검색 | CAUTION | 🟡 | 6/6 | **PASS** |
| Q09 | 완전 미지 | UNKNOWN | ⚪ | 6/6 | **PASS** |
| Q10 | 스킵 대상 | N/A | — | N/A | **PASS** |

**총 결과: 10/10 PASS (100%)**

---

## 4. 수정 전후 비교 (문제 5건)

| 케이스 | 수정 전 Ring | 수정 전 Notif | 수정 후 전 접점 |
|---|---|---|---|
| Q02 저장+스캠 | 🟢 | 🔴 | 🔴 통일 |
| Q05 스팸 | 🟢 | 🟡 | 🟡 통일 |
| Q06 배달 | 🟢 | 🟡 | 🟡 통일 |
| Q07 증거부족 | 🟢 | ⚪ | ⚪ 통일 |
| Q08 기관 | 🟢 | 🟡 | 🟡 통일 |

---

## 5. 검증 체크리스트

- [x] RingState: fromAction(ActionRecommendation) 추가
- [x] RingSystem: action 기반 emoji/labelKo/labelEn 추가
- [x] DecisionCardScreen: fromRiskLevel -> fromAction
- [x] CallOverlayContent: fromRiskLevel -> fromAction
- [x] RecentDecisionWidgetContent: fromRiskLevel -> fromAction
- [x] WidgetDecisionItem: action 필드 추가
- [x] DecisionNotificationManager: emoji/labelKo -> action 기반
- [x] 빌드 검증: compileDebugKotlin 성공, 에러 0개
- [x] 10/10 QA 케이스 전수 통과
