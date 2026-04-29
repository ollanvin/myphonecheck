@echo off
echo ========================================
echo  Project Git + GitHub + CI/CD Setup
echo ========================================
echo.

:: Get folder name as repo name
for %%I in (.) do set REPO_NAME=%%~nxI
echo Project: %REPO_NAME%
echo.

where git >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Git not installed. Install from https://git-scm.com
    pause
    exit /b 1
)
echo [OK] Git found

where gh >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] GitHub CLI not installed. Install from https://cli.github.com
    pause
    exit /b 1
)
echo [OK] GitHub CLI found
echo.

:: Check if already a git repo
if exist ".git" (
    echo [SKIP] Already a git repo
    goto :push_existing
)

echo [1/6] Creating .gitignore...
if not exist ".gitignore" (
    (
        echo # Build
        echo *.iml
        echo .gradle/
        echo /local.properties
        echo /.idea/
        echo .DS_Store
        echo /build/
        echo /app/build/
        echo *.apk
        echo *.aab
        echo *.jks
        echo *.keystore
        echo keystore.properties
        echo Thumbs.db
        echo desktop.ini
        echo .vscode/
        echo node_modules/
        echo __pycache__/
        echo *.pyc
        echo .env
        echo .env.local
    ) > .gitignore
    echo [OK] .gitignore created
) else (
    echo [SKIP] .gitignore already exists
)
echo.

echo [2/6] Git init...
git init -b main
echo.

echo [3/6] Git config...
git config user.email "founder@idolab.ai"
git config user.name "idolab"
echo [OK] Config done
echo.

echo [4/6] Git add + commit...
git add -A
git commit -m "feat: %REPO_NAME% initial commit"
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Commit failed
    pause
    exit /b 1
)
echo.

echo [5/6] GitHub auth check...
gh auth status >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo GitHub login required. Browser will open.
    gh auth login -w
    if %ERRORLEVEL% neq 0 (
        echo [ERROR] GitHub login failed
        pause
        exit /b 1
    )
)
echo [OK] GitHub auth OK
echo.

echo [6/6] Create GitHub repo + push...
gh repo create itsalldone-dev/%REPO_NAME% --private --source=. --remote=origin --push
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Repo create failed. Maybe already exists?
    echo Try: git remote add origin https://github.com/itsalldone-dev/%REPO_NAME%.git
    echo      git push -u origin main
    pause
    exit /b 1
)
goto :setup_ci

:push_existing
echo Pushing existing repo...
git add -A
for /f "tokens=1-3 delims=/ " %%a in ('date /t') do set D=%%a-%%b-%%c
git commit -m "update %D%"
git push origin main
goto :setup_ci

:setup_ci
echo.
echo [CI/CD] Setting up GitHub Actions...
if not exist ".github\workflows" mkdir ".github\workflows"

:: Detect project type and create appropriate workflow
if exist "gradlew.bat" (
    echo Detected: Android/Gradle project
    (
        echo name: Android CI
        echo.
        echo on:
        echo   push:
        echo     branches: [ main ]
        echo   pull_request:
        echo     branches: [ main ]
        echo.
        echo jobs:
        echo   build:
        echo     runs-on: ubuntu-latest
        echo     steps:
        echo       - name: Checkout
        echo         uses: actions/checkout@v4
        echo       - name: Set up JDK 17
        echo         uses: actions/setup-java@v4
        echo         with:
        echo           java-version: '17'
        echo           distribution: 'temurin'
        echo           cache: gradle
        echo       - name: Grant execute permission
        echo         run: chmod +x gradlew
        echo       - name: Build Debug APK
        echo         run: ./gradlew assembleDebug --no-daemon
        echo       - name: Run Unit Tests
        echo         run: ./gradlew testDebugUnitTest --no-daemon
        echo       - name: Upload APK
        echo         uses: actions/upload-artifact@v4
        echo         if: success^(^)
        echo         with:
        echo           name: app-debug
        echo           path: app/build/outputs/apk/debug/app-debug.apk
        echo           retention-days: 30
    ) > .github\workflows\ci.yml
) else if exist "package.json" (
    echo Detected: Node.js project
    (
        echo name: Node.js CI
        echo.
        echo on:
        echo   push:
        echo     branches: [ main ]
        echo   pull_request:
        echo     branches: [ main ]
        echo.
        echo jobs:
        echo   build:
        echo     runs-on: ubuntu-latest
        echo     steps:
        echo       - uses: actions/checkout@v4
        echo       - uses: actions/setup-node@v4
        echo         with:
        echo           node-version: '20'
        echo           cache: 'npm'
        echo       - run: npm ci
        echo       - run: npm test
        echo       - run: npm run build --if-present
    ) > .github\workflows\ci.yml
) else if exist "requirements.txt" (
    echo Detected: Python project
    (
        echo name: Python CI
        echo.
        echo on:
        echo   push:
        echo     branches: [ main ]
        echo   pull_request:
        echo     branches: [ main ]
        echo.
        echo jobs:
        echo   build:
        echo     runs-on: ubuntu-latest
        echo     steps:
        echo       - uses: actions/checkout@v4
        echo       - uses: actions/setup-python@v5
        echo         with:
        echo           python-version: '3.12'
        echo       - run: pip install -r requirements.txt
        echo       - run: python -m pytest --tb=short
    ) > .github\workflows\ci.yml
) else (
    echo No known project type detected. Skipping CI setup.
    goto :create_push
)
echo [OK] CI/CD workflow created
echo.

:: Push CI workflow
git add -A
git commit -m "ci: add GitHub Actions workflow"
git push origin main

:create_push
:: Create push.bat
(
    echo @echo off
    echo git add -A
    echo git status --short
    echo for /f "tokens=1-3 delims=/ " %%%%a in ^('date /t'^) do set D=%%%%a-%%%%b-%%%%c
    echo for /f "tokens=1-2 delims=: " %%%%a in ^('time /t'^) do set T=%%%%a:%%%%b
    echo git commit -m "update %%D%% %%T%%"
    echo git push origin main
    echo timeout /t 3
) > push.bat
echo [OK] push.bat created
echo.

:: Create build_run.bat for Android projects
if exist "gradlew.bat" (
    (
        echo @echo off
        echo echo Building...
        echo call gradlew.bat assembleDebug
        echo if %%ERRORLEVEL%% neq 0 ^(echo BUILD FAILED ^& pause ^& exit /b 1^)
        echo echo Installing...
        echo "%%LOCALAPPDATA%%\Android\Sdk\platform-tools\adb.exe" install -r app\build\outputs\apk\debug\app-debug.apk
        echo echo Launching...
        echo "%%LOCALAPPDATA%%\Android\Sdk\platform-tools\adb.exe" shell am start -n com.idolab.cardspend/.MainActivity
        echo echo DONE
        echo timeout /t 3
    ) > build_run.bat
    echo [OK] build_run.bat created
)

echo.
echo ========================================
echo  SUCCESS! - https://github.com/itsalldone-dev/%REPO_NAME%
echo ========================================
echo.
echo Files created:
echo   .gitignore
echo   .github/workflows/ci.yml
echo   push.bat
if exist "gradlew.bat" echo   build_run.bat
echo.
timeout /t 10
