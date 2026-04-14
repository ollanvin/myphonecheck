# Codex Work Instruction: Global Core

## Current Work Direction

MyPhoneCheck is operating under a global single-core transition.

This means:

- keep the 190-country single-core model fixed
- do not add country-specific core branches
- keep `CallCheck` as the only judgment core
- keep `MessageCheck` as a core extension path only
- keep `PushCheck` disabled

## Documentation Rule

All project documents must be stored under the root `docs/` tree.

Do not:

- leave design notes in random folders
- mix constitution material with project implementation records
- leave duplicate temporary writeups floating in the repository

## Constitution Separation Rule

Shared philosophy and cross-project law belong in the constitution repository:

- local path: `C:\Users\user\Dev\ollanvin\web`

Project-specific applied documentation belongs in this repository:

- local path: `C:\Users\user\Dev\ollanvin\myphonecheck`

## Global Core Transition Instruction

When changing the project:

1. Prefer global-core reuse over new feature-specific engines.
2. Put country variance into policy and presentation layers only.
3. Keep relationship memory separate from contacts.
4. Keep search-result status separate from user labels and tags.
5. Show only measured local data.
6. Use direct evidence wording instead of inferred claims.

## Search Status Fixed Copy

- 검색 결과 일치 정보 있음
- 검색 결과 일치 정보 없음
- 검색 결과 확인 불충분
- 아직 검색 확인 안 됨
