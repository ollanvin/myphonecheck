# 릴레이 프로토콜 v1 — 도구 간 자동 전달 체계

**문서 ID**: relay_protocol_v1
**작성일**: 2026-04-22
**작성자**: 비전 (Claude Opus 4.7)
**목적**: 대표님을 도구 간 메신저 역할에서 해방. 휴먼은 승인 게이트만.
**저장 위치**: `docs/04_operations/relay_protocol_v1.md`

---

## 1. 문제 정의

현재 워크플로: 대표님 → 비전 → 대표님 → Cursor → 대표님 → 비전 → 대표님 → Claude Code → 대표님 → 비전...

대표님이 도구 간 복사·붙여넣기 메신저 노릇 중. 빅테크 아님.

---

## 2. 해결 원칙

**모든 도구는 `git`을 메시지 버스로 쓴다.**

- 워크오더 발행 = 파일 커밋 + push
- 워크오더 실행 = 파일 read + 작업 + 보고 파일 생성 + 커밋 + push
- 다음 도구는 `git pull` 후 자기 할 일 찾음
- 대표님은 승인 필요 지점에서만 개입

---

## 3. 폴더 구조

```
docs/
├── 07_relay/
│   ├── queue/           # 발행되었지만 아직 실행 안 된 워크오더
│   ├── in_progress/     # 실행 중인 워크오더 (도구가 자기 것 찜)
│   ├── done/            # 완료된 워크오더 (보고서와 함께 보관)
│   ├── failed/          # 실패한 워크오더
│   └── needs_approval/  # 대표님 승인 필요한 항목
└── ...
```

### 3-1. 파일 명명 규칙

```
WO-{도메인}-{번호}__{도구}__{상태}.md

예시:
WO-STAGE1-001__cursor__queue.md          # Cursor용, 대기 중
WO-AUDIT-003__claudecode__in_progress.md # Claude Code 작업 중
WO-GOV-004__claudecode__done.md          # 완료
```

**도메인 코드**:
- `STAGE0`, `STAGE1`, `STAGE2`, ... — Stage별 작업
- `GOV` — 거버넌스/헌법
- `AUDIT` — 감사
- `CLEANUP` — 청소
- `RELAY` — 릴레이 시스템 자체

**도구 코드**:
- `cursor`, `claudecode`, `codex`, `cowork`

---

## 4. 워크오더 파일 포맷 (필수 섹션)

```markdown
# 워크오더: {제목} ({WO-ID})

**대상 도구**: {도구명}
**발행자**: 비전
**발행일**: YYYY-MM-DD
**상태**: queue | in_progress | done | failed | needs_approval
**선행 조건**: {다른 WO-ID 또는 없음}

## 0. 즉시 확인 (시작 전)
- [ ] 필수 전제 체크리스트

## 1. 작업 목적
(1~3문장)

## 2. 작업 절차
(step별 구체 명령)

## 3. 산출 보고
다음 위치에 보고서 파일 생성:
`docs/07_relay/done/REPORT-{WO-ID}__{도구}__done.md`

## 4. 제약 사항
(임의 판단 금지, 실패 시 중단 등)

## 5. 실패 시 대응
(실패 지점별 대응 매트릭스)
```

---

## 5. 도구별 루틴

### 5-1. 모든 도구 공통 시작 루틴

```bash
cd {리포 경로}
git pull origin main
ls docs/07_relay/queue/ | grep "__{자기 도구 이름}__"
```

자기 이름이 박힌 워크오더가 있으면 순서대로 하나 집어서:
1. 파일을 `queue/` → `in_progress/` 로 `git mv`
2. 파일명의 `__queue` → `__in_progress`로 변경
3. 커밋 + push (“claim this workorder” 목적)
4. 본문 실행
5. 보고서를 `done/` 폴더에 생성
6. 워크오더 파일을 `in_progress/` → `done/`로 `git mv`, 파일명도 변경
7. 커밋 + push

**실패 시**: `in_progress/` → `failed/`로 이동, 실패 사유 보고서 생성.

### 5-2. Cursor

- 자기 워크오더: `*__cursor__*`
- 구현 전용. 감사 작업 거부.

### 5-3. Claude Code

- 자기 워크오더: `*__claudecode__*`
- 감사, 정리, 거버넌스, 장기 주행.
- Auto Mode 전제.

### 5-4. Codex CLI

- 자기 워크오더: `*__codex__*`
- 크로스체크 전담.
- Claude Code와 동일 작업 금지 (대칭 유지).

### 5-5. 코웍 (Linux 샌드박스)

- 자기 워크오더: `*__cowork__*`
- JVM·문서 전용, Android/Windows 스택 제외.
- 리포를 /tmp에 clone 후 Read-Only 분석. 커밋 금지 (대신 보고서 본문을 채팅으로 반환).

### 5-6. 비전 채팅

- 워크오더 **발행자**, 상태 확인자, 판정자.
- `done/` 폴더의 보고서를 정독하고 다음 워크오더 발행.

---

## 6. 승인 게이트 (대표님 개입 지점)

자동화하면 안 되는 항목 (전통적 빅테크 관행):

| 게이트 | 이유 |
|---|---|
| `git push origin main` 파괴적 명령 | Claude Code classifier가 차단, 대표님 직접 실행 |
| 브랜치 삭제 | 동일 |
| 헌법 본문 결정 | 사람 책임 영역 |
| 가격·정책 같은 상용화 결정 | 사람 책임 영역 |
| Play Console 제출 | 사람 책임 영역 |

그 외는 도구들이 알아서.

---

## 7. 충돌 방지

두 도구가 같은 워크오더를 동시에 잡는 것 방지:

- `queue/` → `in_progress/` 이동 시 **파일명 변경 + 커밋 + push까지 한 번의 원자 작업**
- 다른 도구가 push 시도할 때 conflict 발생하면 → 포기 후 다른 워크오더 찾기
- 이것은 빅테크의 "optimistic concurrency" 패턴

---

## 8. 대표님 사용법

앞으로:

1. **새 작업 요청 시**: "비전, X 해줘" 한 마디
2. **비전이**: 워크오더 파일 만들어서 `docs/07_relay/queue/`에 push
3. **대표님**: `git push origin main` 실행 (승인 게이트)
4. **도구들**: 알아서 pull 받고 자기 것 처리
5. **비전**: 주기적으로 `done/` 폴더 확인하고 다음 단계 발행
6. **대표님**: 비전이 "승인 필요"라고 할 때만 개입

복사·붙여넣기 노동 제거. 대표님은 방향·전략·승인만.

---

## 9. 본 프로토콜 시행 전제

- 모든 도구가 리포에 git push 권한 보유 (현재 확보됨)
- Claude Code classifier 파괴적 명령 차단은 유지 (대표님 승인 게이트로 활용)
- Cowork는 push 권한 없음 (Read-Only, 보고서 본문만 반환)

---

## 10. 릴레이 시스템 부트스트랩

본 프로토콜이 실효성 있으려면 **폴더 구조 생성 + 첫 워크오더 세트 발행이 선행**되어야 한다. 다음 워크오더가 부트스트랩:

- `WO-RELAY-001__claudecode__queue.md` — 폴더 구조 생성
- `WO-GOV-005__claudecode__queue.md` — 헌법 패치 v1.7
- `WO-STAGE1-001__cursor__queue.md` — 푸시 휴지통
- `WO-CLEANUP-003__claudecode__queue.md` — Scripts 폴더 정리
- `WO-AUDIT-001__codex__queue.md` — WO-CLEANUP-002 크로스체크

비전이 이 5개를 이번 세션에 일괄 발행한다.

---

## 끝
