# 11. Cold Start (On-Device Bootstrap)

**원본 출처**: v1.7.1 §11 (63줄)
**v1.8.0 Layer**: Engine
**의존**: `07_engine/03_nkb.md` + `20_features/25_smoke_scenarios.md`
**변경 이력**: 본 파일은 v1.7.1 §11 (63줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/07_engine/06_cold_start.md`

---


신규 설치 후 NKB가 비어있을 때, **본사 fallback 없이** 디바이스가 자체 패턴 학습을 수행하는 절차.

## 11-1. Cold Start 단계

```
Day 0: 앱 설치·권한 허용

Step 1: 통화 이력 수집 (권한 허용 시)
  - READ_CALL_LOG → CallLog.Calls
  - 최근 90일 통화 번호·시간·횟수 수집

Step 2: 문자 이력 수집 (권한 허용 시)
  - READ_SMS → Telephony.Sms
  - 최근 90일 발신자 번호·URL 패턴 수집

Step 3: 연락처 수집 (권한 허용 시)
  - READ_CONTACTS → Contacts
  - 연락처에 있는 번호는 SAFE 초기값

Step 4: 로컬 패턴 학습
  - 반복 수신 번호 → NEUTRAL 초기값
  - 야간 수신 번호 → 의심 플래그
  - 광고 포맷 번호(15xx, 16xx 등) → AD 초기값

Step 5: Self-Discovery 실행 (§7)
  - Search Engine probe
  - Official Domain probe
  - ClusterProfile 생성·저장

Step 6: 외부 쿼리 큐 초기화
  - 상위 위험 후보 50개 번호에 대해 백그라운드 Layer 2·3 probe 예약
  - WorkManager로 7일간 분산 실행 (배터리 보호)
```

## 11-2. 권한 거부 시 graceful degradation

| 거부 권한 | 대체 동작 |
|---|---|
| READ_CALL_LOG | 통화 수신 시점부터 NKB 축적 (Cold Start 시 0건으로 시작) |
| READ_SMS | MessageCheck 기능 제한 + 사용자에게 설명 |
| READ_CONTACTS | SAFE 초기값 없이 시작 |
| READ_PHONE_STATE | **필수** — 거부 시 CallCheck 작동 불가 안내 |

## 11-3. Cold Start 권한 요청 UX

1. 앱 최초 실행: 온보딩 4개 슬라이드 (제품 소개·헌법 요약·권한 안내·시작)
2. 슬라이드 4: "시작하기" 버튼 → 순차 권한 요청
3. 각 권한 설명: "어디에 쓰이는지" 한 줄 (strings.xml 다국어)
4. 거부 가능: 권한 거부해도 앱 실행 가능, 기능만 제한

## 11-4. 초기 NKB 용량 가정

- 통화 이력 90일: 평균 수신 300건 → 고유 번호 50~150개
- 문자 이력 90일: 평균 200건 → 고유 발신자 30~80개
- 연락처: 평균 200~500명
- **총 초기 NKB 엔트리**: 500~1,000건
- **엔트리당 크기 상한**: 2KB (MEM-2KB 약속, §0-B)
- **초기 NKB 용량**: 1~2MB

---

