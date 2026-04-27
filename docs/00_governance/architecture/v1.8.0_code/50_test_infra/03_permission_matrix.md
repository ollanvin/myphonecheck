# 34-1. 권한 매트릭스 (Patch 23·36·37 적용)

**원본 출처**: v1.7.1 §34 H1 + §34-1 전문
**v1.8.0 Layer**: Test
**의존**: `50_test_infra/01_test_infra.md`
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/50_test_infra/03_permission_matrix.md`

---

# 34. 테스트 인프라
## 34-1. 권한 매트릭스 (Patch 23·36·37 적용)

| 권한 | CallCheck | MessageCheck | MicCheck | CameraCheck | 헌법 조항 |
|---|---|---|---|---|---|
| `READ_PHONE_STATE` | ✅ 필수 | - | - | - | 제4조 |
| `READ_CALL_LOG` | ✅ 선택 (Cold Start용) | - | - | - | 제4조 |
| `READ_SMS` / `RECEIVE_SMS` / `SEND_SMS` / `WRITE_SMS` | - | ✅ Mode A 전용 | - | - | 제4조 (Mode B는 권한 0) |
| `READ_CONTACTS` | ✅ 선택 | ✅ 선택 | - | - | 제4조 |
| ~~`QUERY_ALL_PACKAGES`~~ | ❌ | ❌ | **❌ Patch 36 제거** | **❌ Patch 36 제거** | **Patch 36 — `<queries>` 블록(§24-6-1) 대체** |
| `PACKAGE_USAGE_STATS` | - | - | ✅ Special Access (선택) | ✅ Special Access (선택) | 제4조 |
| `SYSTEM_ALERT_WINDOW` | ✅ 필수 (오버레이) | - | - | - | 제4조 |
| `POST_NOTIFICATIONS` | ✅ 선택 | ✅ 선택 | ✅ 선택 | ✅ 선택 | 제4조 |
| `INTERNET` | ✅ (Layer 2·3) | ✅ | - | - | 제1조 (Mic/Camera는 네트워크 호출 없음, §18-6-1) |
| `ACCESS_NETWORK_STATE` | ✅ | ✅ | - | - | 제4조 (SLA Detector) |
| **Play Integrity API (런타임 권한 아님, Patch 38)** | **✅ 결제 활성화 시** | **-** | **-** | **-** | **제1조 (스토어 공식 API 허용 범위)** |
| ~~`RECORD_AUDIO`~~ | ❌ | ❌ | **❌ 요청 안 함** | ❌ | **Patch 23 — 스캔만 수행, 녹음 안 함** |
| ~~`CAMERA`~~ | ❌ | ❌ | ❌ | **❌ 요청 안 함** | **Patch 23 — 스캔만 수행, 촬영 안 함** |
| ~~`BROADCAST_SMS`~~ | ❌ | ❌ | ❌ | ❌ | **Patch 17 — Play 정책 위반, 제거** |

**Patch 37 반영 (P0-1)**: v1.6.2까지 본 매트릭스에 `QUERY_ALL_PACKAGES ✅ 필수` 행이 잔존했으나, 실제 Manifest는 Patch 36으로 `<queries>` 대체되어 있어 불일치. 7-워커 평가(Claude Code·Cursor·코웍 3자 합의)로 지적. 본 v1.7.0에서 매트릭스를 Manifest와 정합하게 정정.

**Mode A/B 명시 (P2-4)**: MessageCheck의 SMS 4권한은 Mode A(Default SMS Handler) 전용. Mode B(Share Intent 기본)는 권한 0으로 작동.
