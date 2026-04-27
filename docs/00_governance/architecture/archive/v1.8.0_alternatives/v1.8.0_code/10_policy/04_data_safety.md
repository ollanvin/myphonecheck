# 27. Google Play Data Safety + Permissions Declaration (Data Safety 부분)

**원본 출처**: v1.7.1 §27 H1 + §27-1 + §27-2 + §27-4 + §27-5
**v1.8.0 Layer**: Policy
**의존**: `05_constitution.md` (제1조 Out-Bound Zero, 제2조 In-Bound Zero)
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/10_policy/04_data_safety.md`

---

# 27. Google Play Data Safety + Permissions Declaration (Patch 32·33·34)

**본 장은 Patch 32·33·34로 전면 재작성되었다**. 사유:
- **Patch 32**: v1.6.1-patch 시점의 "데이터 수집 없음" 선언이 실제 SMS·통화·앱 읽기 동작과 모순됨을 자비스 Lane 4가 지적 (2026-04-24). Play Console Data Safety 기준에서 "디바이스 내부 접근(on-device access)"도 넓은 의미의 "collection"에 포함될 수 있으므로, **정직한 분류로 전면 재선언**한다.
- **Patch 33**: Permissions Declaration 본문을 신설. 각 민감 권한별로 `core user benefit` + `less-invasive alternative 검토 결과` + `사용자 고지 방식` 3요소를 명시하여 Play 심사 통과 자격을 갖춘다.
- **Patch 34**: NKB at-rest 암호화(§27-5) 신설. Lane 1 D06이 지적한 "v1.0에서 DONE이었던 SQLCipher/AES-256-GCM이 v1.6.1에서 실종"을 복원 (코웍 87a9a3 §8-0 흡수).

헌법 제1·2·5조(정직성) 정면 구현.

## 27-1. Data Safety 분류 — 정직 재선언 (Patch 32)

### 27-1-1. Google Play 기준 용어 정리

- **Data Collection**: 앱이 사용자 데이터에 접근하는 모든 경우. 디바이스 내부 처리만이어도 해당 가능.
- **Data Sharing**: 앱이 사용자 데이터를 제3자에게 전송·전달하는 경우.
- **Processed ephemerally**: 메모리만 통과 후 즉시 폐기하는 처리.

본 앱은 **Data Collection에 해당하되, Data Sharing은 전무**하다는 것이 정확한 선언이다.

### 27-1-2. 수집 분류 (What's collected)

| 데이터 유형 | 접근 여부 | 용도 | 저장 여부 | Sharing |
|---|---|---|---|---|
| **이름·이메일·주소** | 접근 안 함 | — | — | — |
| **전화번호 (사용자 본인)** | 접근 안 함 | — | — | — |
| **통화 이력 (발신·수신 번호·시각)** | ✅ 접근 | CallCheck: 스팸 판별 + NKB 갱신 | 디바이스 Room DB (featureCounts만, 원문 X) | ❌ 공유 없음 |
| **문자 이력 (Mode A 전용)** | 조건부 접근 (사용자가 Default SMS 지정 시) | MessageCheck Mode A: 자동 사칭 감지 | 디바이스 내부, Room DB 해시값만 | ❌ 공유 없음 |
| **문자 본문 (Mode B)** | 사용자 공유 시점만 | MessageCheck Mode B: Share Intent 수동 분석 | 메모리만, 폐기 | ❌ 공유 없음 |
| **연락처 (이름·번호)** | ✅ 접근 | Cold Start: 저장 번호 SAFE 초기화 | 디바이스 내부만 | ❌ 공유 없음 |
| **설치된 앱 목록 (RECORD_AUDIO/CAMERA 필터)** | ✅ 접근 | MicCheck·CameraCheck: 권한 보유 앱 리스트 | 세션 내 메모리, 저장 없음 | ❌ 공유 없음 |
| **앱 사용 통계 (PACKAGE_USAGE_STATS)** | ✅ 접근 (사용자 수동 승인) | MicCheck·CameraCheck: 최근 사용 시각 표시 | 세션 내 메모리 | ❌ 공유 없음 |
| **금융 정보 (결제)** | 접근 안 함 | Google Play가 직접 처리 | — | ❌ 공유 없음 |
| **위치** | 접근 안 함 | — | — | — |
| **사진·동영상·오디오 파일** | 접근 안 함 | — | — | — |
| **외부 검색 결과 스니펫 (3계층 소싱)** | 메모리 통과 | SearchResultAnalyzer 분석 | **즉시 폐기 (헌법 2조)** | ❌ 공유 없음 |
| **Crashlytics 진단 데이터** | 선택적 수집 (옵트아웃 기본 제공) | 안정성 모니터링 | Firebase 서버 | ❌ 광고·분석 공유 없음 |

### 27-1-3. Play Console Data Safety 체크박스 매핑

| Play Console 섹션 | 선언값 |
|---|---|
| Data collected: "Yes, this app collects user data" | ✅ 예 (정직 선언) |
| Data shared with third parties: "No" | ❌ 없음 |
| Data encrypted in transit | ✅ HTTPS만 (INTERNET 권한) |
| **Data encrypted at rest** | ✅ **SQLCipher AES-256-GCM** (§27-5) |
| Data deletion request mechanism | ✅ 앱 내 "모든 데이터 삭제" (§27-4) |
| Play Families Policy applicable | N/A (전 연령) |

**중요**: "Processing only on device" 플래그를 **활용 가능**. 각 데이터 항목별로 "Data is processed ephemerally" 또는 "Processed only on device"를 정직하게 마킹한다. 이는 "collection 자체를 부인"하는 것과 다르며, 헌법 5조(정직성)와 완전 정합한다.

## 27-2. Data Sharing — 공유 없음 (변경 없음)

- 제3자 공유 0 (광고 네트워크·분석 서비스·재판매 전부 없음)
- Firebase Analytics 미사용 (Crashlytics 진단만 사용, 광고 ID 제외)
- GDPR·CCPA·PIPA 정합: 사용자 데이터가 디바이스 경계 밖으로 나가지 않음
## 27-4. 삭제 요청 처리

- **사용자 데이터 삭제 경로**: 앱 설정 → "모든 데이터 삭제" 버튼 → NKB·UserAction·ClusterProfile 전부 Room DB 초기화 + 앱 재시작
- **자동 삭제**: 365일 경과 archive 엔트리 정리 (§12, §30-2)
- **계정 개념 없음** → 외부 삭제 요청 API 불필요 (헌법 1조 정합)
- **GDPR "Right to Erasure"** 정합: 사용자 데이터가 디바이스에만 존재하므로, 앱 삭제 또는 위 버튼 탭으로 완결

## 27-5. NKB At-Rest 암호화 (Patch 34 신설 — Lane 1 D06 복원, 코웍 87a9a3 §8-0 흡수)

NKB는 사용자의 통화·문자 판정 이력(번호 식별자·featureCounts·카테고리 분포·사용자 행동)을 포함한다. 디바이스 분실·악성 앱 로컬 접근 등을 방지하기 위해 **at-rest 암호화가 필수**이다.

### 27-5-1. 기술 스택

- **SQLCipher**: Room Database 전체를 **AES-256-GCM으로 암호화**. 평문 DB 파일이 디스크에 존재하지 않는다.
- **Android Keystore**: SQLCipher passphrase를 **TEE/StrongBox 하드웨어 키**로 암호화하여 SharedPreferences에 저장. 키 추출 불가 (하드웨어 백업).
- **키 생성 시점**: 앱 최초 실행 시 `KeyGenerator.getInstance("AES", "AndroidKeyStore")`로 256-bit 키 생성. 이후 재설치 전까지 동일 키 유지.
- **키 인증 요구**: `setUserAuthenticationRequired(false)` (앱 상시 사용), `setIsStrongBoxBacked(true)` (가능한 기기 한정).

### 27-5-2. Migration 정책

- v1.x 기존 평문 Room DB → v2.0 암호화 DB 마이그레이션 경로 필수 (`RoomDatabase.Migration`).
- Passphrase는 앱 메모리에만 존재. Logcat·Crashlytics·파일에 절대 출력 금지.

### 27-5-3. Room 연결 코드 (참조 구현)

```kotlin
class NkbDatabaseFactory(
    private val context: Context,
    private val keystoreManager: KeystoreManager
) {
    fun create(): NkbDatabase {
        val passphrase: ByteArray = keystoreManager.getOrCreatePassphrase()
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(context, NkbDatabase::class.java, "nkb.db")
            .openHelperFactory(factory)
            .addMigrations(MIGRATION_1_2_ENCRYPT)
            .build()
    }
}

