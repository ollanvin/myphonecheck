# Stage 3-006-REV — 매트릭스 12 시나리오 manual 검증

**WO**: WO-STAGE3-006-REV
**작성일**: 2026-04-29
**워커**: Claude Code

---

## 시나리오 매트릭스

| s | 본질 | SearchInput 타입 | 헌법 정합 | 검증 위치 |
|---|---|---|---|---|
| s01 | Tier Unknown DirectSearchButton primary | PhoneNumber | §10-formula-2axis Unknown | unit (TierMapping) + STUB (Compose color) |
| s02 | KR SIM 3 후보 | PhoneNumber | §1 v2.5.0 SimAiSearchRegistry | unit (Stage3MatrixScenarios) |
| s03 | Global default 2 후보 | PhoneNumber | §1 v2.5.0 최소 2개 보장 | unit |
| s04 | Naver AI ai=1 deeplink | PhoneNumber | §1 v2.5.0 NAVER_AI mode | unit |
| s05 | Google AI udm=50 | PhoneNumber | §1 v2.5.0 GOOGLE_AI_MODE | unit |
| s06 | SIM 변경 시 NAVER_AI default 무효 | PhoneNumber | §8 SIM-Oriented | unit (registry 후보 차이) |
| s07 | Custom Tab fallback URL 동일 | PhoneNumber | §1 §9-3 정합 | unit (URL 동일 검증) + STUB (instrumented) |
| s08 | 2축 가중치 합산 (NKB 0.40 + AI 0.60) | PhoneNumber | §10-formula-2axis | unit (TierMapping + DecisionEngine2AxisTest) |
| s09 | Empty signal Unknown | PhoneNumber | §10-formula-2axis Unknown | unit (TierMapping) |
| s10 | Saved contact + scam regression | PhoneNumber | DecisionEngineImpl v1 보존 | regression (PR diff 본문 변경 0) |
| s11 | 우리 송신 0 회귀 | (모든 타입) | §1 Out-Bound Zero | unit (URL 정적 검증) + STUB (OkHttp interceptor) |
| s12 | URL/MessageBody/AppPackage SearchInput | Url + MessageBody + AppPackage | §1 v2.5.0 SearchInput sealed | unit (toAiSearchQuery) |

## 실측 검증 (대표님 기준)

s05 / s04 — 대표님 2026-04-29 본인 폰 실측:
- 번호 0322379987 + Google AI Mode → TheCall + JUNKCALL.org 자체 통합 ✓
- 번호 0322379987 + Naver AI → 더콜 + 레드루팅 자체 통합 ✓

s12 — Stage 4 본격 진입 전 골격 검증. URL/MessageBody → AI 검색 모드에서도 동일 자체 통합 가능성 검증 (대표님 가설).

## STUB 시나리오 (instrumented 영역, 후속 PR로 위임)

- s01 일부 — DirectSearchButton color 분기 (Compose SemanticsNode)
- s07 일부 — 디바이스 OS Custom Tab 미지원 환경 fallback (실 디바이스 검증)
- s11 일부 — OkHttp interceptor로 우리 도메인 송신 0 instrumented 검증

본 시나리오는 unit test에서 정적 검증 + STUB 마크. 실 instrumented 통합은 Phase 4 매트릭스 영역 (`ScenarioMatrixTest`)와 별도 시리즈.
