# 워크오더: Stage 1 — 푸시 휴지통 구현 (WO-STAGE1-001)

**대상 도구**: Cursor (Windows 로컬 IDE)
**작업 위치**: `C:\Users\user\Dev\ollanvin\myphonecheck`
**발행자**: 비전
**발행일**: 2026-04-22
**역할**: 구현자

---

## 0. 전제 조건

- [ ] WO-GOV-005 (헌법 패치 v1.7) 완료 — PushCheck 재정의가 헌법에 반영됨
- [ ] HEAD == origin/main, 작업 트리 clean

---

## 1. 작업 목적

헌법 v1.7 Patch 31 "푸시 휴지통" 모델을 구현한다.

**최종 사용자 경험**:
- 쿠팡 같은 대형 앱은 Android Notification Channel ID 기반으로 알림 종류(주문상태/프로모션 등)를 체크박스로 관리
- 채널 없는 중소형 앱은 앱 단위 허용/차단
- 차단 알림은 시스템에 일체 표시 안 되고 MyPhoneCheck 내부 "푸시 휴지통"에만 쌓임
- 사용자는 휴지통을 열어 복원/삭제 가능

**서버 의존성**: 0. 디바이스 완결.

**Stage 1 완료 범위** (본 워크오더):
1. `NotificationListenerService` 권한 요청 UX
2. 알림 수신 시 Room DB에 수집
3. 앱·채널 단위 차단 설정 UI
4. 휴지통 화면 (복원/삭제)
5. 대형 앱 채널 매핑 테이블 (최소 5개 앱 수작업 등록)

**Stage 1 비범위** (Stage 2+로 연기):
- 키워드 학습 보조 분류
- 알림 카테고리 자동 추천
- 매핑 테이블 확장 (상위 30~50개)

---

## 2. 모듈 구조

### 2-1. 신규 모듈: `:feature:push-trash`

`settings.gradle.kts`에 `include(":feature:push-trash")` 추가.

**중요**: 기존 `feature/push-intercept/` 디렉터리와 혼동 금지. 이건 이미 헌법상 disabled + 디렉터리 삭제 대상. 본 Stage 1에서 `push-intercept` 디렉터리도 함께 정리할 것.

### 2-2. 디렉터리 구조

```
feature/push-trash/
├── build.gradle.kts
└── src/main/kotlin/app/myphonecheck/mobile/feature/pushtrash/
    ├── service/
    │   └── PushTrashNotificationListener.kt  # NotificationListenerService
    ├── repository/
    │   └── PushTrashRepository.kt
    ├── ui/
    │   ├── PushTrashScreen.kt                # 메인 화면 (앱 리스트 + 설정)
    │   ├── PushTrashBinScreen.kt             # 휴지통 화면
    │   └── AppBlockSettingsScreen.kt         # 앱별 채널/블록 설정
    ├── viewmodel/
    │   ├── PushTrashViewModel.kt
    │   └── PushTrashBinViewModel.kt
    └── mapping/
        └── ChannelLabelMapper.kt             # 채널 ID → 한글 라벨 내장 매핑
```

### 2-3. Room 스키마 추가

`:data:local-cache`에 3개 엔티티 + DAO 추가.

**엔티티 1**: `BlockedChannelEntity`
```kotlin
@Entity(tableName = "blocked_channels", primaryKeys = ["packageName", "channelId"])
data class BlockedChannelEntity(
    val packageName: String,
    val channelId: String,
    val blockedAt: Long
)
```

**엔티티 2**: `BlockedAppEntity`
```kotlin
@Entity(tableName = "blocked_apps")
data class BlockedAppEntity(
    @PrimaryKey val packageName: String,
    val mode: String,  // "all_blocked" | "all_allowed"
    val blockedAt: Long
)
```

**엔티티 3**: `TrashedNotificationEntity`
```kotlin
@Entity(tableName = "trashed_notifications")
data class TrashedNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val channelId: String?,
    val title: String?,
    val text: String?,
    val iconResId: Int?,
    val postedAt: Long,
    val capturedAt: Long
)
```

### 2-4. 서비스 등록

`app/src/main/AndroidManifest.xml`에 추가:
```xml
<service
    android:name="app.myphonecheck.mobile.feature.pushtrash.service.PushTrashNotificationListener"
    android:label="@string/push_trash_listener_label"
    android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
    android:exported="false">
    <intent-filter>
        <action android:name="android.service.notification.NotificationListenerService" />
    </intent-filter>
</service>
```

### 2-5. 기존 feature/push-intercept 삭제

```powershell
Remove-Item -Path feature/push-intercept -Recurse -Force
```
`settings.gradle.kts`에서 혹시 include 돼 있으면 삭제.

---

## 3. 핵심 구현 스펙

### 3-1. `PushTrashNotificationListener`

