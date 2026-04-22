# Global Single Core System

## System Definition

MyPhoneCheck is fixed as one global system that can scale across 190 countries without country-specific core branching.

The architecture is organized into three layers:

1. Global Core
2. Country Policy Layer
3. Presentation Layer

## Global Core

The global core owns only globally shared behavior:

- identifier analysis
- call and SMS shared decision pipeline
- search evidence handling
- number profile persistence
- quick labels
- detail tags
- search status
- action memory
- on-device storage

The global core must not contain country-specific `if/else` feature behavior.

## Search Evidence — 3축 모델 (2026-04-22)

결정 엔진의 검색 증거는 3축으로 구성된다. 4개 체크 유닛 전부에 적용.

| 축 | 이름 | 데이터 원천 | 트리거 |
|---|---|---|---|
| L1 | Internal (내부) | 온디바이스 통화·문자·태그 이력 | 이벤트 인입 시 |
| L2 | External (외부) | 일반 검색엔진 (Chrome Custom Tab 1차) | 이벤트 인입 시 |
| L3 | Authoritative Open Data (오픈소스) | 정부 신고 DB, NVD, KISA, FTC 등 공신력 있는 기관의 공개 데이터 | 이벤트 인입 시 (디바이스가 직접 쿼리) |

**원칙**:
- 디바이스 완결형. 서버 인프라 없음.
- 디바이스가 "필요한 순간" 공개 API에 직접 접근.
- Room DB에 24시간 캐시 유지.

`core:common`의 `SearchEvidence.Layer` enum에 이미 L1_NKB, L2_SEARCH, L3_PUBLIC_DB 정의되어 있음 (Stage 0 동결 시점에 예견).

## Country Policy Layer

The country policy layer absorbs country variance without changing the core:

- number normalization
- country code handling
- emergency and special-number exceptions
- local permission and regulatory rules
- search-source availability flags
- locale-aware date, time, and number formatting hooks

Country expansion must happen here, not in the global core.

## Presentation Layer

The presentation layer owns only display concerns:

- localized labels
- warning copy tone
- display formatting
- market wording

Presentation must not redefine the decision core.

## Single Core Judgment Rule

`CallCheck Core Engine` is the only judgment core.

- Incoming calls enter the core directly.
- Incoming SMS also enter the same core by phone number.
- SMS body handling is limited to secondary metadata such as link existence, link length, and short-link presence.

The app must not reintroduce an independent message parsing engine.

## MessageCheck Position

`MessageCheck` is an extension path into the shared core:

- number is the primary identity
- search evidence and local history stay shared
- message content is not promoted to a separate classification engine

## PushCheck Position

`PushCheck` remains disabled in this architecture state.

It is isolated from active navigation and active background behavior until re-evaluated later.

## Country Branching Ban

The following are prohibited:

- country-specific core engines
- country-specific feature forks
- country-specific decision pipelines
- message-specific shadow engines

Future countries must be supported by adding policy and presentation layers only.
