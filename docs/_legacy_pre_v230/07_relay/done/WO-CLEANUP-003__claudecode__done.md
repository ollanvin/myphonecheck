# 워크오더: Scripts 폴더 정리 (WO-CLEANUP-003)

**대상 도구**: Claude Code v2.1.117 (Windows 로컬)
**기동 모드**: Auto Mode 전제
**작업 위치**: `C:\Users\user\Scripts\` (리포 외부)
**발행자**: 비전
**발행일**: 2026-04-22

---

## 0. 전제 조건

- [ ] WO-GOV-005 완료 (헌법 패치 v1.7 적용)
- [ ] `C:\Users\user\Scripts\` 폴더 존재
- [ ] `C:\Users\user\ollanvin\scripts\` 폴더 존재 (이동 대상지)

---

## 1. 작업 목적

`C:\Users\user\Scripts\` 폴더에 누적된 잡동사니 29개 항목을 3그룹으로 분류해 정리:

- Group A (운영 스크립트) → `C:\Users\user\ollanvin\scripts\`로 이동
- Group B (MyPhoneCheck 문서) → 리포 내 `docs/06_history/archive/2026-04-imports/`로 이동 후 git 추가
- Group C (임시·실험 파일) → 삭제

---

## 2. 그룹 분류 기준 + 파일별 처리

### Group A — 운영 스크립트 (이동처: `C:\Users\user\ollanvin\scripts\`)

다음 파일들이 대상:
- `DevSnapshotBackup.ps1`
- `DevSnapshotBackup_BigTech.ps1`
- `DevSnapshotBackup_BigTech.ps1.bak`
- `register_backup_scheduler.ps1`
- `run_build_install.ps1`
- `run_role_test.ps1`
- `run_test.ps1`
- `test_incremental_backup.ps1`
- `backup_launcher.vbs`
- `logs/` (하위 폴더 전체)
- `MyPhoneCheckAgent/` (하위 폴더 전체)

### Group B — MyPhoneCheck 문서 (이동처: 리포 내 `docs/06_history/archive/2026-04-imports/`)

다음 파일들이 대상:
- `ARCHITECTURE-ALL.zip`
- `ARCHITECTURE-cardspend.md`
- `ARCHITECTURE-constitution.md`
- `ARCHITECTURE-executing-OS.md`
- `ARCHITECTURE-myphonecheck.md`
- `MyPhoneCheck_Architecture_v1.4_disc.docx`
- `MyPhoneCheck_Architecture_v1.5.1_ac9e0c.docx`
- `MyPhoneCheck_Architecture_v1.5.2_0e40dd.docx`
- `MyPhoneCheck_Patches_v1.5.2-patch_bc64*.docx`
- `MyPhoneCheck_Structure_Analysis_v4.docx`
- `MyPhoneCheck_Work_Order_v4.3.docx`
- `synthesis_2026-04-22_pre-stage1.md` ← 방금 코웍이 생성한 종합 보고서

### Group C — 임시·실험 파일 (삭제 대상)

다음 파일들:
- `build_v4.3.txt` (94KB, 빌드 로그로 추정)
- `mpc_test.txt` (0KB)
- `mpc_test2.txt` (0KB)

### 기타 — 분류 애매한 파일

스크린샷에서 확인 안 된 파일이 있을 수 있음. 실제 `Get-ChildItem`으로 나온 목록과 대조해서 위 3그룹에 없는 파일이 있으면 **Group D (보류)**로 분류하여 보고서에 별도 기재. 임의 판단하지 말 것.

---

## 3. 작업 절차

### Step 1: 현재 상태 snapshot

```powershell
cd C:\Users\user\Scripts
Get-ChildItem -Force | Select-Object Name, Length, LastWriteTime | Format-Table | Out-File C:\Users\user\Dev\ollanvin\myphonecheck\docs\07_relay\done\scripts_folder_snapshot_20260422.txt
```

### Step 2: Group B 이동 및 리포 커밋

```powershell
$target = "C:\Users\user\Dev\ollanvin\myphonecheck\docs\06_history\archive\2026-04-imports"
New-Item -ItemType Directory -Path $target -Force | Out-Null

# 각 파일 이동
$groupB = @(
    "C:\Users\user\Scripts\ARCHITECTURE-ALL.zip",
    "C:\Users\user\Scripts\ARCHITECTURE-cardspend.md",
    "C:\Users\user\Scripts\ARCHITECTURE-constitution.md",
    "C:\Users\user\Scripts\ARCHITECTURE-executing-OS.md",
    "C:\Users\user\Scripts\ARCHITECTURE-myphonecheck.md",
    "C:\Users\user\Scripts\MyPhoneCheck_Architecture_v1.4_disc.docx",
    "C:\Users\user\Scripts\MyPhoneCheck_Architecture_v1.5.1_ac9e0c.docx",
    "C:\Users\user\Scripts\MyPhoneCheck_Architecture_v1.5.2_0e40dd.docx",
    "C:\Users\user\Scripts\MyPhoneCheck_Structure_Analysis_v4.docx",
    "C:\Users\user\Scripts\MyPhoneCheck_Work_Order_v4.3.docx",
    "C:\Users\user\Scripts\synthesis_2026-04-22_pre-stage1.md"
)

