@echo off
echo ========================================
echo  CallCheck 자동 테스트 실행
echo ========================================
echo.

PowerShell -ExecutionPolicy Bypass -File "%~dp002-auto-test.ps1" %*

echo.
echo 테스트 완료. 아무 키나 누르면 종료됩니다.
pause