```kotlin
@AndroidEntryPoint
class PushTrashNotificationListener : NotificationListenerService() {

    @Inject lateinit var repository: PushTrashRepository

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val pkg = sbn.packageName
        val channelId = sbn.notification.channelId
        
        // 0. 우리 앱 자신의 알림은 무시
        if (pkg == applicationContext.packageName) return

        serviceScope.launch {
            val decision = repository.decide(pkg, channelId)
            when (decision) {
                Decision.ALLOW -> {
                    // 그대로 통과 (cancelNotification 호출 안 함)
                }
                Decision.BLOCK -> {
                    // Room에 저장 후 시스템 알림 취소
                    repository.recordTrashed(sbn)
                    cancelNotification(sbn.key)
                }
            }
        }
    }
    
    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Stage 1에서는 무시. Stage 2+에서 휴지통 동기화 검토
    }
}
```

**기술 검증 필수 (Stage 1 초기)**:
- `onNotificationPosted`가 **시스템 표시 전**에 호출되는가?
- `cancelNotification()`이 시스템 알림을 **완전히** 제거하는가? (소리·진동·락스크린 포함)
- Android 버전·제조사별 차이 기록

검증 결과를 `docs/07_relay/done/TECH-VERIFICATION-NLS.md`에 문서화.

### 3-2. `PushTrashRepository`

```kotlin
class PushTrashRepository @Inject constructor(
    private val blockedChannelDao: BlockedChannelDao,
    private val blockedAppDao: BlockedAppDao,
    private val trashedNotificationDao: TrashedNotificationDao,
) {
    sealed class Decision {
        object ALLOW : Decision()
        object BLOCK : Decision()
    }

    suspend fun decide(pkg: String, channelId: String?): Decision {
        // 1순위: 앱 단위 "all_blocked"면 차단
        val appBlock = blockedAppDao.find(pkg)
        if (appBlock?.mode == "all_blocked") return Decision.BLOCK
        
        // 2순위: 채널 단위 차단 리스트에 있으면 차단
        if (channelId != null && blockedChannelDao.isBlocked(pkg, channelId)) {
            return Decision.BLOCK
        }
        
        // 기본: 허용
        return Decision.ALLOW
    }

    suspend fun recordTrashed(sbn: StatusBarNotification) {
        trashedNotificationDao.insert(
            TrashedNotificationEntity(
                packageName = sbn.packageName,
                channelId = sbn.notification.channelId,
                title = sbn.notification.extras.getString(Notification.EXTRA_TITLE),
                text = sbn.notification.extras.getString(Notification.EXTRA_TEXT),
                iconResId = sbn.notification.smallIcon?.resId,
                postedAt = sbn.postTime,
                capturedAt = System.currentTimeMillis(),
            )
        )
    }
}
```

### 3-3. `ChannelLabelMapper` (최소 5개 앱)

```kotlin
object ChannelLabelMapper {
    private val map: Map<Pair<String, String>, Int> = mapOf(
        // 쿠팡
        "com.coupang.mobile" to "order_status" to R.string.channel_coupang_order,
        "com.coupang.mobile" to "delivery" to R.string.channel_coupang_delivery,
        "com.coupang.mobile" to "promotion" to R.string.channel_coupang_promo,
        // 배민
        "com.sampleapp.baemin" to "order" to R.string.channel_baemin_order,
        "com.sampleapp.baemin" to "promotion" to R.string.channel_baemin_promo,
        // ... 최소 5개 앱
    )

    fun label(context: Context, pkg: String, channelId: String): String {
        return map[pkg to channelId]?.let { context.getString(it) } ?: channelId
    }
}
```

**주의**: 채널 ID는 앱 개발자가 임의로 정하므로 **실기기 테스트로 확인 후** 매핑 테이블 채워야 함. 가짜 ID 넣지 말 것.

### 3-4. `PushTrashScreen` (UX)

```
┌──────────────────────────────────┐
│ 푸시 휴지통                         │
├──────────────────────────────────┤
│ [알림 접근 권한 활성화 필요]          │
│ [권한 설정 열기]                    │
├──────────────────────────────────┤
│ 지난 7일 수집: 0건                   │
│ 차단 규칙: 0건                       │
│                                   │
│ [휴지통 보기]  [앱별 설정]           │
└──────────────────────────────────┘
```

권한 없으면 상단에 안내. 권한 있으면 최근 알림 수집 통계 + 휴지통/설정 진입 버튼.

### 3-5. `AppBlockSettingsScreen`

설치된 앱 중 **최근 7일 내 알림 보낸 앱**만 리스트로 표시 (PackageManager 전수가 아닌 우리가 관찰한 앱만).

