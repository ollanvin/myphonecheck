<#
.SYNOPSIS
    CallCheck Frozen/정적 테스트 — VM 역할 동일 (로컬에서도 실행 가능)

.DESCRIPTION
    VM이 담당하는 것과 동일한 테스트를 로컬 PC에서 실행한다.
    네트워크 의존 없이 항상 재현 가능한 테스트만 포함.

    ┌─────────────────────────────────────────────────────┐
    │ 포함 테스트:                                         │
    │  - CountrySearchRouterTest (라우팅)                  │
    │  - SearchFallbackTest (폴백 체인)                    │
    │  - SignalConflictResolutionTest (충돌 해소)           │
    │  - FrozenSnapshotValidationTest (고정 fixture)       │
    │  - EndToEndSearchIntegrationTest (통합)              │
    │                                                     │
    │ 제외 테스트:                                         │
    │  - RealWorldValidationSetTest (Live, 네트워크 필요)  │
    │  - NationalProviderEndToEndTest (Live, 네트워크 필요)│
    └─────────────────────────────────────────────────────┘

.EXAMPLE
    .\run_frozen_tests.ps1
#>

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Resolve-Path "$ScriptDir\..\.."

Write-Host ""
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host " CallCheck Frozen/정적 테스트 실행" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

Push-Location $ProjectRoot

try {
    # Gradle wrapper 또는 standalone
    $gradle = if (Test-Path ".\gradlew.bat") { ".\gradlew.bat" } else { "gradle" }

    Write-Host "Gradle 실행: $gradle" -ForegroundColor Gray
    Write-Host ""

    # VM 역할 테스트만 실행 (네트워크 의존 테스트 제외)
    & $gradle :data:search:testDebugUnitTest `
        --console=plain `
        -Dorg.gradle.jvmargs="-Xmx2048m" `
        2>&1 | Tee-Object -Variable testOutput

    $exitCode = $LASTEXITCODE

    Write-Host ""
    if ($exitCode -eq 0) {
        Write-Host "✅ Frozen/정적 테스트 전체 통과" -ForegroundColor Green
    } else {
        Write-Host "❌ 테스트 실패 발생 (exit code: $exitCode)" -ForegroundColor Red
    }

    exit $exitCode
} finally {
    Pop-Location
}
