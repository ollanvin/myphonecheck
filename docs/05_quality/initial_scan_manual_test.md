# Initial Scan 수동 테스트 절차

**대상**: PR Initial Scan (v2.0.0 §28 정식 코드 구현)
**Architecture**: v2.0.0 §28 + 헌법 §1/§2/§3/§8

---

## 0. 준비

- 디바이스 또는 에뮬레이터 (Android 8.0+).
- USIM 장착 권장.
- CallLog 1건 이상, SMS Inbox 1건 이상.

## 1. 빌드·설치

```powershell
.\gradlew assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 2. Room v13 → v14 마이그레이션 (기존 사용자)

기존 v13 DB가 설치된 디바이스에서 갱신 설치:
- 앱 부팅 정상.
- adb logcat에서 Room migration 오류 없음.
- 기존 데이터 (UserCallRecord, CardTransaction 등) 그대로.
- 신규 테이블 (call_base_entry, sms_base_entry, package_base_entry, sim_context_snapshot) 생성됨.

신규 설치는 자동으로 v14 스키마로 시작.

## 3. Initial Scan 진입

1. 앱 진입 → Settings → "Initial Scan" 카드 탭.
2. "Permission consent" 화면 노출.
3. "Start scan" 탭 → 시스템 다이얼로그 (READ_PHONE_STATE / READ_CALL_LOG / READ_SMS) 동의.
4. 진행 화면 (CircularProgressIndicator + SIM/Calls/SMS/Packages 라벨 표시).
5. 완료 화면: 각 베이스 항목 수 표시.
   - "SIM context" — 국가 + 통신사
   - "Call log" — N entries
   - "SMS inbox" — N entries
   - "Installed apps" — N entries
6. "Continue" 탭 → 이전 화면 복귀.

## 4. 권한 거부 시

- 권한 거부 후 "Start scan" → 스캐너가 권한 없으면 빈 리스트 반환.
- 결과 화면에 0 entries 표시. 앱 crash 없음.

## 5. 재스캔 동작

- 한 번 완료 후 다시 Settings → Initial Scan 진입.
- ViewModel `checkAlreadyCompleted()`가 SimContextSnapshot 존재 시 즉시 onComplete 콜백 → 화면 자동 닫힘.
- 재스캔이 필요하면 BaseDataRepository.clear() 또는 후속 PR의 명시 재스캔 버튼 사용.

## 6. 회귀 — 기존 Surface 동작 동일

- CardCheck, CallCheck, MessageCheck, PushTrash 진입 → 기존 동작 그대로.
- Settings 화면의 기존 진입 카드 (Backup, PushTrash, CardCheck, CallCheck, MessageCheck) 정상 동작.

## 7. 헌법 §1 Out-Bound Zero 검증

- adb logcat 또는 네트워크 모니터에서 외부 IP 트래픽 없음 확인.
- Initial Scan 코드 경로는 모두 디바이스 로컬 (ContentResolver, PackageManager, TelephonyManager).

## 8. 헌법 §2 In-Bound Zero 검증

- Room DB 덤프(SQLCipher) 시 SMS 본문/통화 녹음 없음.
- call_base_entry: e164/regionCode/numberType/카운트만.
- sms_base_entry: sender/카테고리/카운트만 (본문 0).
- package_base_entry: 권한 CSV만 (앱 데이터 0).

---

**검증 완료 조건**: 1~8 항목 모두 PASS, 회귀 0, 외부 통신 0.
