# 32. Interface Injection (v1.5.2 Patch 10)

**원본 출처**: v1.7.1 §32 (42줄)
**v1.8.0 Layer**: Implementation
**의존**: `60_implementation/05_repo_layout.md` + `07_engine/05_decision_formula.md`
**변경 이력**: 본 파일은 v1.7.1 §32 (42줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/60_implementation/03_interface_injection.md`

---


## 32-1. 원칙

`engine/decision`이 feature/* 모듈을 **직접 호출하지 않는다**. feature/* 가 구현하는 인터페이스를 `engine/decision`이 의존한다. 의존 방향 역전 (DIP).

## 32-2. 인터페이스 목록

```kotlin
// core/common 에 정의
interface UserNotifier {
    fun showRiskAlert(risk: RiskKnowledge)
}

interface SubscriptionGate {
    fun isPremiumActive(): Boolean
}

interface PermissionChecker {
    fun hasPermission(permission: String): Boolean
}
```

feature/* 모듈이 각 인터페이스를 구현하여 DI 컨테이너(Hilt 또는 Koin)로 주입.

## 32-3. 의존 그래프 (재확인)

```
core/common (interface 정의만)
    ▲
    │ 구현
    │
feature/call ───┐
feature/message ─┼─▶ engine/decision ─▶ core/common (interface 사용)
feature/mic ─────┤
feature/camera ──┘
```

역방향 호출 없음. Stage 0 FREEZE 유지.

---

