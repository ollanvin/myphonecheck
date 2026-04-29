# README — MyPhoneCheck Architecture v2.6.0

**원본 출처**: v2.5.0 (검색 4축 → 2축 단순화 MINOR)
**v2.6.0 작업 성격**: 사용자 소버린 + 3액션 단일 책임 MINOR 승격
**의존**: `00_core/01_primary.md`
**변경 이력**: v1.7.1 → v1.8.0 → v1.9.0 → v2.0.0 → v2.1.0 → v2.2.0 → v2.3.0 → v2.4.0 → v2.5.0 → v2.6.0
**파일 경로**: `docs/00_governance/architecture/v2.6.0/README.md`

---

## 목적

Architecture v2.5.0을 기반으로 v2.6.0 MINOR 승격(§3 결정권 중앙집중 금지 본문 강화 + §11 "3액션 단일 책임" 신설) 결과를 담는 Working Canonical 디렉토리.

## 작성자

워커: Claude Code (WO-V260-CONST-3ACTIONS).

## 비교

`v1.7.1/` ~ `v2.5.0/` 모두 FROZEN 보존. 본 `v2.6.0/`은 사용자 소버린 100% 귀속 + 3액션 (차단/태그/검색) 단일 책임 + 시스템 dialer 미간섭 정공법을 담는 신규 디렉토리.

## 내부 구조

`INDEX.md`와 하위 디렉터리 README 참조. 78 파일 (v2.5.0 동일 구조 + 본문 정정).

## 핵심 변경 사항 (v2.5.0 → v2.6.0)

| 영역 | 변경 |
|---|---|
| §3 결정권 중앙집중 금지 (3-3절 신설) | 사용자 데이터 소버린 100% 귀속. 자동 차단·자동 거절·기본 전화 앱 권한 영구 미포함. 시스템 dialer 미간섭. |
| §11 신설 (3액션 단일 책임) | 차단 (Block) / 태그 (Tag) / 검색 디테일 (Direct Search) 3액션만 노출. 추가 영구 금지. |
| 영구 미포함 액션 | 수신 / 거절 / 자동 차단 / 자동 거절 / 자동 무음 / 신고 / 녹음 |
| 6 Surface (21~27) | "수신/거절/차단 외" 표현 → "3액션 외" 정정. 21_call.md에 "CallCheck 3액션" 섹션 신설. |
| 경쟁 포지션 명시 | Truecaller/Whoscall (전화 앱 대체) 미선택 / Hiya/Phone by Google (보조 진영) 정합 + 검색 2축 차별화 |
| `RoleManager.ROLE_DIALER` | 영구 미사용 명시 (헌법 §3 정합) |
