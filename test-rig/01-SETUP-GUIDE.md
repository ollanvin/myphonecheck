# CallCheck 로컬 테스트 리그 셋업 가이드

## 1단계: Android Studio 설치

1. https://developer.android.com/studio 접속
2. **Download Android Studio** 클릭 → 약관 동의 → 다운로드
3. 설치 실행 → 기본값으로 Next → Install
4. 설치 완료 후 Android Studio 실행
5. **Standard** 설치 선택 → Next → Finish
6. SDK 다운로드 자동 진행 (약 2~3GB, 10분 소요)

## 2단계: SDK 컴포넌트 확인

Android Studio 실행 후:
1. **More Actions** → **SDK Manager**
2. **SDK Platforms** 탭:
   - ✅ Android 14.0 (API 34) 체크 확인
3. **SDK Tools** 탭:
   - ✅ Android SDK Build-Tools 34
   - ✅ Android SDK Command-line Tools
   - ✅ Android Emulator
   - ✅ Android SDK Platform-Tools
4. **Apply** → 설치

## 3단계: 에뮬레이터(AVD) 생성

1. **More Actions** → **Virtual Device Manager**
2. **Create Virtual Device**
3. 디바이스 선택: **Pixel 7** (추천)
4. 시스템 이미지 선택: **API 34** (x86_64) → Download 클릭
5. AVD 이름: `CallCheck-Test`
6. **Finish**

## 4단계: 환경변수 설정

PowerShell 관리자 권한으로 실행:

```powershell
# Android SDK 경로 (기본값 기준)
[System.Environment]::SetEnvironmentVariable("ANDROID_HOME", "$env:LOCALAPPDATA\Android\Sdk", "User")
[System.Environment]::SetEnvironmentVariable("Path", "$env:Path;$env:LOCALAPPDATA\Android\Sdk\platform-tools;$env:LOCALAPPDATA\Android\Sdk\emulator", "User")
```

PowerShell 재시작 후 확인:
```powershell
adb version
emulator -list-avds
```

`CallCheck-Test`가 목록에 나오면 셋업 완료.

## 5단계: 확인

```powershell
adb version          # → Android Debug Bridge version 35.x.x
emulator -list-avds  # → CallCheck-Test
```

이 두 명령이 정상 출력되면 다음 단계(자동 테스트 파이프라인)로 진행 가능합니다.
