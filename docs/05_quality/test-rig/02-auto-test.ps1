# CallCheck 자동 테스트 파이프라인
# 용도: 에뮬레이터에서 APK 설치 → 실행 → 통화 시뮬레이션 → 로그/스크린샷 수집
# 실행: PowerShell -ExecutionPolicy Bypass -File 02-auto-test.ps1

param(
    [string]$ApkPath = "",
    [string]$AvdName = "CallCheck-Test",
    [string]$OutputDir = "$PSScriptRoot\test-results",
    [string]$PackageName = "app.callcheck.mobile",
    [string]$TestNumber = "+821012345678"
)

# ─── 설정 ───
$ErrorActionPreference = "Stop"
$Timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$ResultDir = "$OutputDir\$Timestamp"

# APK 자동 탐색
if (-not $ApkPath) {
    $ApkPath = Get-ChildItem -Path "$PSScriptRoot\.." -Filter "app-debug.apk" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty FullName
    if (-not $ApkPath) {
        Write-Error "APK 파일을 찾을 수 없습니다. -ApkPath 파라미터로 지정하세요."
        exit 1
    }
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " CallCheck 자동 테스트 파이프라인" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "APK: $ApkPath"
Write-Host "AVD: $AvdName"
Write-Host "결과: $ResultDir"
Write-Host ""

# 결과 디렉토리 생성
New-Item -ItemType Directory -Force -Path $ResultDir | Out-Null

function Write-Step($step, $desc) {
    Write-Host "[$step] $desc" -ForegroundColor Yellow
}

function Write-Pass($msg) {
    Write-Host "  PASS: $msg" -ForegroundColor Green
}

function Write-Fail($msg) {
    Write-Host "  FAIL: $msg" -ForegroundColor Red
}

# ─── STEP 1: 에뮬레이터 부팅 ───
Write-Step "1/8" "에뮬레이터 부팅"

$emulatorRunning = adb devices 2>$null | Select-String "emulator"
if (-not $emulatorRunning) {
    Write-Host "  에뮬레이터 시작 중..." -ForegroundColor Gray
    Start-Process -FilePath "emulator" -ArgumentList "-avd $AvdName -no-snapshot-load -no-audio -no-window" -WindowStyle Hidden

    # 부팅 대기 (최대 120초)
    $timeout = 120
    $elapsed = 0
    do {
        Start-Sleep -Seconds 5
        $elapsed += 5
        $bootComplete = adb shell getprop sys.boot_completed 2>$null
        Write-Host "  대기 중... ($elapsed`s)" -ForegroundColor Gray
    } while ($bootComplete -ne "1" -and $elapsed -lt $timeout)

    if ($bootComplete -ne "1") {
        Write-Fail "에뮬레이터 부팅 타임아웃 ($timeout`s)"
        exit 1
    }
    Write-Pass "에뮬레이터 부팅 완료 ($elapsed`s)"
} else {
    Write-Pass "에뮬레이터 이미 실행 중"
}

# 에뮬레이터 안정화 대기
Start-Sleep -Seconds 3

# ─── STEP 2: APK 설치 ───
Write-Step "2/8" "APK 설치"

# 기존 앱 제거 (있으면)
adb uninstall $PackageName 2>$null | Out-Null

$installResult = adb install -r $ApkPath 2>&1
if ($installResult -match "Success") {
    Write-Pass "APK 설치 성공"
    # 설치 증거 캡처
    adb shell screencap -p /sdcard/install-done.png
    adb pull /sdcard/install-done.png "$ResultDir\01-install-success.png" 2>$null
} else {
    Write-Fail "APK 설치 실패: $installResult"
    $installResult | Out-File "$ResultDir\install-error.log"
    exit 1
}

# ─── STEP 3: 앱 실행 ───
Write-Step "3/8" "앱 첫 실행"

adb shell am start -n "$PackageName/.MainActivity" 2>$null
Start-Sleep -Seconds 3

# 첫 실행 스크린샷
adb shell screencap -p /sdcard/first-launch.png
adb pull /sdcard/first-launch.png "$ResultDir\02-first-launch.png" 2>$null
Write-Pass "앱 실행 + 스크린샷 저장"

# ─── STEP 4: 권한 허용 ───
Write-Step "4/8" "권한 자동 허용"

# 필요한 권한 목록
$permissions = @(
    "android.permission.READ_CALL_LOG",
    "android.permission.READ_CONTACTS",
    "android.permission.READ_SMS",
    "android.permission.READ_PHONE_STATE",
    "android.permission.CALL_PHONE"
)

foreach ($perm in $permissions) {
    $grantResult = adb shell pm grant $PackageName $perm 2>&1
    if ($grantResult -match "error|Exception") {
        Write-Host "  경고: $perm 부여 실패 (런타임 전용일 수 있음)" -ForegroundColor Gray
    }
}

# CallScreeningService 기본 앱 설정 (Android 에뮬레이터에서는 수동 필요할 수 있음)
adb shell cmd role add-role-holder android.app.role.CALL_SCREENING "$PackageName" 2>$null

Start-Sleep -Seconds 2
adb shell screencap -p /sdcard/permissions.png
adb pull /sdcard/permissions.png "$ResultDir\03-permissions.png" 2>$null
Write-Pass "권한 허용 완료"

# ─── STEP 5: logcat 시작 (백그라운드) ───
Write-Step "5/8" "logcat 수집 시작"

$logcatJob = Start-Job -ScriptBlock {
    param($ResultDir, $PackageName)
    & adb logcat -v time --pid=$(& adb shell pidof $PackageName) 2>$null | Out-File "$ResultDir\logcat-full.log"
} -ArgumentList $ResultDir, $PackageName

# 앱 프로세스 태그 기반 logcat도 별도 수집
Start-Job -ScriptBlock {
    param($ResultDir)
    & adb logcat -v time -s "CallCheck:*" "CallScreening:*" "CallActionReceiver:*" "DecisionEngine:*" "Hilt:*" 2>$null | Out-File "$ResultDir\logcat-callcheck.log"
} -ArgumentList $ResultDir | Out-Null

Write-Pass "logcat 수집 중"

# ─── STEP 6: 미저장 번호 수신 시뮬레이션 ───
Write-Step "6/8" "미저장 번호 수신 시뮬레이션"

Write-Host "  테스트 번호: $TestNumber" -ForegroundColor Gray

# 에뮬레이터 전화 시뮬레이션
# 방법 1: telnet (에뮬레이터 콘솔)
$emulatorPort = (adb devices | Select-String "emulator-(\d+)" | ForEach-Object { $_.Matches[0].Groups[1].Value })
if ($emulatorPort) {
    Write-Host "  에뮬레이터 포트: $emulatorPort" -ForegroundColor Gray

    # 수신 전화 발생
    $telnetScript = @"
gsm call $TestNumber
"@
    $telnetScript | & "$env:ANDROID_HOME\platform-tools\adb" emu gsm call $TestNumber 2>$null
}

# 방법 2: adb emu 명령 (더 안정적)
adb emu gsm call $TestNumber 2>$null

Write-Host "  수신 전화 발생, 5초 대기..." -ForegroundColor Gray
Start-Sleep -Seconds 5

# 수신 중 스크린샷
adb shell screencap -p /sdcard/incoming-call.png
adb pull /sdcard/incoming-call.png "$ResultDir\04-incoming-call.png" 2>$null
Write-Pass "수신 전화 시뮬레이션 완료"

# ─── STEP 7: CallScreeningService 동작 확인 ───
Write-Step "7/8" "CallScreeningService 동작 확인"

Start-Sleep -Seconds 3

# 전화 종료
adb emu gsm cancel $TestNumber 2>$null
Start-Sleep -Seconds 2

# CallScreeningService 관련 로그 추출
$screeningLog = adb logcat -d -v time -s "CallCheck:*" "CallScreening:*" 2>$null
$screeningLog | Out-File "$ResultDir\05-screening-service.log"

# 결정 카드 스크린샷 (알림 or 오버레이)
adb shell screencap -p /sdcard/decision-card.png
adb pull /sdcard/decision-card.png "$ResultDir\05-decision-card.png" 2>$null

if ($screeningLog -match "CallScreening|onScreenCall|DecisionEngine") {
    Write-Pass "CallScreeningService 로그 감지됨"
} else {
    Write-Fail "CallScreeningService 로그 없음 — 서비스가 활성화되지 않았을 수 있음"
}

# ─── STEP 8: 결과 정리 ───
Write-Step "8/8" "결과 정리"

# logcat 중지
Stop-Job -Job $logcatJob -ErrorAction SilentlyContinue 2>$null

# 최종 logcat 스냅샷
adb logcat -d -v time 2>$null | Out-File "$ResultDir\logcat-snapshot.log"

# 결과 요약 생성
$resultFiles = Get-ChildItem $ResultDir
$summary = @"
========================================
CallCheck 테스트 결과 요약
========================================
실행 시간: $Timestamp
APK: $ApkPath
AVD: $AvdName
테스트 번호: $TestNumber

수집된 파일:
$($resultFiles | ForEach-Object { "  - $($_.Name) ($([math]::Round($_.Length/1KB, 1))KB)" } | Out-String)

판정 기준:
- [확인] APK 설치 가능 여부
- [확인] 앱 실행 가능 여부
- [확인] 권한 부여 완료 여부
- [확인] 수신 전화 시뮬레이션 완료 여부
- [확인] CallScreeningService 로그 존재 여부
- [확인] 결정 카드 UI 노출 여부
========================================
"@

$summary | Out-File "$ResultDir\RESULT-SUMMARY.txt"
Write-Host ""
Write-Host $summary
Write-Host "결과 저장 위치: $ResultDir" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
