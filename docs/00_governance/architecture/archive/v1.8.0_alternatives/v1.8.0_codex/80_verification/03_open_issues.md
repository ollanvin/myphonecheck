# 21. Open Issues (12건, v1.4_disc 계승 + v1.6.1 갱신)

**원본 출처**: v1.7.1 §21 (44줄)
**v1.8.0 Layer**: Verification
**의존**: `80_verification/02_success_criteria.md` + `80_verification/04_round5_consensus.md`
**변경 이력**: 본 파일은 v1.7.1 §21 (44줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/80_verification/03_open_issues.md`

---


v1.4_disc에서 제기된 12건 Open Issues와 현재 해결 상태.

| # | 이슈 | 원안 | v1.6.1 상태 |
|---|---|---|---|
| 01 | 본사 매핑 0 판정 기준 애매 | "본사가 매핑 안 한다"의 정의 | §15 자동 검증 스크립트로 해결 |
| 02 | 가격 net ARPU 실측 없음 | 경쟁사 분석만 | $2.49 확정 + Play Console 실측 대기 |
| 03 | Softmax 가중치 튜닝 | 초기값만 | Phase 1 실기기 데이터 수집 후 튜닝 |
| 04 | Cold Start UX 길이 | 30초 목표 근거 부재 | §11 구체 단계 명시, 실측 대기 |
| 05 | 키워드 사전 규모 | 국가당 몇 건 필요 | strings.xml ko: 200건 / en: 300건 초기값 |
| 06 | Stale 재검증 우선순위 | 랜덤 vs FIFO | WorkManager 균등 분산 (20건/일) |
| 07 | UserAction 충돌 (신고·안심 동시) | 최근 우선 | `actionType.ordinal` 높은 쪽 우선 |
| 08 | NKB 50MB 상한 도달 시 | archive 처리 근거 | 365일 경과 엔트리 archive 플래그 |
| 09 | 공공 API 장애 대응 | 문서 부재 | §14 SLA L2 Degraded 상태 정의 |
| 10 | 언어-국가 분리 | 언어 ≠ 국가 | §28 명시 (Patch 05) |
| 11 | iOS 계획 | MAJOR 이동 결정 | v2.0.0 범위 |
| 12 | Four Surfaces 우선순위 | 모두 병행 vs 순차 | Phase 1(Call) → Phase 2(Message) → Phase 3(Mic/Camera) 순차 |
| **13** | **DO_NOT_MISS 처분** (Patch 35) | v1.0 §4.1 E2E 완성 기능이 v1.6.1-patch까지 실종 (Lane 1 D05) | **§8-2-2 `UserAction.DoNotMiss` 서브타입으로 복원, §3-4-1 UX 강조 규칙, §21-1-1 적용 단계 표 신설. Phase 1 CallCheck부터 점진 적용** |
| **14** | **NKB at-rest 암호화** (Patch 34) | v1.0 §13 SQLCipher/AES-256 DONE → v1.6.1-patch 실종 (Lane 1 D06) | **§27-5에 SQLCipher + Android Keystore 명세 복원 (코웍 87a9a3 §8-0 흡수)** |
| **15** | **AppSecurityWatch Surface** (Patch 31) | MicCheck/CameraCheck에 포함됐던 CVE·침해 감시 → 단순 관리자 축소(Patch 30)로 분리 필요 | **§17-3에 후행 Surface로 이관, 별도 워크오더 진입 예정** |
| **16** | **MessageCheck Mode A/B** (Patch 29) | "Default SMS Handler 재설계 또는 NotificationListenerService" 모호 (자비스 Lane 4 지적) | **§18-4-0~7로 2-모드 명시, Mode B(Share Intent) 기본·Mode A(Default SMS) 사용자 선택** |
| **17** | **QUERY_ALL_PACKAGES 대안** (Patch 36) | 자비스 Lane 4 "Play 거의 100% 리젝" 지적 | **§24-6-1 Manifest에서 제거 + `<queries>` 블록으로 대체** |
| **18** | **Data Safety 정직 재선언** (Patch 32·33) | "수집 0" vs 실제 SMS·통화·앱 접근 모순 (자비스 Lane 4 허위 disclosure 지적) | **§27 전면 재작성: "Yes, app collects user data" + "No third-party sharing" + "Processed only on device" 정확 마킹, §27-3 Permissions Declaration 본문 신설** |

## 21-1. DO_NOT_MISS 처분 적용 단계 표 (Patch 35 신설)

본 표는 §8-2-4의 Phase별 적용 범위를 본문에 정식 등재한다. 코웍 87a9a3 §17-6-5 기반.

| Phase | Surface | DO_NOT_MISS 적용 효과 |
|---|---|---|
| Phase 1 | CallCheck | 착신 오버레이 dismiss 5초 → 15초 / High-Priority Notification Channel / 노란 강조 띠 |
| Phase 2 | MessageCheck Mode A·B | HIGH 알림 채널 분기, 메시지 카드 상단 강조 |
| Phase 3 | MicCheck·CameraCheck | 특정 앱 권한 변동(설치·업데이트 시) 즉시 강조 알림 |
| Phase 후행 | PushCheck·AppSecurityWatch | 동일 규칙 자동 계승 |

### 21-1-1. 데이터 수명

- 사용자가 명시 해제하기 전까지 영구 유지 (Stale 정책 제외)
- 디바이스 초기화·앱 데이터 삭제 시에만 소실
- 구독 만료와 무관하게 유지 (UX 자산)

---

