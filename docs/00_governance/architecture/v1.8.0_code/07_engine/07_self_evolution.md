# 12. 자가 진화 (Self-Evolution)

**원본 출처**: v1.7.1 §12 (50줄)
**v1.8.0 Layer**: Engine
**의존**: `07_engine/03_nkb.md`
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/07_engine/07_self_evolution.md`

---

# 12. 자가 진화 (Self-Evolution)

NKB는 시간이 지날수록 사용자 환경에 맞춰 **스스로 개선**된다. 본사 재학습 없이 디바이스 내에서 완결.

## 12-1. 자가 진화 메커니즘

| 메커니즘 | 트리거 | 동작 |
|---|---|---|
| **Periodic Refresh** | WorkManager 매일 1회 | Stale 엔트리 재 probe |
| **User Action Bias** | UserAction 이벤트 | 해당 번호 Softmax 재계산 |
| **Cluster Update** | 7일 경과 | Self-Discovery probe 재실행, 응답 시간 갱신 |
| **Stale Cleanup** | 365일 경과 (Tier 4도 초과 시) | 엔트리 삭제 대신 "archive" 플래그 |
| **Rebalancing** | 신호 출처 분포 편향 시 | Tier 가중치 동적 조정 (범위 제한) |

## 12-2. PeriodicMaintenance (WorkManager)

```kotlin
class PeriodicMaintenanceWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // 1. Stale 엔트리 재검증
        val staleEntries = nkb.findStale(limit = 20)
        staleEntries.forEach { entry ->
            decisionEngine.enqueueRefresh(entry.identifier)
        }

        // 2. Cluster 재검증 (7일 경과 시)
        if (shouldReverifyCluster()) {
            selfDiscovery.reprobe()
        }

        // 3. 초과 archive 정리
        nkb.archiveStaleEntries(olderThan = 365.days)

        return Result.success()
    }
}
```

## 12-3. 자가 진화의 헌법 정합

- 본사 재학습 0 → 제3조·제7조 정합
- 사용자 행동만으로 개선 → 개인화(맞춤) 헌법 구현
- Periodic도 디바이스 내부 이벤트 → 외부 의존 0

---
