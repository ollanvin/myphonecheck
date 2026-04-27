# 23. 프로젝트 구조 (Repository Layout)

**원본 출처**: v1.7.1 §23 전문 (91+21=112줄, §23-4 FREEZE.md 블록 포함)
**v1.8.0 Layer**: Implementation
**의존**: `60_implementation/02_stage0_freeze.md`
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/60_implementation/05_repo_layout.md`

---

# 23. 프로젝트 구조 (Repository Layout)

## 23-1. 최상위 구조

```
myphonecheck/
├── app/                           # 최종 Android APK 조립
├── core/
│   ├── common/                    # Stage 0 4 계약 (FREEZE)
│   ├── data/                      # Room·Network·Billing·PublicApi
│   └── ui/                        # 공통 UI 컴포넌트 (FourAttributeCard 등)
├── feature/
│   ├── call/                      # CallCheck Surface
│   ├── message/                   # MessageCheck Surface
│   ├── mic/                       # MicCheck Surface
│   ├── camera/                    # CameraCheck Surface
│   ├── onboarding/
│   ├── settings/
│   └── subscription/              # Billing UX
├── engine/
│   ├── decision/                  # Decision Engine 구현
│   ├── discovery/                 # Self-Discovery
│   ├── nkb/                       # NKB Room DAO·Migration
│   └── analyzer/                  # SearchResultAnalyzer
├── i18n/
│   └── strings/                   # values-*/strings.xml 자원 (다국어)
├── scripts/
│   ├── verify-no-server.sh
│   ├── verify-network-policy.sh
│   ├── verify-no-mapping.sh
│   ├── verify-frozen-model.sh
│   ├── verify-strings-i18n.sh
│   ├── verify-doc-hash.ps1
│   └── build_architecture_v170.py
├── docs/
│   ├── 00_governance/
│   │   ├── MyPhoneCheck_Architecture_v1.7.0.md   # 본 문서
│   │   ├── MyPhoneCheck_Infrastructure_v1.0.md   # 페어 인프라 최종본
│   │   └── constitution/
│   │       └── APP_FACTORY_CONSTITUTION_ROOT_ROLE.md
│   ├── 01_prd/
│   ├── 02_design/
│   └── 99_archive/
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 23-2. Kotlin 모듈 의존 그래프 (FREEZE: Patch 10 Interface Injection)

```
app ────────────────┐
                    ▼
feature/call ─────▶ engine/decision ─▶ engine/nkb ─▶ core/data ─▶ core/common
feature/message ──▶ engine/decision ─▶ engine/nkb
feature/mic ──────▶ engine/decision
feature/camera ───▶ engine/decision
feature/onboarding ▶ engine/discovery
feature/settings ─▶ core/common
feature/subscription ▶ core/data (Billing)
```

**규칙**:
- feature/* → engine/* → core/data → core/common **방향만 허용**
- 역방향 또는 feature/* 간 직접 의존 금지
- core/common이 의존할 외부 모듈 없음 (Pure Kotlin JVM)
- Interface Injection은 `engine/decision` → feature/* 호출을 interface로 역전 (Patch 10)

## 23-3. core/common 모듈 (Stage 0 FREEZE)

core/common은 **순수 Kotlin JVM 모듈**이다. Android 의존 금지, 외부 라이브러리 의존 금지.

파일 구조:
```
core/common/
├── build.gradle.kts              # kotlin("jvm") + jvmToolchain(17)
├── src/main/kotlin/
│   ├── IdentifierType.kt         # sealed class
│   ├── RiskKnowledge.kt          # interface
│   ├── Checker.kt                # Checker<IN, OUT> interface
│   ├── DecisionEngineContract.kt # 엔진 계약
│   └── FreezeMarker.kt           # FREEZE 선언 마커 어노테이션
├── src/test/kotlin/
│   ├── FreezeMarkerTest.kt       # 22개 테스트 PASS (Patch 37 통일, 이미 완료)
│   └── ContractSignatureTest.kt
└── FREEZE.md                      # FREEZE 선언 문서
```

## 23-4. FREEZE.md 내용 (요약)

```markdown
# core/common FREEZE 선언

- Frozen 시점: 2026-04-22 Stage 0 Contracts 완료
- 변경 금지: 파일·클래스·메서드 시그니처·이름·가시성
- 변경 필요 시: MAJOR 버전 (v2.0.0)에서만 + 대표님 승인
- 검증: FreezeMarkerTest 22개 (CI에서 매 PR 실행, Patch 37 통일)
- CI 강제: PR의 core/common 변경 시 FreezeMarkerTest 실패 → 머지 차단
```

## 23-5. 기타 폴더 README 철칙 (메모리)

메모리 철칙에 따라 모든 폴더는 README.md 필수. 4종 항목:
1. 목적 (이 폴더의 존재 이유)
2. 책임 범위 (어떤 코드·문서가 들어가는가)
3. 외부 인터페이스 (다른 폴더에서 어떻게 사용하는가)
4. 내부 파일 안내 (각 파일의 역할)

빈 README·폴더명만 적힌 README는 위반.

---
