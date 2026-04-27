# Stage 2-005 수동 테스트 절차

**대상**: PR Stage 2-005 — 검색 3대 축 + InputAggregator
**Architecture**: v2.0.0 §30 + 헌법 §1 Out-Bound Zero / §2 In-Bound Zero / §3 / §8

---

## 0. 준비

- 본 PR 범위: 인터페이스 + 기본 구현 + InputAggregator + DI.
- 실제 공개 피드 다운로드, UI 통합은 후속 PR.
- 따라서 수동 테스트는 빌드/회귀 검증 + DI 그래프 무결성 중심.

## 1. 빌드·설치

```powershell
.\gradlew assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 2. DI 그래프 무결성 (앱 부팅)

- 앱 정상 부팅 확인 (Hilt 그래프 컴파일 통과 = `assembleDebug` 성공으로 검증됨).
- 부팅 후 메인 화면 노출 → DI 누락 시 즉시 crash이므로 정상 진입 = 그래프 OK.

## 3. 회귀 — 모든 Surface 동작 동일

- CardCheck 진입 → 거래 리스트 표시.
- CallCheck 진입 → 통화 이력 정규화.
- MessageCheck 진입 → 발신자 인벤토리 + 분류.
- PushTrash 진입 → 휴지통 동작.
- 본 PR이 기존 Surface의 어떤 코드 경로도 변경하지 않으므로 동작 동일해야 함.

## 4. 헌법 §1 Out-Bound Zero 검증

- 본 PR은 외부 통신 코드 0:
  - `CustomTabExternalSearch`는 URL 빌드만 — 본 앱이 요청 발신 안 함.
  - `PublicFeedAggregator`는 `cache.lookup()`만 호출 — 외부 통신 0.
  - `PublicFeedSource` 기본 바인딩 = `emptyList` — 옵트인된 출처 0.
- adb logcat에서 외부 IP/도메인 트래픽 없음 확인.

## 5. 후속 PR 통합 시 검증할 항목 (현 단계 비대상)

- 외부 검색 버튼 → Custom Tab 열림 (UI 레이어 통합 후).
- 공개 피드 옵트인 토글 (Settings UI 통합 후).
- 내부 이력 검색 결과 표시 (HistoryRepository 실 구현 후).

---

**검증 완료 조건**: 1~4 PASS (assembleDebug + 회귀 0 + 외부 통신 0).
