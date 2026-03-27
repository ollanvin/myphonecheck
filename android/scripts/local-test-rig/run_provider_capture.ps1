<#
.SYNOPSIS
    Provider HTML Capture & Freeze — Live 성공 결과를 Frozen fixture로 편입

.DESCRIPTION
    1) 지정된 번호에 대해 DuckDuckGo HTML을 스크래핑한다.
    2) 파싱 결과가 유효하면(1건+) 성공으로 판정한다.
    3) 성공한 HTML을 Frozen fixture 디렉토리에 저장한다.
    4) 저장 시 기존 fixture를 백업(타임스탬프)한다.

    ┌─────────────────────────────────────────────────────┐
    │ Live 성공 → Frozen 편입 파이프라인                    │
    │                                                     │
    │ [실전 번호] → [DDG 스크래핑] → [파싱 검증]           │
    │                                    ↓                │
    │                            성공: fixture 저장        │
    │                            실패: debug HTML 저장     │
    │                                                     │
    │ 이후 VM에서 FrozenSnapshotValidationTest 실행하면    │
    │ 네트워크 없이도 회귀 검증 가능                        │
    └─────────────────────────────────────────────────────┘

.PARAMETER Phone
    캡처할 전화번호 (필수)

.PARAMETER Category
    카테고리 태그 (fixture 파일명에 사용)
    예: kr-institution, kr-business, kr-delivery, kr-scam 등

.PARAMETER Label
    사람 읽기용 설명 (로그 출력용)

.EXAMPLE
    .\run_provider_capture.ps1 -Phone "15881234" -Category "kr-business" -Label "신세계백화점"
    .\run_provider_capture.ps1 -Phone "0312345678" -Category "kr-custom" -Label "새 번호 테스트"
#>

param(
    [Parameter(Mandatory=$true)]
    [string]$Phone,

    [Parameter(Mandatory=$true)]
    [string]$Category,

    [string]$Label = ""
)

$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Web

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Resolve-Path "$ScriptDir\..\.."
$FixtureDir = "$ProjectRoot\data\search\src\test\resources\fixtures\ddg-snapshots"
$LiveResultDir = "$ScriptDir\live-results"
$Timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"

# 디렉토리 준비
foreach ($dir in @($FixtureDir, $LiveResultDir)) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
}

Write-Host ""
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host " Provider Capture & Freeze" -ForegroundColor Cyan
Write-Host " Phone: $Phone | Category: $Category | Label: $Label" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# ── Step 1: DuckDuckGo HTML 스크래핑 ──

$encodedPhone = [System.Web.HttpUtility]::UrlEncode($Phone)
$url = "https://html.duckduckgo.com/html/?q=$encodedPhone"

Write-Host "[1/4] DDG 스크래핑: $url" -ForegroundColor White

try {
    $headers = @{
        "User-Agent"      = "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
        "Accept-Language"  = "ko-KR,ko;q=0.9,en;q=0.8"
    }
    $response = Invoke-WebRequest -Uri $url -Headers $headers -TimeoutSec 15 -UseBasicParsing
    $html = $response.Content
    $htmlSize = $html.Length

    Write-Host "      ✅ HTTP 200, ${([math]::Round($htmlSize / 1024, 1))}KB 수신" -ForegroundColor Green
} catch {
    Write-Host "      ❌ 네트워크 실패: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# ── Step 2: 파싱 검증 ──

Write-Host "[2/4] 파싱 검증..." -ForegroundColor White

$resultCount = ([regex]::Matches($html, 'class="result__a"')).Count
$snippetCount = ([regex]::Matches($html, 'class="result__snippet"')).Count

if ($resultCount -gt 0) {
    Write-Host "      ✅ 결과: ${resultCount}건, 스니펫: ${snippetCount}건" -ForegroundColor Green
} else {
    Write-Host "      ❌ 파싱 결과 0건 — fixture 편입 불가" -ForegroundColor Red

    # 디버깅용 원본 저장
    $debugPath = Join-Path $LiveResultDir "debug-$Category-$Phone-$Timestamp.html"
    Set-Content -Path $debugPath -Value $html -Encoding UTF8
    Write-Host "      디버그 HTML 저장: $debugPath" -ForegroundColor DarkGray

    exit 1
}

# ── Step 3: Live 원본 저장 ──

Write-Host "[3/4] Live 원본 저장..." -ForegroundColor White

$livePath = Join-Path $LiveResultDir "live-$Category-$Phone-$Timestamp.html"
Set-Content -Path $livePath -Value $html -Encoding UTF8
Write-Host "      ✅ $livePath" -ForegroundColor Green

# ── Step 4: Frozen fixture 편입 ──

Write-Host "[4/4] Frozen fixture 편입..." -ForegroundColor White

$fixtureName = "$Category-$Phone.html"
$fixturePath = Join-Path $FixtureDir $fixtureName

# 기존 fixture 백업
if (Test-Path $fixturePath) {
    $backupName = "$Category-$Phone.backup-$Timestamp.html"
    $backupPath = Join-Path $LiveResultDir $backupName
    Copy-Item $fixturePath $backupPath
    Write-Host "      [BACKUP] 기존 fixture 백업: $backupName" -ForegroundColor DarkGray
}

Set-Content -Path $fixturePath -Value $html -Encoding UTF8

Write-Host "      ✅ Frozen fixture 저장: $fixtureName" -ForegroundColor Green

# ── 결과 ──

Write-Host ""
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host " Capture & Freeze 완료" -ForegroundColor Cyan
Write-Host " Phone: $Phone → $fixtureName" -ForegroundColor White
Write-Host " Results: ${resultCount}건 | Size: $([math]::Round($htmlSize / 1024, 1))KB" -ForegroundColor White
Write-Host ""
Write-Host " 다음 단계:" -ForegroundColor Yellow
Write-Host " 1) FrozenSnapshotValidationTest에 이 번호의 테스트 케이스 추가" -ForegroundColor Gray
Write-Host " 2) VM에서 빌드: gradle :data:search:testDebugUnitTest" -ForegroundColor Gray
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
