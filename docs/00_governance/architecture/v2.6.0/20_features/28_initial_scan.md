# 28. Initial Scan — 최초 론칭 디바이스 스캔

> **신규 Surface (v2.0.0)**: 앱 최초 론칭 후 디바이스를 스캔하여 6 Surface 베이스데이터·베이스양식 일괄 구축.
> 기준 헌법: `docs/00_governance/architecture/v2.0.0/05_constitution.md` (제8조 SIM-Oriented Single Core)

---

## 28-1. 정의

**Initial Scan**은 사용자 동의 후 디바이스의 다음 자원을 스캔하여 6 Surface 베이스데이터를 일괄 구축하는 1회성 + 점진 갱신 프로세스.

핵심 가치: **빈 상태 시작 → 즉시 활용 가능한 베이스 컨텍스트 확보**.

원칙:
- 사용자 동의 우선 (스캔 전 명시적 권한 요청)
- 외부 통신 0 (헌법 1조)
- 원문 폐기, 정규화·메타데이터만 저장 (헌법 2조)
- SimContext 우선 확정 (헌법 8조)

## 28-2. 스캔 대상 자원

| 자원 | 권한 | 베이스데이터 |
|---|---|---|
| SIM 정보 | READ_PHONE_STATE | 국가, 통신사, MCC/MNC, SimContext 확정 |
| CallLog | READ_CALL_LOG | 발신·수신 패턴, 자주 통화 번호, libphonenumber 정규화 |
| SMS Inbox | READ_SMS | 발신자 인벤토리, 카드사 후보, 스팸 후보 |
| NotificationListener | NLS | (등록만, 향후 알림 자동 수집) |
| PackageManager | (시스템) | 권한 보유 앱 인벤토리 (마이크/카메라/SMS 권한) |

## 28-3. 작동 흐름

```
[앱 최초 론칭]
     ↓
[권한 동의 UI (한 번에 또는 단계별)]
     ↓
[SIM 기반 SimContext 확정 — §29 + 헌법 8조]
     ↓
[병렬 스캔 — :core:global-engine/parsing/ 활용]
  ├─ CallLog → CallCheck 베이스 (libphonenumber 정규화 + 빈도)
  ├─ SMS Inbox → MessageCheck 베이스 + CardCheck 후보 (Source Detector)
  ├─ PackageManager → MicCheck/CameraCheck 인벤토리
  └─ NLS 등록 → PushCheck 활성화
     ↓
[베이스데이터 영구 저장 (디바이스 로컬, Room DB)]
     ↓
[사용자에게 "스캔 완료" 알림 + 6 Surface 활성화]
```

## 28-4. 헌법 1조 (Out-Bound Zero) 정합

- 스캔 결과 외부 전송 0
- 모든 처리 디바이스 로컬
- 사용자 동의 후 디바이스 자원 읽기만

## 28-5. 헌법 2조 (In-Bound Zero) 정합

- 원문 폐기, 메타데이터·정규화 결과만 저장
- SMS 본문, CallLog 통화 내역 등 원문 영구 저장 안 함
- featureCounts·발신자 인벤토리·카드 후보 등 추출 결과만 저장

## 28-6. 헌법 3조 (결정권 중앙집중 금지) 정합

- 디바이스 로컬 스캔, 외부 결정 없음
- 사용자가 스캔 동의·범위·재실행 모두 통제
- v2.0.0 강화 (§3 본문 주석): 온디바이스 코어 엔진 활용은 본 조 비대상

## 28-7. 헌법 8조 (SIM-Oriented Single Core) 정합

- SimContext 확정이 스캔 시작 시점 첫 단계
- 모든 후속 파싱이 SimContext 기반 (`countryIso`, `phoneRegion`, `currency`)
- SIM 부재 시 디바이스 Locale fallback + 사용자 명시 (§29-5)

## 28-8. 점진 갱신 (Incremental Update)

Initial Scan 1회 후 추가 데이터 발견 시:
- 새 SMS·통화·앱 설치 → 자동 점진 갱신
- 사용자 트리거 재스캔 가능 (Settings 화면)
- SIM 변경 시 베이스 컨텍스트 재계산 동의 요청 (§29-4)
- 베이스데이터 초기화 옵션 (Settings → CardCheck/PushCheck 등 각 Surface)

## 28-9. 사용자 대면 약속

> **"최초 1회 디바이스를 스캔하여 6 Surface 베이스를 구축합니다.**
> **모든 결과는 디바이스 내부에만 저장되며, 어떤 외부 서버로도 전송되지 않습니다.**
> **언제든 재스캔 또는 베이스데이터 초기화 가능합니다."**

## 28-10. 모듈 매핑

- `:feature:initial-scan` (신규, Stage 2-001 후속 PR로 구현)
  - `ScanCoordinator.kt` — 권한 → SimContext → 병렬 스캔 → 저장 흐름
  - `ScanResult.kt` — 스캔 결과 (Surface별 카운트·요약)
  - `ScanProgressViewModel.kt` — 진행률 UI
- `:core:global-engine/sim-context/` (의존, §29)
- `:core:global-engine/parsing/` (의존, §30 — phone, message, currency, notification)
- `:data:local-cache` (베이스데이터 저장)

## 28-11. Stage 2-001 구현 범위 (예정)

본 §28은 v2.0.0 명문화. 실제 모듈 구현은 별도 PR:

1. `:feature:initial-scan` 모듈 신설
2. 권한 동의 UI (Compose)
3. SimContext 확정 호출 (§29 모듈 활용)
4. 병렬 스캔 코디네이터 (CallLog/SMS/Package/NLS)
5. Room DB 베이스데이터 저장
6. 진행률 화면 + 완료 알림
7. 점진 갱신 트리거 (BroadcastReceiver, WorkManager)

## 28-12. cross-ref

- §29 SIM-Oriented Single Core (SimContext 확정 우선 단계)
- §30 :core:global-engine (스캔 파싱 활용)
- §95 Six Surfaces Integration (스캔 결과 활용 다이어그램)
- 헌법 §1·§2·§3·§8 정합 검증
