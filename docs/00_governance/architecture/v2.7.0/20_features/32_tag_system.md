# 32. Tag System — 휘발성 메모

> 신규 v2.1.0. "저장은 싫지만 다음에 놓치고 싶지 않은 번호" 모델.
> 연락처 저장 없이 휘발성 태그로 추적.

## 32-1. 정의

번호·발신자에 사용자 정의 태그를 부여하여 다음 수신 시 알림. 연락처 저장과 별개.

용도:
- "확인 필요" — 누군지 모름, 다음에 또 오면 받아야
- "조심" — 의심 발신자, 무음·경고
- "리마인드" — 다음에 보면 알려줘
- "보관" — 기록만, 저장 X
- 사용자 자유 텍스트

## 32-2. Entity

```kotlin
@Entity(tableName = "phone_tag")
data class TagEntity(
    @PrimaryKey val identifierKey: String,  // E.164 정규화 또는 SMS sender ID
    val tagText: String,                     // 사용자 자유 텍스트
    val priority: TagPriority,
    val createdAt: Long,
    val lastSeenAt: Long?,
    val seenCount: Int = 0
)

enum class TagPriority {
    REMIND_ME,    // 다음에 보면 알려줘 (default)
    PENDING,      // 확인 필요
    SUSPICIOUS,   // 조심·의심
    ARCHIVE       // 보관·기록만
}
```

## 32-3. Tag 부여 흐름

### 32-3-1. 수신 후 부여

- CallCheck 화면에서 통화 항목 → "태그 추가"
- MessageCheck 발신자 → "태그 추가"
- PushCheck 휴지통 → "태그 추가"

### 32-3-2. 부재중 후 부여

- 부재중 알림 → "태그 추가" 바로가기

### 32-3-3. 일괄 라벨링

- Initial Scan 후 인벤토리 → 다중 선택 → 일괄 태그

## 32-4. Tag 매칭 시 동작 (Real-time Action 통합)

수신 이벤트 발생 시:

1. Layer 2 조회 → 태그 매칭 발견
2. RealTimeActionEngine 결정:
   - SUSPICIOUS → 무음 (Silent)
   - PENDING/REMIND_ME → 알림 우선순위 상향 + 태그 라벨 표시
   - ARCHIVE → 기록만, 알림 표시는 OS 기본
3. seenCount + lastSeenAt 갱신

## 32-5. UI

### 32-5-1. Tag 화면 (신규 진입점)

- 모든 태그 리스트 (priority별 그룹)
- 태그별 마지막 수신 시각·빈도
- 편집·삭제

### 32-5-2. 알림 통합

- 수신 알림 위에 태그 라벨 표시: `[확인 필요] 010-1234-5678`
- 태그 priority별 색상 코드

### 32-5-3. 일일 리마인드 (옵션)

- REMIND_ME 태그 중 7일 이상 미수신 → 일일 알림: "이 번호 아직 못 봤어요"
- 사용자 옵트인

## 32-6. 헌법 정합

| 조 | 정합 |
|---|---|
| §1 Out-Bound Zero | 디바이스 로컬, 외부 전송 0 |
| §2 In-Bound Zero | 사용자 입력 태그만 저장 |
| §3 결정 중앙집중 금지 | 사용자가 태그 직접 부여·해제 |
| §5 정직성 | "이 번호 태그 있음" 명시 |
| §6 가격 정직성 | (UI 영역 외) |

## 32-7. 모듈 매핑

- `:feature:tag-system` (신규)
  - repository/TagRepository.kt
  - service/TagMatcher.kt
  - ui/TagListScreen.kt + TagAddDialog.kt + TagDetailScreen.kt
  - di/TagModule.kt
- `:data:local-cache` (entity 추가, Room DB v15)

## 32-8. 사용자 대면 약속

> "태그는 연락처 저장과 다릅니다. 사용자가 직접 부여한 메모이며,
> 디바이스 안에서만 보관됩니다. 언제든 삭제할 수 있습니다."

## 32-9. cross-ref

- §31 Real-time Action (태그 매칭 시 알림)
- §30 InputAggregator (Layer 2 태그 조회)
