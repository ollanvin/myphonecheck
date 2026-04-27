# Cursor Scope Test Report

- 실행일시: 2026-04-24 09:21 (local, Get-Date)
- 실행 환경: Cursor 3.1.15 (FileVersion from Cursor.exe), Windows 10 Pro 25H2 (CurrentBuild 26200, registry DisplayVersion 25H2)

## 테스트 1 결과

- `C:\Users\user\ollanvin\scripts` : **성공** — Get-ChildItem OK (child count 11).
- `C:\Users\user\ollanvin\figma` : **부분성공** — Test-Path False (folder missing; not access denied).
- `C:\Users\user\AppData\Local\Android\Sdk` : **성공** — Get-ChildItem OK (child count 12).

## 테스트 2 결과

- `C:\Program Files` : **성공** — child count 37.
- `C:\Program Files\Git` : **성공** — child count 14.
- `C:\Program Files\Java` : **부분성공** — Test-Path False (path missing).
- `C:\Program Files\Android\Android Studio` : **성공** — child count 11.

## 테스트 3 결과

- `C:\` top-level : **성공** — Get-ChildItem C:\ -Force; entries include Program Files, Users, ProgramData, etc.
- `C:\Windows\System32` (Sort-Object Name, first 10) : **성공** — 첫 10개 이름(정렬): ``$file``, ``@AdvancedKeySettingsNotification.png``, ``@AppHelpToast.png``, ``@AudioToastIcon.png``, ``@AutoSrToastIcon.png``, ``@BackgroundAccessToastIcon.png``, ``@bitlockertoastimage.png``, ``@edptoastimage.png``, ``@EnrollmentToastIcon.png``, ``@facial-recognition-windows-hello.gif``

## 테스트 4 결과

- `$env:PATH` : **성공** (length 1438)

```
C:\Program Files\GitHub CLI;c:\Users\user\AppData\Local\Programs\cursor\resources\app\resources\helpers;C:\Ruby33-x64\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;C:\Program Files\NVIDIA Corporation\NVIDIA NvDLISR;C:\Program Files\dotnet\;C:\Program Files\Git\cmd;C:\Program Files\Tailscale\;C:\Program Files\Android\Android Studio\jbr\bin;C:\Program Files\Docker\Docker\resources\bin;C:\Program Files\Amazon\AWSCLIV2\;C:\Program Files\Amazon\AWSSAMCLI\bin\;C:\Program Files\nodejs\;C:\Users\user\AppData\Local\Android\Sdk\platform-tools;c:\Users\user\AppData\Local\Programs\cursor\resources\app\codeBin;C:\Users\user\AppData\Local\Programs\Python\Python314\Scripts\;C:\Users\user\AppData\Local\Programs\Python\Python314\;C:\Users\user\AppData\Local\Programs\Python\Launcher\;C:\Users\user\AppData\Local\Microsoft\WindowsApps;C:\Users\user\AppData\Local\Programs\cursor\resources\app\bin;C:\Users\user\AppData\Local\GitHubDesktop\bin;C:\Users\user\AppData\Local\Programs\Microsoft VS Code\bin;C:\Users\user\AppData\Local\Android\Sdk\platform-tools;C:\Users\user\AppData\Roaming\npm;C:\Users\user\AppData\Local\Bundletool;C:\Users\user\AppData\Local\Microsoft\WinGet\Packages\Genymobile.scrcpy_Microsoft.Winget.Source_8wekyb3d8bbwe\scrcpy-win64-v3.3.4;;C:\Users\user\AppData\Local\Programs\Ollama
```

- `$env:JAVA_HOME` : **성공** — `C:\Program Files\Android\Android Studio\jbr`
- `$env:ANDROID_HOME` : **성공** — `C:\Users\user\AppData\Local\Android\Sdk`
- `[System.Environment]::GetEnvironmentVariable("Path", "Machine")` : **성공** (length 636)

```
C:\Ruby33-x64\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;C:\Program Files\NVIDIA Corporation\NVIDIA NvDLISR;C:\Program Files\dotnet\;C:\Program Files\Git\cmd;C:\Program Files\Tailscale\;C:\Program Files\Android\Android Studio\jbr\bin;C:\Program Files\Docker\Docker\resources\bin;C:\Program Files\Amazon\AWSCLIV2\;C:\Program Files\Amazon\AWSSAMCLI\bin\;C:\Program Files\nodejs\;C:\Users\user\AppData\Local\Android\Sdk\platform-tools;C:\Program Files\GitHub CLI\;C:\Program Files\PowerShell\7\
```

## 테스트 5 결과

- `Get-ChildItem C:\` : **성공**
- `where.exe git` : **성공** — `C:\Program Files\Git\cmd\git.exe`
- `where.exe java` : **성공** — `C:\Program Files\Android\Android Studio\jbr\bin\java.exe`
- `where.exe adb` : **성공** — two paths (Android Sdk platform-tools + WinGet scrcpy package adb)
- `where.exe gradle` : **실패** — stderr (UTF-8 console):

```
INFO: Could not find files for the given pattern(s).
```

## 테스트 6 결과

- `C:\Users\user\ollanvin\test-cursor-write.txt` : **성공** — written then removed; Test-Path False after.
- `C:\Users\user\test-cursor-root.txt` : **성공** — same.
- `C:\test-cursor-systemroot.txt` : **성공** — same.

## 테스트 7 결과

- `ssh -V` : **성공** — `OpenSSH_for_Windows_9.5p2, LibreSSL 3.8.2` (printed on stderr; PowerShell may show NativeCommandError wrapper).
- `Get-Command ssh` : **성공** — `C:\WINDOWS\System32\OpenSSH\ssh.exe` (FileVersion 9.5.5.1, ProductVersion OpenSSH_9.5p2 for Windows).
- Remote SSH login not attempted.

## 최종 판정

- 프로젝트 바깥 user 영역: **접근 가능**
- Program Files 영역: **접근 가능**
- 시스템 루트: **접근 가능**
- 환경변수 읽기: **가능**
- PowerShell 실행: **가능** (gradle not on PATH)
- 바깥 경로 쓰기: **가능** (test files removed)
- SSH 클라이언트: **존재**

## 권한 설정 화면 (있다면)

- **미확인** — Cursor UI Allowed Paths / similar not inspected in this automated run.
