# v1.8.0_cowork

**목적**: MyPhoneCheck Architecture v1.7.1 → v1.8.0 구조 마이그레이션 결과물 (코웍 담당).

**작성 워커**: 코웍 (Cowork)
**작성 완료 시각**: 2026-04-24 심야 (KST)
**작업 소요 시간**: 본 WO 접수 후 오토모드 완료까지
**비교 대상**: `v1.8.0_cursor/`, `v1.8.0_claudecode/` (3워커 독립 병렬 작업)

**작업 성격**: 기계적 재배치. 원본 텍스트 변경 0. 요약·압축·의역 없음.

**원본**: `v1.7.1/MyPhoneCheck_Architecture_v1.7.1.md` (4,556줄, 216 KB)

**WO**: WO-V180-MIGRATE-002

---

## 검증 결과

| 항목 | 결과 |
|---|---|
| 필수 39 H1 섹션 생존 | 39/39 PASS |
| UTF-8 인코딩 | 전 파일 PASS |
| POSIX EOF newline | 전 파일 PASS |
| 네이밍 규칙 (§4-8) | 전 파일 PASS |
| 총 파일 수 | 70 |
| 총 크기 | 257.9 KB (목표 210~270KB 범위 내) |
| 텍스트 변경 | 0건 (기계적 복사 only) |

## 판단 유보 항목

1. **비전 작성분 7파일**: `00_core/01~03_*.md`, `appendix/A~D_*.md`는 비전 별도 제공 예정. 현재 placeholder.
2. **§0 메타 분산**: §0은 appendix A·C·D + `70_business/02_kpi_mapping.md`에 분산 이관. WO 매핑표 지시대로.
3. **§18 분할**: §18은 `20_features/21~24_*.md`에 Surface별 분할 + `25_smoke_scenarios.md`에 나머지(스모크런·폐기·삭제 기록) 배치. 중복 최소화.
4. **§28 중복 방지**: `10_policy/07_country_i18n.md`는 `40_i18n/01_country_separation.md`로의 cross-ref만 포함. 전문 중복 방지.
