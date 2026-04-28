# Public Feed 활성화 기록

**작성**: 비전 + Claude Code (PR #34)
**작성일**: 2026-04-28
**근거**:
- WO-V210-COMPETITOR-LICENSE-CHECK (`docs/05_quality/competitor_license_review.md`)
- WO-V210-KISA-FEED-URL (`docs/05_quality/kisa_feed_url_review.md`)
- WO-V210-FEEDS-ACTIVATE (본 PR)

---

## 활성화된 출처 (UI 노출, `requiresUserOptIn = true`)

### Security Intelligence (글로벌)
- **Abuse.ch URLhaus** (CC0)
  - URL: `https://urlhaus.abuse.ch/downloads/csv_recent/`
  - 갱신: HOURLY
  - 데이터: PHISHING_URL
- **PhishTank** (CC-BY-SA)
  - URL: `https://data.phishtank.com/data/online-valid.json`
  - 갱신: HOURLY
  - 데이터: PHISHING_URL

### Government Public (한국, Phase 2-B 보고서 정합)
- **KISA Phishing URLs** (`data.go.kr/data/15109780`)
  - URL: `https://www.data.go.kr/data/15109780/fileData.do`
  - 라이선스: 「제한 없음」 (출처 표시 권장)
  - 갱신: DAILY
  - 데이터: PHISHING_URL
  - 출처: KISA / 공공데이터포털
- **KISA Phishing URLs Recent** (`data.go.kr/data/15143094`)
  - URL: `https://www.data.go.kr/data/15143094/fileData.do`
  - 라이선스: 「제한 없음」 (출처 표시 권장)
  - 갱신: DAILY
  - 데이터: PHISHING_URL
  - 출처: KISA / 공공데이터포털

활성 출처 합계: **4** (Security 2 + Government 2).

---

## 비노출 처리된 출처 (`requiresUserOptIn = false`, UI 미표시)

### Government Public (한국) — 미결
- **kisa_smishing_kr** (스미싱 발신번호 전용 placeholder)
  - 사유: Phase 2-B §3 미결 (벌크 데이터셋 추가 조회 필요).
  - 향후: 별도 후속 WO에서 확정 후 활성화.
  - downloadUrl: 빈 문자열 (placeholder).

### Competitor App — 사업개발 트랙
- **thecall_kr** (TheCall, 한국): YELLOW (ToS UNKNOWN).
- **whowho_kr** (whowho, 한국): YELLOW (UNKNOWN).
- **moaff_kr** (뭐야이번호, 한국): YELLOW~RED (자동화·복제 금지 조항).
- **whoscall_global** (Whoscall, 글로벌): RED 무허가 / GREEN 공식 계약 (ASI) 후.

**사업개발 트랙 진행 사항 (별도)**:
1. 약관 전문 입수 + 법무 검토.
2. 운영사 직접 접촉 → 서면 라이선스 협상.
3. Whoscall ASI 공식 계약 경로 검토.
4. 협상·계약 후 별도 활성화 PR (각 출처 `requiresUserOptIn` true 전환 + 실 URL 등록).

### Telco Blocklist — 추가 검토
- **kt_blocklist_kr** (KT 통신사 차단): placeholder, 추가 검토 후 활성.
  - downloadUrl: 빈 문자열.
  - 향후: 통신사 공식 공개 데이터 확정 후 별도 PR.

비노출 출처 합계: **6** (Government Smishing 1 + Competitor 4 + Telco 1).

---

## KrCERT RSS 후속

- RSS 안내: `https://www.krcert.or.kr/kr/subPage.do?menuNo=205121`
- 목록: `https://knvd.krcert.or.kr/rssList.do`
- 단일 .xml URL은 안내 페이지 재확인 필요.
- 별도 후속 WO에서 활성화 가능 (본 PR 비포함).

---

## 헌법 정합

| 조 | 정합 |
|---|---|
| §1 Out-Bound Zero | KISA/Abuse/PhishTank 활성 출처만 다운로드, 사용자 데이터 외부 전송 0 |
| §2 In-Bound Zero | 라이선스 정합 데이터만 캐싱·가공 (CC0 / CC-BY-SA / 「제한 없음」) |
| §3 결정 중앙집중 금지 | 사용자 옵트인 명시 동의 (활성 출처만 UI 표시) |
| §5 정직성 | 라이선스·출처·갱신 주기 모두 사용자에게 표시 |
| §6 가격 정직성 | 본 앱 $2.49/월 상업 정합 라이선스만 활성 (KISA 「제한 없음」 정합) |

---

## 다음 단계

- 정기 라이선스 재검토 (분기/반기) — KISA 데이터셋 라이선스 변경 모니터링.
- KrCERT RSS 후속 활성화 WO.
- 스미싱 발신번호 데이터셋 확정 WO (Phase 2-B §3).
- Competitor 사업개발 트랙 진행 (별도, 협상·계약 진행도에 따른 단계별 PR).
- KT Telco 추가 검토 후 활성화 WO.

---

WO: WO-V210-FEEDS-ACTIVATE (정정본).
