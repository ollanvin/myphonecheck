<#
.SYNOPSIS
    CallCheck Live Validation — 대표님 로컬 PC 전용

.DESCRIPTION
    실전 번호 검증셋을 실제 네트워크로 검증하고,
    성공한 HTML 스냅샷을 Frozen fixture로 자동 저장한다.

    ┌─────────────────────────────────────────────────────┐
    │ 실행 환경: 대표님 로컬 PC (Windows)                  │
    │ 역할: Live Validation (2층)                          │
    │ VM에서 실행 금지 — VM은 Frozen/정적/빌드만 담당       │
    └─────────────────────────────────────────────────────┘

.NOTES
    사전 조건:
    - JDK 17+ 설치 (JAVA_HOME 설정)
    - Android SDK 설치 (ANDROID_HOME 설정)
    - Gradle 8.6+ (wrapper 또는 standalone)
    - 인터넷 연결 필수

.EXAMPLE
    .\run_live_validation.ps1
    .\run_live_validation.ps1 -PhoneNumbers "15881234","1345"
    .\run_live_validation.ps1 -SaveFixtures
#>

param(
    [string[]]$PhoneNumbers = @(),
    [switch]$SaveFixtures,
    [switch]$Verbose
)

$ErrorActionPreference = "Stop"

# ═══════════════════════════════════════════════════════
# 설정
# ═══════════════════════════════════════════════════════

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Resolve-Path "$ScriptDir\..\.."
$FixtureDir = "$ProjectRoot\data\search\src\test\resources\fixtures\ddg-snapshots"
$LiveResultDir = "$ProjectRoot\scripts\local-test-rig\live-results"
$Timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$LogFile = "$LiveResultDir\live-validation-$Timestamp.log"

# 기본 검증 번호 셋 (자비스 승인 완료 목록)
$DefaultPhoneNumbers = @(
    @{ Phone = "1345";        Category = "kr-institution";   Label = "정부민원안내콜센터" },
    @{ Phone = "15881234";    Category = "kr-business";      Label = "신세계백화점" },
    @{ Phone = "15881255";    Category = "kr-delivery";      Label = "CJ대한통운" },
    @{ Phone = "0288881234";  Category = "kr-scam";          Label = "보이스피싱 대역" },
    @{ Phone = "0120444113";  Category = "jp-institution";   Label = "NTT 고장접수" },
    @{ Phone = "8008290433";  Category = "us-scam";          Label = "US 스캠" },
    @{ Phone = "15889999";    Category = "kr-mixed";         Label = "현대홈쇼핑" },
    @{ Phone = "15771000";    Category = "kr-medical";       Label = "삼성서울병원" },
    @{ Phone = "15881599";    Category = "kr-bank";          Label = "KB국민은행" },
    @{ Phone = "114";         Category = "kr-telecom";       Label = "KT 번호안내" }
)

# ═══════════════════════════════════════════════════════
# 디렉토리 준비
# ═══════════════════════════════════════════════════════

if (-not (Test-Path $LiveResultDir)) {
    New-Item -ItemType Directory -Path $LiveResultDir -Force | Out-Null
}
if (-not (Test-Path $FixtureDir)) {
    New-Item -ItemType Directory -Path $FixtureDir -Force | Out-Null
}

# ═══════════════════════════════════════════════════════
# 함수: DuckDuckGo HTML 스크래핑
# ═══════════════════════════════════════════════════════

function Invoke-DdgScrape {
    param([string]$Phone)

    $encodedPhone = [System.Web.HttpUtility]::UrlEncode($Phone)
    $url = "https://html.duckduckgo.com/html/?q=$encodedPhone"

    try {
        $headers = @{
            "User-Agent"      = "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
            "Accept-Language"  = "ko-KR,ko;q=0.9,en;q=0.8"
        }

        $response = Invoke-WebRequest -Uri $url -Headers $headers -TimeoutSec 15 -UseBasicParsing

        if ($response.StatusCode -eq 200) {
            return @{
                Success = $true
                Html    = $response.Content
                Size    = $response.Content.Length
            }
        } else {
            return @{ Success = $false; Error = "HTTP $($response.StatusCode)" }
        }
    } catch {
        return @{ Success = $false; Error = $_.Exception.Message }
    }
}

# ═══════════════════════════════════════════════════════
# 함수: HTML에서 결과 건수 파싱
# ═══════════════════════════════════════════════════════

function Get-ResultCount {
    param([string]$Html)

    $pattern = 'class="result__a"'
    $matches = [regex]::Matches($Html, $pattern)
    return $matches.Count
}

# ═══════════════════════════════════════════════════════
# 함수: Fixture로 저장
# ═══════════════════════════════════════════════════════

function Save-AsFixture {
    param(
        [string]$Html,
        [string]$Category,
        [string]$Phone
    )

    $fixtureName = "$Category-$Phone.html"
    $fixturePath = Join-Path $FixtureDir $fixtureName

    # 기존 fixture 백업
    if (Test-Path $fixturePath) {
        $backupName = "$Category-$Phone.backup-$Timestamp.html"
        $backupPath = Join-Path $LiveResultDir $backupName
        Copy-Item $fixturePath $backupPath
        Write-Host "  [BACKUP] 기존 fixture 백업: $backupName" -ForegroundColor DarkGray
    }

    Set-Content -Path $fixturePath -Value $Html -Encoding UTF8
    Write-Host "  [FROZEN] fixture 저장 완료: $fixtureName" -ForegroundColor Green
}

