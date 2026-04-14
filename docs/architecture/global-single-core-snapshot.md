# MyPhoneCheck Global Single Core Snapshot

Updated: 2026-04-14

## Fixed architecture

MyPhoneCheck is a single global core for 190 countries.

The structure is fixed into three layers:

1. Global Core
   - identifier analysis
   - call/SMS shared decision pipeline
   - on-device search evidence handling
   - number profile memory
   - quick labels
   - detail tags
   - search status
   - action memory
   - local persistence only
2. Country Policy Layer
   - number normalization
   - country code handling
   - emergency/special-number exceptions
   - search-source availability flags
   - local risk boost rules
   - locale formatting policy hooks
3. Presentation Layer
   - localized wording
   - warning tone
   - display formatting
   - market copy only

## Current code anchors

- Global Core entry types:
  - `core/model/.../GlobalIdentifierCore.kt`
  - `core/model/.../SearchStatus.kt`
  - `core/model/.../ActionState.kt`
- Country Policy draft contract:
  - `core/model/.../GlobalLayerContracts.kt`
- Existing country policy implementation:
  - `feature/call-intercept/.../CountryInterceptPolicyProvider.kt`
- Shared decision implementation:
  - `feature/call-intercept/.../CallInterceptRepository.kt`
  - `feature/call-intercept/.../CallInterceptRepositoryImpl.kt`
- SMS reusing core:
  - `feature/message-intercept/.../SmsInterceptReceiver.kt`
- Local relationship memory:
  - `data/local-cache/.../NumberProfileEntity.kt`
  - `data/local-cache/.../DetailTagEntity.kt`
  - `data/local-cache/.../NumberProfileRepository.kt`

## Core ownership rules

- CallCheck Core is the only decision core.
- MessageCheck is not an independent engine.
- SMS may pass link metadata, but message semantics do not become a separate classifier.
- PushCheck remains disabled and outside the active architecture.
- PrivacyCheck remains separate and unchanged in this snapshot.

## Shared core state

The global core operates with these shared concepts:

- `NumberProfile`
  - local relationship memory for unsaved numbers
  - separate from contacts
- `QuickLabel`
  - user-owned one-tap memory labels
- `DetailTag`
  - optional user/system-suggested tags
  - `systemSuggested` is recommendation only
- `SearchStatus`
  - fixed status wording
  - never mixed with user labels/tags
- `ActionState`
  - future interaction memory such as `BLOCKED` or `DO_NOT_BLOCK`

## Search status separation

Search result status must use only these fixed direct labels:

- 검색 결과 일치 정보 있음
- 검색 결과 일치 정보 없음
- 검색 결과 확인 불충분
- 아직 검색 확인 안 됨

User labels/tags must be rendered in a separate section.
They must never be merged into search evidence wording.

## Call and SMS alignment

- Incoming call:
  - `MyPhoneCheckScreeningService` -> `CallInterceptRepository.analyzeIdentifierTwoPhase(...)`
- Incoming SMS:
  - `SmsInterceptReceiver` -> `CallInterceptRepository.analyzeIdentifier(...)`

Both paths now enter the same global identifier core.
The difference between call and SMS is input metadata, not engine branching.

## Country expansion rule

Future country support must be added by:

- extending `CountryPolicyLayer` implementations
- extending presentation/localization policy

Future country support must not require:

- core branching by country
- a separate country-specific engine
- message-specific classification engines
