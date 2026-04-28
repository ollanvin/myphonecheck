# 15. 본사 매핑 0건 검증 체크리스트

**원본 출처**: v1.7.1 §15 (1652–1716)
**v1.8.0 Layer**: Verification
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §15 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/80_verification/05_central_mapping_zero.md`

---

# 15. 본사 매핑 0건 검증 체크리스트

헌법 7조("본사 운영 0 / 본사 매핑 0 / 본사 데이터센터 0")의 **자동 검증 스크립트**.

## 15-1. 검증 스크립트 목록

| 스크립트 | 검증 대상 | 실패 조건 |
|---|---|---|
| `scripts/verify-no-server.sh` | 프로젝트 전체 | "our backend", "company server", AWS SDK import 발견 |
| `scripts/verify-network-policy.sh` | Manifest + HttpClientProvider | 네트워크 진입점이 Layer 2·3 외 존재 |
| `scripts/verify-no-mapping.sh` | 리소스 + 코드 | "country → engine" 하드코딩 매핑 발견 |
| `scripts/verify-frozen-model.sh` | Entity 파일 | Frozen 필드 누락·변경 발견 |
| `scripts/verify-strings-i18n.sh` | Kotlin 코드 | 문자열 하드코딩 (`"안녕하세요"` 등) 발견 |

## 15-2. 검증 샘플 (verify-no-server.sh)

```bash
#!/usr/bin/env bash
set -e

# 우리가 운영하는 서버 코드·문서 패턴 탐지
PATTERNS=(
    "our-backend"
    "ollanvin-server"
    "myphonecheck-api"
    "com.amazonaws.*Lambda"
    "com.amazonaws.*DynamoDB"
    "api.myphonecheck.app"
)

FAILED=0
for pattern in "${PATTERNS[@]}"; do
    if grep -r -i "$pattern" --include="*.kt" --include="*.kts" --include="*.xml" .; then
        echo "❌ 헌법 1·7조 위반 가능성: '$pattern' 발견"
        FAILED=1
    fi
done

[ $FAILED -eq 0 ] && echo "✅ 본사 매핑 0건 검증 PASS"
exit $FAILED
```

## 15-3. CI 통합

`.github/workflows/android-ci.yml`에 다음 단계 추가:

```yaml
- name: Verify Constitution Compliance
  run: |
    scripts/verify-no-server.sh
    scripts/verify-network-policy.sh
    scripts/verify-no-mapping.sh
    scripts/verify-frozen-model.sh
    scripts/verify-strings-i18n.sh
```

한 개라도 실패하면 PR 머지 차단.

## 15-4. 위반 발견 시 SOP

1. Detekt·CI에서 자동 탐지
2. PR에 자동 코멘트 (violation 목록 + 해당 파일·라인)
3. 수정 후 재실행 → PASS까지 머지 차단
4. 감사 로그(§0-B)에 위반 이력 기록
