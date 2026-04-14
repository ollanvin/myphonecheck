# Emulator Validation Checklist

## Next Validation Session

Run this checklist in the emulator on the next validation pass.

## Core Reuse

- Confirm incoming SMS uses the same identifier core path as calls.
- Confirm there is no independent MessageCheck parsing engine left in active flow.
- Confirm SMS handling is limited to number-based analysis plus link warning metadata.

## PushCheck

- Confirm PushCheck is not visible in navigation.
- Confirm PushCheck background behavior remains disabled.

## Search Status

- Confirm search status uses only the fixed direct wording:
  - 검색 결과 일치 정보 있음
  - 검색 결과 일치 정보 없음
  - 검색 결과 확인 불충분
  - 아직 검색 확인 안 됨
- Confirm search status is rendered separately from user labels and tags.

## Number Profile

- Confirm saved quick labels reappear on the next call.
- Confirm saved quick labels reappear on the next SMS detail view.
- Confirm detail tags persist across sessions.
- Confirm `차단 금지` style action memory persists separately from search status.

## Message Actions

- Confirm `삭제` works.
- Confirm `차단` works.
- Confirm `둘 다` works.
- Confirm the next interaction reflects the stored action state correctly.