각 앱 카드:
- 앱 이름 + 아이콘
- 최근 7일 알림 건수
- **채널 리스트 (관찰된 channelId만)**: 체크박스로 허용/차단
- 채널이 0개면: "전체 허용 / 전체 차단" 라디오 버튼 + 안내 "이 앱은 알림을 세부 분류하지 않습니다"

### 3-6. `PushTrashBinScreen` (휴지통)

```
┌──────────────────────────────────┐
│ 푸시 휴지통 (14건)                   │
├──────────────────────────────────┤
│ [쿠팡 아이콘] 스프링 세일 40% 할인      │
│ 2026-04-22 14:23                 │
│ [복원]  [삭제]                      │
├──────────────────────────────────┤
│ ...                               │
└──────────────────────────────────┘
```

- 시간 역순 정렬
- 복원 버튼: 해당 알림 규칙 해제 + 시스템 알림 재발행 (또는 사용자에게 "다음 알림부터 복원" 안내)
- 삭제 버튼: Room에서 삭제

---

## 4. 문자열 리소스 (하드코딩 금지 — 메모리 #1)

모든 사용자 노출 문자열은 `feature/push-trash/src/main/res/values/strings.xml` + 각 언어별 `values-XX/` 에 등록.

최소 한국어(ko) + 영어(en). 나머지 언어는 `values-XX/strings.xml`에 복사해 두고 임시로 영어 유지.

---

## 5. 테스트

### 5-1. JVM 단위 테스트 (`src/test/kotlin`)

- `PushTrashRepositoryTest`: decide 분기 로직 단위 테스트
- `ChannelLabelMapperTest`: 매핑 존재/부재 케이스
- Room DAO 테스트 (실제 in-memory Room)

### 5-2. 수동 실기기 테스트 (필수)

실기기 테스트 절차를 `docs/05_quality/stage1_push_trash_manual_test.md`에 문서화:
1. 권한 허용 플로우
2. 쿠팡/배민 등 대형 앱 알림 수신 → 채널 ID 로그 확인
3. 채널 차단 설정 후 알림 재발송 → 시스템 표시 안 되는지 확인
4. 휴지통에 보관되는지 확인
5. 복원 플로우

### 5-3. 감사 위임

- `:feature:push-trash` 모듈 테스트 통과가 Stage 1 PASS의 필수 조건
- 실기기 테스트 결과 스크린샷/로그를 보고서에 포함

---

## 6. 작업 단계별 커밋 (의미 단위)

Stage 1 전체를 한 커밋으로 하지 말고 의미 단위로 분리:

1. `feat(push-trash): scaffold :feature:push-trash module`
2. `feat(push-trash): add Room entities and DAOs for push trash`
3. `feat(push-trash): implement NotificationListenerService with decide logic`
4. `feat(push-trash): add channel label mapping (5 apps)`
5. `feat(push-trash): implement UI screens (main, settings, bin)`
6. `chore(push-trash): remove stale feature/push-intercept directory`
7. `test(push-trash): add unit tests`
8. `docs(push-trash): add manual test procedure`

각 커밋 후 push (가능하면). 마지막 커밋에서 `Refs: WO-STAGE1-001` 명시.

---

## 7. 보고서

파일: `docs/07_relay/done/REPORT-WO-STAGE1-001__cursor__done.md`

포함:
- 추가된 모듈·파일 목록
- Room 스키마 변경 요약
- 실기기 테스트 결과 (Android 버전, 제조사별)
- 기술 검증 결과 (`onNotificationPosted` 타이밍, `cancelNotification` 완전성)
- 매핑 테이블에 등록된 앱·채널 목록
- 알려진 한계·리스크
- Stage 2 권고 (매핑 확장, 키워드 학습 등)

---

## 8. 제약 사항

- **알림 본문 파싱 금지**. 메타데이터(channelId, category)만 사용
- **서버 인프라 0**
- **UI 문자열 하드코딩 금지** (strings.xml만)
- **core:common 변경 금지** (FREEZE)
- **기존 push-intercept 디렉터리 정리는 본 Stage에 포함**
- **매핑 테이블은 실기기에서 확인한 실제 채널 ID만** (추정 금지)

---

## 9. 실패 시 대응

| 지점 | 대응 |
|---|---|
| `onNotificationPosted` 시스템 표시 후 호출 (타이밍 문제) | 중단, 대안 검토 후 비전·대표님 보고 |
| `cancelNotification` 소리·진동 못 막음 | Android 버전별 워크어라운드 조사, 불가 시 한계 명시 |
| 대형 앱 실제 채널 ID 접근 불가 | 해당 앱만 "중소형 앱 단위 처리"로 분류 |

---

## 10. 완료 후 다음 단계

비전이 Stage 1 결과 정독 → Claude Code에 감사 워크오더(WO-AUDIT-STAGE1-001) 발행 → PASS 시 Stage 2 후보 선정.

---

## 끝
