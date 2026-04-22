# 워크오더: 헌법 패치 v1.7 반영 (WO-GOV-005)

**대상 도구**: Claude Code v2.1.117 (Windows 로컬)
**기동 모드**: Auto Mode 전제
**작업 위치**: `C:\Users\user\Dev\ollanvin\myphonecheck`
**발행자**: 비전
**발행일**: 2026-04-22
**역할**: 감사 역할 행사 — 거버넌스 변경

---

## 0. 전제 조건

- [ ] WO-RELAY-001 완료 (릴레이 폴더 구조 존재)
- [ ] WO-AUDIT-001 결과 ALL PASS (감사 통과 후만 헌법 패치)
- [ ] HEAD == origin/main, 작업 트리 clean

---

## 1. 작업 목적

2026-04-22 대표님 확정 사항 4건을 헌법군에 반영:

1. **가격 변경**: $2.49/월 단일, 연간 없음, 전세계 동일. 기존 "~1.5 USD" 및 "USD 1" 폐기.
2. **PushCheck 재정의**: SPEC v2.1 P6 폐기 → 푸시 휴지통 모델로 대체
3. **Mic/Camera 외부 이벤트**: 트리거형 감지 (신규 앱 설치 + 화면 진입). 폴링 배치 폐기.
4. **RiskLevel 정합**: 5단계(`core:common`) 정본 + 4단계(`core:model`) UI 매퍼 방식 명문화

---

## 2. 작업 절차

### Step 1: 환경 준비

```powershell
cd C:\Users\user\Dev\ollanvin\myphonecheck
git pull origin main
git status  # clean 확인
```

### Step 2: Base Architecture §11 가격 조항 변경

파일: `docs/01_architecture/myphonecheck_base_architecture_v1.md`

§11 "Subscription Model" 섹션에서 다음 두 조항을 교체:

**삭제할 원문**:
```
- 가격대는 **약 1.5달러 수준**을 선호한다.
```

**삽입할 신규 문단**:
```
- 가격은 **$2.49/월 단일 가격**으로 확정한다 (2026-04-22).
- **연간 플랜은 제공하지 않는다.** 월간 구독만 존재.
- 전세계 동일 가격 (국가별 차등 없음).
- 경쟁 가격 조사 근거 (2026-04): Whoscall $2.89, Hiya $3.99, Truecaller $4.49 대비 최저가 포지셔닝이면서 지속 가능 마진 확보.
```

**삭제할 원문** (31일 과금 조항은 유지, 디바이스당 1회는 유지):
```
(§11 본문 "가격대는 **약 1.5달러 수준**을 선호한다." 이 한 줄만 교체)
```

### Step 3: PRD §13 가격 조항 폐기

파일: `docs/02_product/specs/PRD_CALLCHECK_V1.md`

§13 Pricing 섹션의 "USD 1/month" 문구 삭제하고 다음으로 교체:
```
**Pricing**: $2.49/month (monthly only, no annual plan). Global uniform pricing. See `myphonecheck_base_architecture_v1.md §11` for latest authoritative pricing.
```

### Step 4: SPEC v2.1 P6 폐기 + 푸시 휴지통 정의

파일: `docs/03_engineering/integration/SPEC_2026-04-14_core_rebuild_v2.md`

§3.3 "P6 PushCheck — 통계 → 행동 CTA 연결" 섹션 **전체**를 다음으로 교체:

