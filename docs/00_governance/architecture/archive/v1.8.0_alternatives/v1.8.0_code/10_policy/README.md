# 10_policy/ — README

## 목적
Policy Layer — 권한·모드·Data Safety·Permissions Declaration·Store Policy·국가/언어.

## 책임 범위
원본 §24-6 Manifest 권한, §18-4 Mode A/B 상세, §27 전문, §28 전문, §33 Store Policy 부분. 제품이 스토어·사용자·규제와 맺는 계약.

## 외부 인터페이스
`05_constitution.md` (제1·2·6조 정책 근거), `20_features/*` (기능별 권한), `60_implementation/02_stage0_freeze.md` (Store Policy 구현), `40_i18n/*` (i18n 정책).

## 내부 파일 안내
- `01_permissions.md` — Manifest 권한 선언 (원본 §24-6).
- `02_sms_mode.md` — SMS Mode A/B 상세 (원본 §18-4).
- `03_special_access.md` — (PLACEHOLDER) Special Access (비전 작성분 §3).
- `04_data_safety.md` — Data Safety 분류·Data Sharing·삭제·NKB 암호화 (원본 §27-1,2,4,5).
- `05_permissions_declaration.md` — Permissions Declaration 권한별 정당화 (원본 §27-3).
- `06_store_policy.md` — Store Policy 대응 요약 (원본 §33-2).
- `07_country_i18n.md` — 국가/언어 분리 (원본 §28, `40_i18n/01_country_separation.md`와 동일 원본).
