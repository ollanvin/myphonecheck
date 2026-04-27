# 10_policy

**목적**: 정책 관련 문서. 권한, SMS 모드, Data Safety, Permissions Declaration, Store Policy, 국가/언어.

**책임 범위**: Google Play 정책 대응, 권한 매트릭스 정책, 국제화 정책.

**외부 인터페이스**: `50_test_infra/03_permission_matrix.md`에서 권한 테스트 참조. `20_features/`에서 정책 준수 참조.

**내부 파일 안내**:
- `01_permissions.md` — Permissions + Manifest 정합 (§1 + §24-6)
- `02_sms_mode.md` — SMS Mode A/B 정책 (§2 + §18-4)
- `03_special_access.md` — 특별 접근 권한 정책 (§3, 비전 작성분)
- `04_data_safety.md` — Data Safety (§27-1, §27-2, §27-4, §27-5)
- `05_permissions_declaration.md` — Permissions Declaration (§27-3)
- `06_store_policy.md` — Store Policy 대응 (§33 일부)
- `07_country_i18n.md` — 국가/언어 분리 (§28)