# ═══════════════════════════════════════════════════════
# 메인 실행
# ═══════════════════════════════════════════════════════

Add-Type -AssemblyName System.Web

Write-Host ""
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host " CallCheck Live Validation — 로컬 PC 전용" -ForegroundColor Cyan
Write-Host " 실행 시각: $Timestamp" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# 검증 대상 결정
$targets = @()
if ($PhoneNumbers.Count -gt 0) {
    foreach ($p in $PhoneNumbers) {
        $found = $DefaultPhoneNumbers | Where-Object { $_.Phone -eq $p }
        if ($found) {
            $targets += $found
        } else {
            $targets += @{ Phone = $p; Category = "custom"; Label = "사용자 지정" }
        }
    }
} else {
    $targets = $DefaultPhoneNumbers
}

Write-Host "검증 대상: $($targets.Count)개 번호" -ForegroundColor White
Write-Host ""

# 결과 집계
$successCount = 0
$failCount = 0
$skipCount = 0
$results = @()

foreach ($target in $targets) {
    $phone = $target.Phone
    $category = $target.Category
    $label = $target.Label

    Write-Host "[$($targets.IndexOf($target) + 1)/$($targets.Count)] $phone ($label)" -NoNewline

    $result = Invoke-DdgScrape -Phone $phone

    if ($result.Success) {
        $count = Get-ResultCount -Html $result.Html

        if ($count -gt 0) {
            Write-Host " ✅ $count건 ($([math]::Round($result.Size / 1024, 1))KB)" -ForegroundColor Green
            $successCount++

            # Live HTML 원본 저장 (항상)
            $livePath = Join-Path $LiveResultDir "live-$category-$phone-$Timestamp.html"
            Set-Content -Path $livePath -Value $result.Html -Encoding UTF8

            # -SaveFixtures 옵션 시 Frozen fixture로 자동 편입
            if ($SaveFixtures) {
                Save-AsFixture -Html $result.Html -Category $category -Phone $phone
            }

            $results += @{
                Phone    = $phone
                Label    = $label
                Status   = "SUCCESS"
                Count    = $count
                Size     = $result.Size
            }
        } else {
            Write-Host " ⚠️  HTML 수신했으나 파싱 결과 0건 ($([math]::Round($result.Size / 1024, 1))KB)" -ForegroundColor Yellow
            $skipCount++

            # 파싱 실패 HTML도 저장 (디버깅용)
            $debugPath = Join-Path $LiveResultDir "debug-$category-$phone-$Timestamp.html"
            Set-Content -Path $debugPath -Value $result.Html -Encoding UTF8

            $results += @{
                Phone    = $phone
                Label    = $label
                Status   = "PARSE_FAIL"
                Count    = 0
                Size     = $result.Size
            }
        }
    } else {
        Write-Host " ❌ $($result.Error)" -ForegroundColor Red
        $failCount++

        $results += @{
            Phone    = $phone
            Label    = $label
            Status   = "NETWORK_FAIL"
            Count    = 0
            Size     = 0
        }
    }
}

# ═══════════════════════════════════════════════════════
# 결과 요약
# ═══════════════════════════════════════════════════════

Write-Host ""
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host " Live Validation 결과 요약" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host " 성공: $successCount  실패: $failCount  파싱실패: $skipCount  합계: $($targets.Count)" -ForegroundColor White

if ($SaveFixtures -and $successCount -gt 0) {
    Write-Host " Frozen fixture 업데이트: ${successCount}건" -ForegroundColor Green
}

Write-Host ""

# 로그 파일 저장
$logContent = @"
═══════════════════════════════════════════════════
CallCheck Live Validation Log
실행 시각: $Timestamp
═══════════════════════════════════════════════════
성공: $successCount / 실패: $failCount / 파싱실패: $skipCount / 합계: $($targets.Count)
SaveFixtures: $SaveFixtures

── 상세 결과 ──
"@

foreach ($r in $results) {
    $logContent += "`n$($r.Phone) ($($r.Label)): $($r.Status) — $($r.Count)건, $($r.Size)bytes"
}

Set-Content -Path $LogFile -Value $logContent -Encoding UTF8
Write-Host "로그 저장: $LogFile" -ForegroundColor DarkGray

# ═══════════════════════════════════════════════════════
# Gradle 테스트 실행 (선택)
# ═══════════════════════════════════════════════════════

Write-Host ""
Write-Host "── 다음 단계 ──" -ForegroundColor Yellow
Write-Host "1) Live 결과 확인 후 Frozen fixture 갱신:" -ForegroundColor White
Write-Host "   .\run_live_validation.ps1 -SaveFixtures" -ForegroundColor Gray
Write-Host ""
Write-Host "2) Gradle 테스트 실행 (Frozen + 정적):" -ForegroundColor White
Write-Host "   .\run_frozen_tests.ps1" -ForegroundColor Gray
Write-Host ""
Write-Host "3) 에뮬레이터 통합 테스트:" -ForegroundColor White
Write-Host "   .\run_emulator_callcheck.ps1" -ForegroundColor Gray

exit $(if ($failCount -eq 0) { 0 } else { 1 })