class KeystoreManager(private val context: Context) {
    fun getOrCreatePassphrase(): ByteArray {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val alias = "nkb_master_key"

        if (!keyStore.containsAlias(alias)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
            val spec = KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                setKeySize(256)
                setUserAuthenticationRequired(false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setIsStrongBoxBacked(true)
                }
            }.build()
            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }

        // passphrase 파생 (예: HMAC(master_key, salt))
        return derivePassphraseFromMasterKey(alias)
    }
}
```

### 27-5-4. 헌법 정합

- **헌법 1조 (Out-Bound Zero)**: passphrase·키 모두 외부 전송 0, 디바이스 내부만.
- **헌법 2조 (In-Bound Zero)**: 외부 원문은 여전히 저장 금지 (§6-3). 본 암호화는 저장되는 featureCounts·메타 데이터를 보호할 뿐, 저장 대상 자체는 변경 없음.
- **헌법 5조 (정직성)**: Data Safety에서 "Data encrypted at rest" 체크박스 사실대로 ✅ 선언 가능.

### 27-5-5. 배터리·성능 영향

- SQLCipher 암복호화 오버헤드: 쿼리당 평균 1~3% CPU 증가
- NKB Hit p95 ≤ 5ms 목표(§14-2)에 영향 없음 (측정 검증 필요, Phase 1 실기기 테스트)
- 첫 DB 오픈 시 키스토어 접근으로 100~300ms 지연 → 앱 시작 시 **Cold Start Phase 0에서 미리 초기화** (§11-1에 추가)

---
