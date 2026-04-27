# 제품 전략 (Product Strategy) — One Engine, Four Surfaces

**원본 출처**: v1.7.1 §17 (63줄)
**v1.8.0 Layer**: Product Design
**의존**: `00_core/01_primary.md` + `05_constitution.md`
**변경 이력**: 본 파일은 v1.7.1 §17 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cowork/06_product_design/05_product_strategy.md`

---

# 17. 제품 전략 (Product Strategy) — One Engine, Four Surfaces

본 장은 헌법 7조와 황금알 정의(§3)가 **제품 로드맵 차원**에서 어떻게 구현되는지 명시한다.

## 17-1. One Engine 원칙 재선언

- **엔진은 하나**: Decision Engine은 단일 구현체. 4 Surface가 공유한다.
- **엔진 분기 금지**: "CallCheck 전용 Decision Engine" 같은 분기 금지. 입력 `IdentifierType`으로 분기하되, 동일 엔진 내부에서 처리.
- **Surface는 얇다**: Surface는 입력 수집 + 출력 렌더링만 담당. 판단 로직은 엔진.
- **확장은 Surface 추가로**: 새 기능 추가 시 엔진 변경 없이 Surface 신설.

## 17-2. v1.6 Four Surfaces

| # | Surface | 입력 원천 | 출력 | 구현 상태 (v1.6.1) |
|---|---|---|---|---|
| 1 | CallCheck | `TelephonyManager.CALL_STATE_RINGING` 전화번호 | CallRisk (4속성) | 기준 (v1.3~) |
| 2 | MessageCheck | MMS/SMS 발신번호 + URL + 기관명 (Mode A/B) | MessageRisk (4속성) | 복원 (Patch 18) + 2-모드 (Patch 29) |
| 3 | MicCheck | RECORD_AUDIO 보유 앱 목록 | List<MicPermissionEntry> (단순 관리자, Patch 30) | 축소 (Patch 30) |
| 4 | CameraCheck | CAMERA 보유 앱 목록 | List<CameraPermissionEntry> (단순 관리자, Patch 30) | 축소 (Patch 30) |

## 17-3. 향후 Surface (후행, 본 문서 범위 밖)

| Surface | 개요 | 우선순위 | 이관 이력 |
|---|---|---|---|
| **PushCheck (푸시 휴지통)** | NotificationListenerService 기반, 스팸 지정 발신자 알림 자동 cancel → 자체 DB 저장 → 휴지통 UI | 메모리 #14 + `통합운영설계안_v1 §2.9` "실제 격리 + UI 필수" 확정 | — |
| **AppSecurityWatch** | 신규 앱 설치 시 과거 보안 사고 이력 자동 검색·경고 + 기존 앱 신규 CVE/침해 사고 실시간 감지·알림 + NVD CVE·CISA KEV·Have I Been Pwned 조회 + Decision Engine 기반 앱 평판 판정 | 메모리 #13-1/2 | **Patch 31로 MicCheck/CameraCheck에서 분리 이관** |
| UrlCheck | 브라우저 공유 URL 검사 | 후행 | — |

본 v1.7.0에서는 **Four Surfaces만** 정식 스펙. 위 후행 Surface는 별도 워크오더로 진입.

**이관 근거 (Patch 31)**: MicCheck/CameraCheck는 대표님 지시(2026-04-24)에 따라 "리스트 + 최근 사용 + 회수 버튼" 3기능의 **단순 관리자**로 축소되었다. 이관된 감시·CVE·침해 판정 기능은 **별도의 구체적 Surface**로 재탄생하는 것이 헌법 7조 "구체적 Surface 원칙"에 정합하다.

**PushCheck cross-ref (Lane 1 D34 반영)**: `통합운영설계안_v1.docx §2.9`에서 "실제 격리 + UI 필수"로 확정된 내용은 본 v1.6.1 범위 밖이며, `docs/00_governance/patches/PATCH_v1.7.md Patch 31`로 이관 예정. 본 §17-3이 이관처를 명시한다.

## 17-4. Phase 로드맵

| Phase | 범위 | 검증 게이트 |
|---|---|---|
| Phase 0: Foundation | Stage 0 Contracts (5 파일 / 22 시그니처) | FreezeMarkerTest 22개 PASS ✓ (Patch 37 통일, 이미 완료) |
| Phase 0-hotfix | Java 17 전체 승격 | CI 빌드 PASS ✓ (이미 완료) |
| Phase 1: CallCheck MVP | 4속성 출력 · NKB · 3계층 소싱 · L3 경로 | 스모크런 1~5, 11 PASS |
| Phase 2: MessageCheck | MessageRisk + 3중 평가 + URL probe | 스모크런 6~7 PASS |
| Phase 3: MicCheck / CameraCheck | `MicPermissionEntry`/`CameraPermissionEntry` + PackageManager 스캔 (단순 관리자, Patch 30·37) | 스모크런 8~9 PASS |
| Phase 4: Billing 통합 | Play Billing + 구독 상태 UX | 스모크런 10 PASS |
| Phase 5: 다국어·접근성 | 190개국 strings.xml + TalkBack | SmokeRun12 (다국어 렌더 확인) |
| Phase 6: 안정화 + Store | 90일 Vitals 모니터링 + 스토어 심사 대응 | Play Console 승인 |
| Phase 7: iOS 진입 | v2.0.0 이후 | 별도 MAJOR 계획 |

## 17-5. Rule 3 충돌 해소

헌법·메모리·입력 자료가 충돌할 때 다음 규칙:

- **Rule 1**: 정식 문서 간 충돌 → 후시간 우선
- **Rule 2**: 대화록 내부 충돌 → 위쪽(최근) 우선
- **Rule 3**: 정식 문서 vs 대화록 충돌 → 비전 판정 후 대표님 확인

본 v1.6.1 재작성에서 적용한 Rule 3 사례:
- v1.5.1 원문 "사용자 1.5 USD 가격" ↔ 메모리 #11 "$2.49/월" → 메모리 후시간, $2.49 채택 (v1.6.1·v1.6.2 모두 승계)
- v1.5.1 원문 "PrivacyCheck 포함" ↔ v1.6.0 Patch 21 "PrivacyCheck 폐기 → MicCheck/CameraCheck" → v1.6.0 후시간, 폐기 반영
- v1.5.1 원문 "BROADCAST_SMS 권한" ↔ Patch 17 "제거" → Patch 후시간, 제거 반영

---

