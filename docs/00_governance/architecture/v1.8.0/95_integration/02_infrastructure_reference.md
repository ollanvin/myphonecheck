# 35. 인프라 운영 참조 (Infrastructure Reference)

**원본 출처**: v1.7.1 §35 (4013–4101)
**v1.8.0 Layer**: Integration
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §35 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/95_integration/02_infrastructure_reference.md`

---

# 35. 인프라 운영 참조 (Infrastructure Reference)

본 문서는 **제품·설계 기준선**이며, 이와 쌍을 이루는 **인프라·운영 기준선**은 `MyPhoneCheck_Infrastructure_v1.0.md`가 담당한다. §0-F에서 이미 선언한 페어 관계를 본 장에서 상세화.

## 35-1. 페어 구조 개요

```
docs/00_governance/
├── MyPhoneCheck_Architecture_v1.7.0.md      ← 본 문서 (제품 설계)
│   └─ 정의 범위:
│      • 헌법 7조
│      • 황금알 4속성 · Four Surfaces
│      • NKB 데이터 모델 · Decision Engine 수식
│      • Day-by-Day 구현 가이드
│      • strings.xml 명세 · CI/CD
│      • 스모크런 시나리오 · 드라이런 체크리스트
│
├── MyPhoneCheck_Infrastructure_v1.0.md      ← 페어 (인프라 운영)
│   └─ 정의 범위:
│      • 도구맵 (Claude Code·Cursor·Codex CLI·코웍·비전)
│      • 로컬 경로 구조 (C:\Users\user\Dev\ollanvin\myphonecheck\)
│      • 비밀값 SOP (PlayServiceAccount JSON · Keystore)
│      • 실행 순서 · 백업 정책
│      • 헌법 위반 시정 이력 (AWS Lambda 제거 등)
│      • 스토어 업로드 SOP (Fastlane Mac)
│
└── constitution/
    └── APP_FACTORY_CONSTITUTION_ROOT_ROLE.md
