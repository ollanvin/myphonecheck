# 30. Memory Budget (메모리 예산)

**원본 출처**: v1.7.1 §30 (3402–3456)
**v1.8.0 Layer**: Implementation
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §30 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/60_implementation/04_memory_budget.md`

---

# 30. Memory Budget (메모리 예산)

## 30-1. NKB 엔트리당 크기 상한 (MEM-2KB 약속)

약속 ID: `MEM-2KB` (§0-B 감사 로그)

`NumberKnowledge` 엔트리 1개당 메모리 + 디스크 크기 상한 **2KB**.

### 30-1-1. 예산 내역

| 필드 | 예상 크기 |
|---|---|
| numberE164 | 16B (최대 E.164 15자리 + UTF-8) |
| categoryDistribution (6 카테고리) | 48B (6 × 8B float) |
| topCategory, riskLevel, isAmbiguous 등 enum·bool | 20B |
| SignalSummary | 200B (카운트 + featureCounts Map) |
| TierContribution (최대 4 Tier) | 128B |
| firstSeenAt, lastUpdatedAt (Long×2) | 16B |
| discoveredClusterId | 16B |
| 기타 메타 | 32B |
| **합계 (라이브 오브젝트)** | **~476B** |
| Room 직렬화 오버헤드 | ~500B |
| 인덱스 오버헤드 | ~300B |
| **디스크 총합** | **~1.28KB** (2KB 상한 내) |

### 30-1-2. 검증 방법

- Android Studio Memory Profiler로 100건 엔트리 적재 후 총 heap 측정 → `/100`
- Room DB 파일 크기 / 엔트리 수 비교
- CI 테스트 `NkbSizeBudgetTest` 작성 (5000건 삽입 후 DB 파일 크기 측정)

## 30-2. DB 전체 크기 상한

§14-3 SLA 기준: NKB DB 크기 ≤ 50MB (1년 차)

### 30-2-1. 50MB 초과 시 동작

1. `PeriodicMaintenanceWorker`가 상시 DB 크기 측정
2. 50MB 초과 감지 → 365일 경과 엔트리 archive 플래그 전환 (삭제 아님)
3. archive 엔트리는 조회 시 `STALE_OFFLINE`으로 표시, 신뢰도 하락
4. 200MB 초과 시: archive 엔트리 중 userActionCount=0인 것부터 삭제 (사용자 행동 있는 것은 보존)

## 30-3. 런타임 RAM 상한

- 활성 앱 중위값 ≤ 80MB (Android Studio Memory Profiler)
- 오버레이 표시 중 피크 ≤ 150MB
- OOM 발생 시 Crashlytics 보고 → 다음 릴리즈에서 최적화

## 30-4. 메모리 누수 방지

- 싱글톤 객체에서 Context 참조 시 `ApplicationContext`만 사용
- Coroutine Scope는 lifecycle 기반 (viewModelScope·lifecycleScope)
- BroadcastReceiver는 메모리 누수 위험 없음 (앱 단일 등록)
- LeakCanary를 debug 빌드에 포함
