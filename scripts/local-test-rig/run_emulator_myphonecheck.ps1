<#
.SYNOPSIS
    CallCheck 에뮬레이터 통합 테스트 — 대표님 로컬 PC 전용

.DESCRIPTION
    Android 에뮬레이터에서 CallCheck APK를 설치하고
    실제 전화 수신 시뮬레이션으로 UI/Notification/Role을 검증한다.

    ┌─────────────────────────────────────────────────────┐
    │ 실행 환경: 대표님 로컬 PC (Windows)                  │
    │ 역할: 디바이스 통합 검증 (2층)                        │
    │ 사전 조건:                                           │
    │  - Android Emulator 실행 중                          │
    │  - ADB 접근 가능                                     │
    │  - APK 빌드 완료                                     │
    └─────────────────────────────────────────────────────┘

.PARAMETER ApkPath
    설치할 APK 경로. 미지정 시 기본 debug APK 위치 사용.

.PARAMETER TestNumbers
    테스트할 전화번호 목록. 미지정 시 기본 셋 사용.

.EXAMPLE
    .\run_emulator_callcheck.ps1
    .\run_emulator_callcheck.ps1 -ApkPath ".\app\build\outputs\apk\debug\app-debug.apk"
#>

param(
    [string]$ApkPath = "",
    [string[]]$TestNumbers = @()
)

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Resolve-Path "$ScriptDir\..\.."
$Timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$ResultDir = "$ScriptDir\emulator-results\$Timestamp"

# 기본 APK 경로
if (-not $ApkPath) {
    $ApkPath = "$ProjectRoot\app\build\outputs\apk\debug\app-debug.apk"
}

# 기본 테스트 번호
if ($TestNumbers.Count -eq 0) {
    $TestNumbers = @(
        "1345",         # 기관
        "15881234",     # 기업 (신세계백화점)
        "15881255",     # 택배 (CJ대한통운)
        "0288881234",   # 사기
        "15881599"      # 은행 (KB국민은행)
    )
}

# ═══════════════════════════════════════════════════════
# 사전 검증
# ═══════════════════════════════════════════════════════

Write-Host ""
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host " CallCheck Emulator Integration Test" -ForegroundColor Cyan
Write-Host " $Timestamp" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# ADB 확인
$adb = "adb"
try {
    $devices = & $adb devices 2>&1
    $connected = ($devices | Select-String "device$").Count
    if ($connected -eq 0) {
        Write-Host "❌ ADB에 연결된 디바이스/에뮬레이터가 없습니다." -ForegroundColor Red
        Write-Host "   에뮬레이터를 먼저 실행해 주세요." -ForegroundColor Yellow
        exit 1
    }
    Write-Host "✅ ADB 연결 확인: ${connected}대" -ForegroundColor Green
} catch {
    Write-Host "❌ ADB를 찾을 수 없습니다. ANDROID_HOME/platform-tools를 PATH에 추가하세요." -ForegroundColor Red
    exit 1
}

# APK 확인
if (-not (Test-Path $ApkPath)) {
    Write-Host "❌ APK 파일 없음: $ApkPath" -ForegroundColor Red
    Write-Host "   먼저 빌드하세요: gradlew assembleDebug" -ForegroundColor Yellow
    exit 1
}
Write-Host "✅ APK 확인: $ApkPath" -ForegroundColor Green

# 결과 디렉토리
New-Item -ItemType Directory -Path $ResultDir -Force | Out-Null

# ═══════════════════════════════════════════════════════
# Step 1: APK 설치
# ═══════════════════════════════════════════════════════

Write-Host ""
Write-Host "── Step 1: APK 설치 ──" -ForegroundColor Yellow

& $adb install -r -t $ApkPath 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ APK 설치 실패" -ForegroundColor Red
    exit 1
}
Write-Host "✅ APK 설치 완료" -ForegroundColor Green

# ═══════════════════════════════════════════════════════
# Step 2: CallScreening Role 등록 확인
# ═══════════════════════════════════════════════════════

Write-Host ""
Write-Host "── Step 2: Role 확인 ──" -ForegroundColor Yellow

$packageName = "app.callcheck.mobile"
$roleCheck = & $adb shell "dumpsys role" 2>&1 | Select-String $packageName
if ($roleCheck) {
    Write-Host "✅ CallScreening Role 등록 확인" -ForegroundColor Green
} else {
    Write-Host "⚠️  CallScreening Role 미등록 — 앱을 열고 수동 등록 필요" -ForegroundColor Yellow
    Write-Host "   앱 실행 중..." -ForegroundColor Gray
    & $adb shell am start -n "$packageName/.feature.main.MainActivity" 2>&1
    Write-Host "   Role 등록 후 Enter 키를 누르세요..." -ForegroundColor Yellow
    Read-Host
}

# ═══════════════════════════════════════════════════════
# Step 3: 전화 수신 시뮬레이션
# ═══════════════════════════════════════════════════════

Write-Host ""
Write-Host "── Step 3: 전화 수신 시뮬레이션 ──" -ForegroundColor Yellow

foreach ($number in $TestNumbers) {
    Write-Host ""
    Write-Host "  📞 테스트: $number" -ForegroundColor White

    # 수신 전화 시뮬레이션
    & $adb shell "am broadcast -a android.intent.action.PHONE_STATE --es state RINGING --es incoming_number $number" 2>&1

    # 대기 (검색 + 분석 시간)
    Write-Host "     3초 대기 (검색 분석 중)..." -ForegroundColor Gray
    Start-Sleep -Seconds 3

    # 스크린샷 저장
    $screenshotName = "screenshot-$number.png"
    $devicePath = "/sdcard/$screenshotName"
    $localPath = Join-Path $ResultDir $screenshotName

    & $adb shell screencap $devicePath 2>&1
    & $adb pull $devicePath $localPath 2>&1
    & $adb shell rm $devicePath 2>&1

    if (Test-Path $localPath) {
        Write-Host "     ✅ 스크린샷 저장: $screenshotName" -ForegroundColor Green
    }

    # 로그 저장
    $logName = "logcat-$number.txt"
    $logPath = Join-Path $ResultDir $logName
    & $adb logcat -d -s "CallCheck" 2>&1 | Set-Content $logPath -Encoding UTF8
    Write-Host "     ✅ 로그 저장: $logName" -ForegroundColor Green

    # 전화 종료
    & $adb shell "am broadcast -a android.intent.action.PHONE_STATE --es state IDLE" 2>&1
    Start-Sleep -Seconds 1
}

# ═══════════════════════════════════════════════════════
# Step 4: Notification 확인
# ═══════════════════════════════════════════════════════

Write-Host ""
Write-Host "── Step 4: Notification 상태 ──" -ForegroundColor Yellow

$notifications = & $adb shell "dumpsys notification --noredact" 2>&1 | Select-String $packageName
if ($notifications) {
    $notifPath = Join-Path $ResultDir "notifications.txt"
    $notifications | Set-Content $notifPath -Encoding UTF8
    Write-Host "✅ Notification 발견 — notifications.txt 저장 완료" -ForegroundColor Green
} else {
    Write-Host "⚠️  Notification 미발견" -ForegroundColor Yellow
}

# ═══════════════════════════════════════════════════════
# 결과 요약
# ═══════════════════════════════════════════════════════

Write-Host ""
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host " Emulator Test 완료" -ForegroundColor Cyan
Write-Host " 결과 디렉토리: $ResultDir" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# 결과 파일 목록
Get-ChildItem $ResultDir | ForEach-Object {
    Write-Host "  $($_.Name) ($([math]::Round($_.Length / 1024, 1))KB)" -ForegroundColor Gray
}
