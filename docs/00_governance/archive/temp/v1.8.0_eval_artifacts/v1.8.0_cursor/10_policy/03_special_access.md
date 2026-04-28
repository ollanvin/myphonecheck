### 27-3-5. PACKAGE_USAGE_STATS (Special App Access)

**원본 출처**: v1.7.1 §27-3-5 (3231–3236)
**v1.8.0 Layer**: Policy
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §27-3-5 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/10_policy/03_special_access.md`

---

### 27-3-5. PACKAGE_USAGE_STATS (Special App Access)

- **Core user benefit**: MicCheck·CameraCheck에서 "마지막 사용 시각"을 표시하여 사용자가 **오랫동안 사용하지 않은 앱의 권한을 회수**할 수 있도록 돕는다.
- **Less-invasive alternative**: 사용 통계 없이 단순 권한 보유 앱 목록만 표시. 이 경우 "쓰지도 않는 앱의 권한"을 식별할 수 없어, 사용자 결정 품질 저하.
- **사용자 고지**: MicCheck·CameraCheck 첫 진입 시 "앱 사용 통계 접근 허용하기" 안내 → 시스템 설정으로 이동 → 사용자 수동 활성화. **자동 부여되지 않는 Special App Access**이므로 사용자 의도 확실.
- **거부 시 동작**: 앱 리스트는 표시하되 "최근 사용: 정보 없음"으로 표시. 회수 버튼은 정상 작동.