```markdown
### 3.3 P6 PushCheck — 푸시 휴지통 (2026-04-22 재정의)

#### 3.3.1 기존 v2.1 P6 (폐기)

통계 표시 + 1클릭 차단 모델. 2026-04-22 대표님 판단으로 "통계만 보여주는 반쪽 기능"이라 폐기. 본 문서에 이력으로만 남김.

#### 3.3.2 신규 모델: 푸시 휴지통

**핵심 사상**: 스팸 알림은 시스템 차원에서 완전 격리하되, 사용자가 나중에 되돌아볼 수 있는 "휴지통"에 보관.

**대형 플랫폼 처리 (Notification Channel ID 기반)**:
- 앱별로 Android Notification Channel ID 목록 자동 수집
- 사용자가 채널 단위로 허용/차단 (체크박스 UI)
- 차단된 채널의 알림은 수신 즉시 `NotificationListenerService.cancelNotification()` + Room DB에 저장
- 앱 내장 매핑 테이블(상위 30~50개 앱)로 채널 ID → 한글 라벨 변환

**중소형 앱 처리 (앱 단위)**:
- 채널 분리 안 한 앱 또는 단일 채널 앱: "전체 허용" 또는 "전체 차단" 2선택
- 기본값: "전체 허용" (사용자 명시적 차단 전까지 개입 안 함)
- 알림 본문 파싱 금지. 메타데이터만 사용.

**기술 요구사항**:
- `NotificationListenerService` 권한 (런타임 수동 허용 필요)
- `cancelNotification()`이 채널 ID 기반으로 정확히 작동하는지 기술 검증 필요 (Stage 1 초기에 수행)
- 차단 알림은 시스템 노출 0 (소리·진동·상단바·락스크린 전부 제로)
- 휴지통 UI는 MyPhoneCheck 앱 내부에만 존재

**유지보수성 원칙**:
- 알림 본문 파싱 금지 (다국어·오탐 리스크)
- 앱 내장 매핑 테이블은 앱 업데이트로 갱신 (원격 서버 없음)
- 채널 매핑 없는 앱은 원본 채널 이름 그대로 표시

**서버 의존성**: 0. 디바이스 완결형.
```

### Step 5: Base §6.3-6.4 Mic/Camera 외부 이벤트 추가

파일: `docs/01_architecture/myphonecheck_base_architecture_v1.md`

§6.3 Mic Check와 §6.4 Camera Check 각각에 다음 불릿 추가:

```markdown
- **외부 보안 사고 이력 표시 (2026-04-22 추가)**: 신규 앱 설치 시 `PACKAGE_ADDED` 브로드캐스트를 받아 디바이스가 그 순간 공개 DB(NVD 등)에 직접 쿼리하여 과거 마이크/카메라 관련 보안 사고 이력을 검색·표시한다. 이미 설치된 앱은 사용자가 Mic/Camera 유닛 화면 진입 시 Room 캐시를 확인(24시간 유효)하고 만료된 항목만 재쿼리한다. 주기적 배치 폴링은 하지 않는다 (배터리·네트워크 비용 회피). 서버 인프라 없음 — 디바이스가 필요할 때 공개 DB에 직접 접근.
```

### Step 6: 검색 3대 축 명문화

파일: `docs/01_architecture/global-single-core-system.md`

"Global Core" 섹션 끝에 다음 블록 추가:

```markdown
## Search Evidence — 3축 모델 (2026-04-22)

결정 엔진의 검색 증거는 3축으로 구성된다. 4개 체크 유닛 전부에 적용.

| 축 | 이름 | 데이터 원천 | 트리거 |
|---|---|---|---|
| L1 | Internal (내부) | 온디바이스 통화·문자·태그 이력 | 이벤트 인입 시 |
| L2 | External (외부) | 일반 검색엔진 (Chrome Custom Tab 1차) | 이벤트 인입 시 |
| L3 | Authoritative Open Data (오픈소스) | 정부 신고 DB, NVD, KISA, FTC 등 공신력 있는 기관의 공개 데이터 | 이벤트 인입 시 (디바이스가 직접 쿼리) |

**원칙**:
- 디바이스 완결형. 서버 인프라 없음.
- 디바이스가 "필요한 순간" 공개 API에 직접 접근.
- Room DB에 24시간 캐시 유지.

`core:common`의 `SearchEvidence.Layer` enum에 이미 L1_NKB, L2_SEARCH, L3_PUBLIC_DB 정의되어 있음 (Stage 0 동결 시점에 예견).
```

### Step 7: RiskLevel 매퍼 원칙 명문화

파일: `docs/00_governance/project-governance.md`

"In-repo architecture charter" 섹션 내 Stage 0 직후에 다음 불릿 추가:

```markdown
- **RiskLevel 이중 타입 정책 (2026-04-22)**: `core:common.risk.RiskLevel`(5단계: SAFE/SAFE_UNKNOWN/UNKNOWN/CAUTION/DANGER)을 **도메인 분류 정본**으로 한다. `core:model.RiskLevel`(4단계: HIGH/MEDIUM/LOW/UNKNOWN)은 **UI 표시 모델**로 유지. 변환은 단방향 매퍼(`:feature:decision-engine` 내 `RiskLevelMapper.kt`)로 처리. 매핑: SAFE→LOW, SAFE_UNKNOWN→LOW, UNKNOWN→UNKNOWN, CAUTION→MEDIUM, DANGER→HIGH. `core:common`은 FREEZE 상태 유지하므로 수정 불가. 역방향 매핑은 의미 손실로 인해 정의하지 않는다.
```

