# 27-3. Permissions Declaration — 권한별 정당화 (Special Access 발췌)

**원본 출처**: v1.7.1 §27-3-5~§27-3-8 (26줄)
**v1.8.0 Layer**: Policy
**의존**: `10_policy/05_permissions_declaration.md` + `30_billing.md`
**변경 이력**: 본 파일은 v1.7.1 §27-3-5~§27-3-8 (26줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/10_policy/03_special_access.md`

---


- **Core user benefit**: MicCheck·CameraCheck에서 "마지막 사용 시각"을 표시하여 사용자가 **오랫동안 사용하지 않은 앱의 권한을 회수**할 수 있도록 돕는다.
- **Less-invasive alternative**: 사용 통계 없이 단순 권한 보유 앱 목록만 표시. 이 경우 "쓰지도 않는 앱의 권한"을 식별할 수 없어, 사용자 결정 품질 저하.
- **사용자 고지**: MicCheck·CameraCheck 첫 진입 시 "앱 사용 통계 접근 허용하기" 안내 → 시스템 설정으로 이동 → 사용자 수동 활성화. **자동 부여되지 않는 Special App Access**이므로 사용자 의도 확실.
- **거부 시 동작**: 앱 리스트는 표시하되 "최근 사용: 정보 없음"으로 표시. 회수 버튼은 정상 작동.



- **Core user benefit**: 통화 수신 시 오버레이로 위험도·4속성을 즉시 표시. 전체 화면 탈취 없이 비침습적.
- **Less-invasive alternative**: 일반 알림만 사용. 그러나 통화 화면 위에 즉각 표시 불가, 사용자가 알림 서랍을 내려야 함 → 착신 결정 시점 놓침.
- **사용자 고지**: 시스템 다이얼로그. 거부 시 일반 알림으로 폴백.



- **Core user benefit**: 새 통화·문자·고위험 발견 시 사용자에게 알림. Android 13+ 런타임 권한.
- **사용자 고지**: 첫 실행 시 시스템 다이얼로그. 거부 가능.

### 27-3-8. Play Integrity API (Patch 38, 런타임 권한 아님)

- **Core user benefit**: 구독 결제 활성화 시점에 기기 환경 무결성(루팅·에뮬레이터·Frida 탐지)을 확인하여 결제 우회·크랙을 방지한다. 정품 사용자 보호.
- **기술 스코프**: Google Play Services `com.google.android.gms:play-services-integrity` Gradle 의존성. **런타임 권한 요청 없음**, Manifest 변경 없음, 사용자 상호작용 없음.
- **데이터 처리**: Play Integrity 토큰은 Google Play Services에서 직접 반환받아 **로컬에서만 파싱**. 자체 서버로 전송하지 않음 (`classicRequest` 모드). 헌법 1조 "스토어 공식 API 허용" 범위 내.
- **사용자 고지**: 온보딩 결제 화면에 1줄 고지: "결제 활성화 시 Google Play가 기기 무결성을 확인합니다. 이 정보는 Google Play와 본 앱 사이에서만 사용됩니다."
- **거부 가능성**: 사용자 거부 메커니즘 불가 (Google Play Services 내장 기능). 단, 네트워크 단절·Google Play 미지원 디바이스에서는 자동 스킵되며 1계층 검증만으로 활성화 허용 (헌법 4조 fail-open).

