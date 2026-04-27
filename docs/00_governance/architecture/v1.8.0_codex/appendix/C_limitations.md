## 0-D. 검증 불가 한계 로그 (Limitations Log)

**원본 출처**: v1.7.1 §0-D (15줄)
**v1.8.0 Layer**: Appendix
**의존**: `appendix/A_audit_log.md` + `appendix/D_version_matrix.md`
**변경 이력**: 본 파일은 v1.7.1 §0-D (15줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/appendix/C_limitations.md`

---


현재 시점에 검증할 수 없는 항목을 명시. 향후 실측으로 보완한다.

| 한계 | 내용 | 보완 시점 |
|---|---|---|
| 실기기 성능 | L3 NKB p95 ≤ 5ms 실측 미완 | Stage 1 이후 실기기 테스트 |
| 국가별 스토어 가격 | $2.49 Tier가 없는 국가 존재 가능 | Play Console 가격 설정 시 |
| 공공 API 응답 안정성 | KISA·경찰청 API 가용성 SLA 미확인 | 통합 개발 단계 |
| MacinCloud 연동 | iOS 빌드·서명·배포 파이프라인 실측 미완 | Stage 2 iOS 진입 시 |
| 외부 검증 라운드 | 헐크·자비스·스타크 본 재작성본 리뷰 미실시 | 대표님 승인 직전 |
| PACKAGE_USAGE_STATS 존폐 (2차 외부 검증, 자비스 지적) | 자비스 2차 라운드 R-05: "최근 사용 시각은 편의 기능, Play 심사 리스크 > 사용자 결정 품질"로 제거 권고. 본문 §27-3-5는 "반쪽 기능 금지" 원칙으로 Special Access 유지 중. 헌법 해석 충돌로 대표님 판단 보류. | v1.7.2 또는 Play 심사 실제 피드백 시점 |
| Billing 실패 로컬 로그 (2차 외부 검증, 스타크 지적) | 스타크 2차 라운드: `onPurchasesUpdated` OK 분기만 존재, 실패 시 CS 대응 데이터 없음. 로컬 로그 추가는 헌법 1·2조(In/Out-Bound Zero) 해석 충돌 + 서버 0 원칙상 CS 대응 불가 구조가 필연적 대가. 대표님 판단 보류. 당면 대안: "사용자가 Play Console로 직접 환불 요청" 공식 채널 선언. | v1.7.2 |
| Frida 런타임 메모리 후크 완전 방어 | Patch 38 Play Integrity API는 루팅·에뮬레이터·Frida Gadget 주입까지 탐지. 단 런타임 메모리 후크는 서버 측 재검증 없이 근본 해결 불가 (헌법 1조 "자체 서버 0"과의 트레이드오프). 알려진 한계. | 서버 도입 없는 한 보완 불가 |

