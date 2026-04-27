# Store Policy 대응 + Stage 0 Contracts

**원본 출처**: v1.7.1 §33-2 Store Policy 대응 요약 (13줄)
**v1.8.0 Layer**: Policy
**의존**: `05_constitution.md` + `10_policy/04_data_safety.md`
**변경 이력**: 본 파일은 v1.7.1 §33-2 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cowork/10_policy/06_store_policy.md`

---

# 33. Store Policy 대응 + Stage 0 Contracts

## 33-1. Stage 0 Contracts 전문 (Kotlin 소스)
## 33-2. Store Policy 대응 요약

| 스토어 | 정책 | 대응 |
|---|---|---|
| Google Play | BROADCAST_SMS 사용 금지 | Patch 17로 제거, Mode B(Share Intent)·Mode A(Default SMS) 2-모드 |
| Google Play | ~~QUERY_ALL_PACKAGES 사용 정당화~~ | **Patch 36으로 제거 → `<queries>` 블록(§24-6-1) 기반 Package Visibility 최소화** |
| Google Play | READ_SMS·READ_CALL_LOG 민감 권한 | Permissions Declaration 제출 (§27-3) |
| Google Play | 데이터 수집 정직 선언 | Data Safety "Yes, collects + No sharing + Processed only on device" (§27-1, Patch 32) |
| Apple App Store (예정) | CallKit·CallDirectoryExtension 구조 | Phase 7 iOS 진입 시 설계 |
| Apple App Store (예정) | Message Filter Extension | Phase 7 iOS 진입 시 설계 |

---