# Patches 파일은 이름에 hash 포함되므로 와일드카드
$patchesFile = Get-ChildItem "C:\Users\user\Scripts\MyPhoneCheck_Patches_v1.5.2-patch_*.docx" | Select-Object -First 1
if ($patchesFile) { $groupB += $patchesFile.FullName }

foreach ($src in $groupB) {
    if (Test-Path $src) {
        Move-Item -Path $src -Destination $target -Force
    }
}

# git 추가 후 커밋 (별도 커밋 — Scripts 이동 이력을 독립적으로 남김)
cd C:\Users\user\Dev\ollanvin\myphonecheck
git add docs/06_history/archive/2026-04-imports/
git commit -m "chore(archive): import historical architecture docs from C:\Users\user\Scripts\

- Include v1.4/v1.5.1/v1.5.2 architecture docs
- Include synthesis_2026-04-22_pre-stage1.md (cowork report)
- Include legacy ARCHITECTURE-*.md constitution drafts
- These were scattered in C:\Users\user\Scripts\; now git-tracked for audit trail

Refs: WO-CLEANUP-003"

git push origin main
```

Push 차단 시 대표님 옵션 A.

### Step 3: Group A 이동 (리포 외부, 개인 도구 영역)

```powershell
$srcBase = "C:\Users\user\Scripts"
$dstBase = "C:\Users\user\ollanvin\scripts"

# 루트 레벨 스크립트 파일들
$groupAFiles = @(
    "DevSnapshotBackup.ps1",
    "DevSnapshotBackup_BigTech.ps1",
    "DevSnapshotBackup_BigTech.ps1.bak",
    "register_backup_scheduler.ps1",
    "run_build_install.ps1",
    "run_role_test.ps1",
    "run_test.ps1",
    "test_incremental_backup.ps1",
    "backup_launcher.vbs"
)

foreach ($f in $groupAFiles) {
    $src = Join-Path $srcBase $f
    if (Test-Path $src) {
        Move-Item -Path $src -Destination $dstBase -Force
    }
}

# 폴더 단위 이동
if (Test-Path "$srcBase\logs") {
    Move-Item -Path "$srcBase\logs" -Destination $dstBase -Force
}
if (Test-Path "$srcBase\MyPhoneCheckAgent") {
    Move-Item -Path "$srcBase\MyPhoneCheckAgent" -Destination $dstBase -Force
}
```

### Step 4: Group C 삭제 (임시 파일)

```powershell
$groupC = @(
    "C:\Users\user\Scripts\build_v4.3.txt",
    "C:\Users\user\Scripts\mpc_test.txt",
    "C:\Users\user\Scripts\mpc_test2.txt"
)

foreach ($f in $groupC) {
    if (Test-Path $f) {
        Remove-Item -Path $f -Force
    }
}
```

### Step 5: Group D (보류) 식별

```powershell
$remaining = Get-ChildItem "C:\Users\user\Scripts" -Force
```

`$remaining`에 남은 파일이 있으면 **보고서에 그대로 기록**, 임의 처리 금지.

### Step 6: Scripts 폴더 최종 상태

```powershell
$finalCount = (Get-ChildItem "C:\Users\user\Scripts" -Force).Count
Write-Host "Scripts 폴더 최종 항목 수: $finalCount"

# 예상: 0건. 남아있으면 Group D로 분류된 것.
```

**폴더 자체 삭제 금지**. Windows가 다른 용도로 참조할 수 있음.

---

## 4. 보고서

파일: `docs/07_relay/done/REPORT-WO-CLEANUP-003__claudecode__done.md`

포함:
- Step 1의 snapshot 내용
- 각 Group별 실제 이동/삭제한 파일 목록
- Group D 보류 파일 목록 (있다면)
- Group B 관련 커밋 SHA
- 최종 Scripts 폴더 상태

---

## 5. 제약 사항

- `C:\Users\user\Scripts\` 폴더 자체 삭제 금지
- Group 분류 애매한 파일은 **임의 처리 없이 Group D로 보고**
- `myphonecheck-backup-pre-filter-20260422` 백업 폴더 건드리지 말 것 (다른 경로지만 혹시 혼동 방지)
- Group B 커밋과 WO-GOV-005 커밋 분리 유지

---

## 6. 실패 시 대응

| 지점 | 대응 |
|---|---|
| 이동처 폴더 부재 | 폴더 먼저 생성 |
| Move 실패 (파일 점유) | 해당 파일 건너뛰고 보고서에 기재 |
| git push 차단 | 대표님 옵션 A 요청 |
| Group 분류 애매 | Group D로 분류, 임의 처리 금지 |

---

## 끝
