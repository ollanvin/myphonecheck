# 14. 비기능 / 디바이스 SLA (4단계)

**원본 출처**: v1.7.1 §14 (1589–1650)
**v1.8.0 Layer**: Engine
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §14 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/07_engine/08_sla.md`

---

# 14. 비기능 / 디바이스 SLA (4단계)

헌법 제4조의 SLA 4단계(§1-4)를 운영 지표로 구체화.

## 14-1. L1 Full (정상)

| 지표 | 목표 |
|---|---|
| NKB Lookup p95 | ≤ 100ms |
| NKB Lookup p99 | ≤ 200ms |
| Surface 렌더 p95 | ≤ 500ms |
| External probe p95 (Layer 2+3) | ≤ 2s |
| 4속성 출력 완결 p95 | ≤ 2.5s |

## 14-2. L3 Offline (헌법 기준선)

| 지표 | 목표 |
|---|---|
| **NKB Hit p95** | **≤ 5ms** ← SLA-14-2 약속 |
| 4속성 출력 완결 p95 | ≤ 500ms |
| UI 반응성 | L1과 동일 (비동기 처리) |

**검증 방법**: `SmokeRun11.kt` (§18 스모크런 11번)에서 비행기 모드 + NKB 200건 엔트리 상태에서 p95 측정.

## 14-3. 리소스 예산

| 리소스 | 상한 | 측정 방법 |
|---|---|---|
| 설치 용량 | ≤ 20MB (초기) | APK analyzer |
| RAM (실행 중) | ≤ 80MB (중위값) | Android Studio Memory Profiler |
| NKB DB 크기 | ≤ 50MB (1년 차) | Room DB 파일 크기 |
| 배터리 소모 | ≤ 1%/일 (백그라운드) | Play Console Vitals |
| 네트워크 | ≤ 5MB/월 (Layer 2+3) | 쿼터 관리 |

## 14-4. SLA Level Detector

```kotlin
class SlaLevelDetector(
    private val networkMonitor: NetworkStateMonitor,
    private val publicApiProbe: PublicApiProbe,
    private val nkb: NkbHealth
) {
    fun currentLevel(): SlaLevel {
        if (!nkb.isHealthy) return SlaLevel.L4_CATASTROPHIC
        if (!networkMonitor.isConnected) return SlaLevel.L3_OFFLINE
        if (!publicApiProbe.isResponsive()) return SlaLevel.L2_DEGRADED
        return SlaLevel.L1_FULL
    }
}

enum class SlaLevel {
    L1_FULL, L2_DEGRADED, L3_OFFLINE, L4_CATASTROPHIC
}
```

## 14-5. SLA 저하 시 UX

- L1 → L2: 알림 없음 (사용자 관점 동일)
- L2 → L3: 상단 배너 "오프라인 모드 — 저장된 정보로 작동 중"
- L3 → L4: 모달 "초기화가 필요합니다 — 데이터가 손상되었습니다" + Cold Start 재실행 버튼

---