```

## 35-2. 문서 관할 분리표

| 주제 | 관할 문서 | 비고 |
|---|---|---|
| 헌법 7조 | **Architecture** | 제품 판단 기준 |
| Four Surfaces 범위 | **Architecture** | 제품 기능 |
| NKB 스키마 | **Architecture** | 데이터 모델 |
| Kotlin 파일 트리 | **Architecture** | 코드 구조 |
| JDK 17 toolchain | 둘 다 참조 (Architecture §26 + Infra §도구맵) | 빌드 환경 |
| CI 워크플로우 | **Architecture §26** | PR 시 검증 |
| Fastlane 릴리즈 | **Infrastructure** | Mac 한정 실행 |
| Play Console 계정 | **Infrastructure** | 대표님 소유, 비밀값 |
| Keystore 경로 | **Infrastructure** | `C:\Users\user\ollanvin\secrets\` (메모리) |
| MacinCloud 연동 | **Infrastructure** | iOS 빌드 파이프라인 |
| 워커 역할 분담 | **Infrastructure** | Cursor·Claude Code·코웍·Codex CLI |
| 백업 SOP | **Infrastructure** | git mirror clone 등 |
| 구독 가격 $2.49 | **Architecture** | 제품 결정 |
| Play Console 가격 Tier 설정 | **Infrastructure** | 운영 SOP |

## 35-3. 충돌 해소 규칙

두 문서가 충돌할 때:

1. **제품 설계 결정** (기능 범위·헌법·데이터 모델) → **Architecture 우선**
2. **인프라 운영 결정** (도구·경로·SOP) → **Infrastructure 우선**
3. 애매하거나 두 범주에 걸친 결정 → 비전이 판정하고 대표님 확인 (Rule 3)

## 35-4. 동기화 의무

| Architecture 변경 | Infrastructure 갱신 필요 |
|---|---|
| 새 Surface 추가 | 도구맵에 새 빌드·테스트 절차 반영 |
| Billing Library 버전 업 | 비밀값 SOP 재검토 (Play Public Key 갱신 시) |
| 신규 외부 API 도입 (Layer 3) | 네트워크 정책 업데이트 |
| 헌법 조항 추가 | 위반 시정 이력 섹션에 반영 |

| Infrastructure 변경 | Architecture 갱신 필요 |
|---|---|
| 워커 역할 분담 변경 | Day-by-Day의 "담당자" 표기 갱신 |
| 신규 빌드 도구 도입 | CI/CD 섹션 업데이트 |
| 백업 정책 변경 | 0-C 정책 모니터링에 반영 |

## 35-5. Infrastructure 문서 요약 (참조용)

본 Architecture가 전제하는 인프라 상태. 상세는 Infrastructure 문서 참조.

- **로컬 레포**: `C:\Users\user\Dev\ollanvin\myphonecheck\`
- **원격 레포**: `github.com/ollanvin/myphonecheck`
- **Keystore 경로**: `C:\Users\user\ollanvin\secrets\` (규약)
- **Play Public Key**: `BuildConfig.PLAY_PUBLIC_KEY` (빌드 시 주입, GitHub Secrets)
- **CI Runner**: GitHub 호스티드 `ubuntu-latest` (Mac Fastlane는 MacinCloud 임대)
- **주 구현자**: Cursor (Windows 로컬, Auto Mode)
- **감사·장시간 주행**: Claude Code (Windows 전역 + SSH + Fastlane)
- **설계·판정·워크오더 발행**: 비전 (Claude 채팅)
- **문서 작성·GitHub 운영**: 코웍 (허용폴더 + GitHub API)
- **2차 CLI 백업**: ChatGPT Codex CLI
- **자동 통신망**: **2026-04-24 폐기 완료** (ops 레포 삭제, 수동 1:1 워커 분배로 전환)


## 35-6. v1.1 갱신 주석 (2026-04-27 추가, Architecture 본문 보존)

본 §35-1 ~ §35-5는 v1.7.1 원본 전문 이관본 (텍스트 변경 금지). 
다만 Infrastructure가 v1.0 → v1.1로 갱신됨에 따라, 본 섹션에서 **현행 페어 상태**를 명시한다.

### 35-6-1. 현행 Infrastructure 페어 상태

- **원본 보관**: `docs/00_governance/infrastructure/v1.0/MyPhoneCheck_Infra_Ops_v1.0.md` (참조 전용)
- **현행 Working Canonical**: `docs/00_governance/infrastructure/v1.1/MyPhoneCheck_Infra_Ops_v1.1.md`
- **갱신 commit**: PR #3, `541b1d4` (2026-04-27)

§35-1 페어 구조 다이어그램의 `MyPhoneCheck_Infrastructure_v1.0.md` 명칭은
**v1.0 (원본 보관) + v1.1 (현행)**으로 읽는다.

### 35-6-2. v1.0 → v1.1 신설 사항 (요약)

본 Architecture v1.8.0과 정합되는 Infrastructure v1.1 신설 6대 사항:

| 위치 | 신설 내용 | Architecture 측 정합점 |
|---|---|---|
| Infra §0.2 | Architecture v1.8.0 참조 경로 5개 명시 + Rule 4 (Arch 우선) | 본 §35-3 충돌 해소 규칙 강화 |
| Infra §0.4 | 4워커 구조(2실행+2감사) + Layer별 차등 + 동시 주행 규칙 | 본 §35-5 워커 역할 정의 확장 |
| Infra §1 | Arch `05_constitution.md` 정합 주석 추가 (본문 v1.0 보존) | Arch `05_constitution.md` 헌법 7조 |
| Infra §4.0 | 쿼터 풀 (Cursor/Codex 독립, Max 공유) + 도구별 주·보조 역할 | 본 §35-5 워커 분담 상세화 |
| Infra §7 | STEP 0-bis (Arch v1.8.0 상태 확인 게이트) + STEP 5-bis (Layer별 차등 적용) | Arch v1.8.0 디렉토리 구조 |
| Infra §8.4 | 4워커 작업 판정 기준 + 외부 검증자(자비스·헐크·스타크) 판정 | Arch `00_core/02_secondary.md` 검증 절차 |

### 35-6-3. 페어 정합 검증 결과 (2026-04-27)

비전 자동 cross-ref 검증 9개 항목:
- §0.2 참조 경로 5개 실재: PASS
- STEP 0-bis 경로 일관성: PASS
- 페어 포인터 작동: PASS (본 §35-6 추가 후)
- 헌법 정합성: PASS
- v1.1 신설 섹션 12종: PASS
- 부록 A v1.1 변경 이력: PASS
- v1.0 원문 보존: PASS (100%)

### 35-6-4. 본 갱신 주석의 위치

본 §35-6은 **Architecture v1.8.0의 cross-ref 양방향 정합** 확보를 위한 최소 갱신.
v1.7.1 원본 §35 (35-1 ~ 35-5)는 무결성 보존, 새 정보는 본 §35-6 부록 형태로만 추가.

향후 Architecture MINOR 승격(v1.8.1 등) 시 본 §35-6 내용은 §35-1 ~ §35-5에 통합 가능.

---

**갱신 출처**: WO-V180-CROSSREF-PATCH-008
**갱신 사유**: 비전 cross-ref 검증 1건 WARN 즉시 수정
**갱신자**: Claude Code (대표님 지시 - 발견 즉시 수정 원칙)
---
