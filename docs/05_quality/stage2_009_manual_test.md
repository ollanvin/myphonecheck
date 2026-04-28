# Stage 2-009 수동 테스트 절차

**대상**: PR Stage 2-009 — UI 언어 Locale 실제 적용
**Architecture**: v2.1.0 §29 + 헌법 §8-2

---

## 0. 준비

- 디바이스 (Android 8.0+).
- AppCompat 1.6.0+ 백포트 활성 — Manifest의 `AppLocalesMetadataHolderService` 자동 영구 저장.

## 1. 빌드·설치

```powershell
.\gradlew assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 2. 첫 진입 — SIM_BASED default

1. 앱 신규 설치 (또는 데이터 초기화).
2. KR SIM 디바이스에서 첫 진입 → SIM_BASED preference (DataStore default).
3. Application.onCreate에서 UiLanguageApplicator.apply 호출 → Locale = ko.
4. UI 한국어 strings.xml(values-ko)이 있으면 한국어 표시; 없으면 default(values) = en 표시.

## 3. ENGLISH 즉시 적용

1. Settings → "v2.0.0 Privacy Settings" → Language 섹션 → ENGLISH 라디오.
2. ViewModel.setLanguagePreference 호출:
   - DataStore에 ENGLISH 저장.
   - UiLanguageApplicator.apply(ENGLISH, simContext) → AppCompatDelegate.setApplicationLocales(en).
3. **즉시 Activity recreate** → UI 영문 변경.
4. 앱 종료 → 재진입 → ENGLISH 유지 (백포트 자동 영구 저장).

## 4. DEVICE_SYSTEM 적용

1. Settings → DEVICE_SYSTEM 라디오.
2. Applicator → null Locale → emptyLocaleList → 시스템 default 따름.
3. 디바이스 시스템 언어 = 한국어이면 한국어 UI; 일본어이면 일본어 UI.

## 5. SIM 변경 시뮬레이션

1. 디바이스 SIM 교체 (KR → JP).
2. 앱 재시작 → SimContextProvider.resolve() = JP.
3. preference = SIM_BASED 유지.
4. UiLanguageApplicator.apply → Locale = ja.

## 6. 27+ 국가 매핑 검증

CountryToLanguageMap에 등록된 국가별 SIM 시뮬레이션 (가능 시):
- KR → ko, JP → ja, CN → zh-CN, TW → zh-TW, HK → zh-HK
- US → en-US, GB → en-GB, AU → en-AU, CA → en-CA, NZ → en-NZ, IE → en-IE, SG → en-SG
- DE → de, AT → de-AT, CH → de-CH
- FR → fr, BE → fr-BE, LU → fr-LU
- ES → es, MX → es-MX, AR → es-AR, CL → es-CL, CO → es-CO
- IT → it
- PT → pt-PT, BR → pt-BR
- RU → ru, PL → pl, UA → uk, CZ → cs
- SE → sv, NO → nb, DK → da, FI → fi
- NL → nl
- TH → th, VN → vi, ID → id, MY → ms, PH → fil, IN → hi
- TR → tr, SA/AE/EG → ar, IL → he

알려지지 않은 국가 (예: ZZ) → en fallback.

## 7. 회귀 — 다른 Surface 동작 동일

- CardCheck/CallCheck/MessageCheck/PushTrash/InitialScan/Tag List/PublicFeed 진입 → 기존 동작.
- BlockList 차단 (PR #27) 동작 동일.

## 8. 헌법 정합

- §8-2 비적용 영역 (UI 언어): 사용자 3단 fallback 선택 가능.
- 통화·전화번호 양식·파싱 엔진 등 다른 영역은 SIM 그대로 유지 (회귀 0).

---

**검증 완료 조건**: 1~8 PASS, 회귀 0, ENGLISH 즉시 적용 + 영구 저장.

## 후속 PR 항목

- strings.xml values-xx 번역 추가 (현재 default + values-ko만).
- locales_config.xml 점진 확장 (번역 완료 언어 추가).
- 사용자에게 "재시작 필요" 안내 (ENGLISH 즉시 적용은 Activity recreate, 일부 시스템 UI는 재시작 필요할 수 있음).
- Settings v2 화면에서 현재 적용된 Locale 표시.