### Step 8: 패치 메타 파일 생성

파일: `docs/00_governance/patches/PATCH_v1.7.md` (신규 파일)

```markdown
# Architecture Patch v1.7

**기준**: v1.6.1 (Patch 23~28 통합)
**패치 ID**: v1.7
**적용일**: 2026-04-22
**발행자**: 비전
**반영자**: Claude Code (WO-GOV-005)
**승인**: 대표님 (founder@idolab.ai)

## Patch 30 — 가격 $2.49 단일화
Base §11 가격 조항 변경, PRD §13 가격 참조로 전환.

## Patch 31 — PushCheck 재정의 (푸시 휴지통)
SPEC v2.1 §3.3 P6 폐기, 푸시 휴지통 모델로 대체.

## Patch 32 — Mic/Camera 외부 이벤트 (트리거형)
Base §6.3-6.4에 외부 보안 사고 이력 표시 추가. 폴링 배치 금지.

## Patch 33 — 검색 3축 명문화
global-single-core-system.md에 L1/L2/L3 모델 추가.

## Patch 34 — RiskLevel 매퍼 정책
project-governance.md에 이중 타입 정책 추가.

## 이전 버전 대비 삭제 항목
- Base §11: "약 1.5달러 수준" 조항
- PRD §13: "USD 1/month" 조항
- SPEC v2.1 §3.3: P6 "통계 + 차단 CTA" 전체 섹션
```

### Step 9: 커밋 + Push + 보고서

```powershell
git add docs/01_architecture/myphonecheck_base_architecture_v1.md
git add docs/02_product/specs/PRD_CALLCHECK_V1.md
git add docs/03_engineering/integration/SPEC_2026-04-14_core_rebuild_v2.md
git add docs/01_architecture/global-single-core-system.md
git add docs/00_governance/project-governance.md
git add docs/00_governance/patches/PATCH_v1.7.md

git diff --cached --stat
# 기대: 정확히 위 6개 파일만

git commit -m "docs(governance): apply architecture patch v1.7

- Patch 30: Pricing locked at \$2.49/mo single plan (replaces ~1.5 USD / USD 1)
- Patch 31: PushCheck redefined as Push Trash model (channel-based, no parsing)
- Patch 32: Mic/Camera external event detection (trigger-based, no polling)
- Patch 33: Search 3-axis model documented (L1 internal / L2 external / L3 open data)
- Patch 34: RiskLevel dual-type policy (5-band canonical + 4-band UI mapper)

Refs: WO-GOV-005, decisions made 2026-04-22"

git push origin main
```

Push 차단 시 대표님 옵션 A 요청.

### Step 10: 보고서

파일: `docs/07_relay/done/REPORT-WO-GOV-005__claudecode__done.md`

내용: 변경된 파일 목록 + 각 Patch의 삽입 위치 확인 + 커밋 SHA + push 결과.

워크오더 파일 이동:
```powershell
git mv docs/07_relay/queue/WO-GOV-005__claudecode__queue.md docs/07_relay/done/WO-GOV-005__claudecode__done.md
```

---

## 3. 제약 사항

- **본문을 비전이 명시한 문구와 한 글자도 다르게 삽입 금지** (번역·축약·의역 금지)
- **기존 헌법 본문 중 명시 삭제 대상 외 다른 내용 보존**
- **core:common 소스 변경 절대 금지** (FREEZE)
- 임의 오탈자 수정도 금지 (다른 워크오더로)

---

## 4. 실패 시 대응

| 지점 | 대응 |
|---|---|
| Step 2~7: 삽입 위치 식별 불가 | 즉시 중단, 해당 파일 현재 본문 200라인 보고 |
| Step 9: staging에 타 파일 섞임 | `git reset HEAD` 후 보고 |
| Step 9: push 거부 | 즉시 보고, pull --rebase 금지 |

---

## 끝
