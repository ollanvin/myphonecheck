# Number Profile Label Tag Design

## Goal

Unsaved numbers must support local relationship management without requiring contact storage.

This structure is user-owned, on-device only, and separate from search evidence.

## Core Structures

### NumberProfile

Persistent local memory for a normalized number:

- `normalizedNumber`
- `lastInteractionAt`
- `lastCallAt`
- `lastSmsAt`
- `quickLabels`
- `doNotMissFlag`
- `blockState`
- `userMemoShort`

### QuickLabel

One-tap user labels:

- `IMPORTANT`
- `REVIEW`
- `BUSINESS`
- `PICK_UP`
- `SMS_ONLY`
- `CAUTION`
- `DO_NOT_BLOCK`
- `DONE`

### DetailTag

Optional expandable tags:

- `normalizedNumber`
- `tagName`
- `source`
- `createdAt`
- `updatedAt`

`systemSuggested` is recommendation-only. It must not be auto-saved.

### SearchStatus

Search status is separate from user judgment and uses only fixed wording:

- 검색 결과 일치 정보 있음
- 검색 결과 일치 정보 없음
- 검색 결과 확인 불충분
- 아직 검색 확인 안 됨

### ActionState

Persistent user action memory reused on later interactions:

- `NONE`
- `BLOCKED`
- `DO_NOT_BLOCK`

## Separation Principle

Search-result status and user relationship signals must never be merged into one line of meaning.

Keep them separate in UI:

- search-result status
- user labels and tags
- user action memory

The app shows evidence and stored user intent. It does not auto-finalize relationship meaning.

## Relationship Management Rule

Users must be able to manage number relationships without saving contacts.

That means:

- one-tap quick labeling after calls
- quick labels and detail tags from message detail
- persistence across future call and SMS interactions
- relationship memory independent from the address book
