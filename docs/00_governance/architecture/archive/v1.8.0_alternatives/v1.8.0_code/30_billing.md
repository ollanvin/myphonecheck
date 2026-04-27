# 31. Billing 구현 세부 (Play Billing Library v7)

**원본 출처**: v1.7.1 §31 (249줄)
**v1.8.0 Layer**: Business
**의존**: `70_business/01_business_model.md`
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/30_billing.md`

---

# 31. Billing 구현 세부 (Play Billing Library v7)

## 31-1. BillingClient 구성

```kotlin
class BillingManager(
    private val context: Context,
    private val billingDao: BillingDao
) : PurchasesUpdatedListener {

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .enablePrepaidPlans()
                .build()
        )
        .build()

    private val productId = "myphonecheck_monthly"  // $2.49/월
    private val productDetails: StateFlow<ProductDetails?> = MutableStateFlow(null)

    suspend fun connect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    scope.launch {
                        queryProductDetails()
                        querySubscriptionStatus()
                    }
                }
            }
            override fun onBillingServiceDisconnected() {
                scope.launch { retryConnect() }
            }
        })
    }

    suspend fun queryProductDetails() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            ))
            .build()
        val result = billingClient.queryProductDetails(params)
        productDetails.value = result.productDetailsList?.firstOrNull()
    }

    suspend fun launchBillingFlow(activity: Activity) {
        val details = productDetails.value ?: return
        val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(details)
                    .setOfferToken(offerToken)
                    .build()
            ))
            .build()

        billingClient.launchBillingFlow(activity, params)
    }

    suspend fun querySubscriptionStatus(): SubscriptionStatus {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        val result = billingClient.queryPurchasesAsync(params)
        val activePurchase = result.purchasesList.firstOrNull {
            it.purchaseState == Purchase.PurchaseState.PURCHASED
        }
        if (activePurchase != null) {
            // 온디바이스 서명 검증 (자체 서버 없음 — 헌법 정합)
            val valid = verifySignatureLocally(activePurchase)
            if (valid) {
                billingDao.upsert(SubscriptionEntity(
                    purchaseToken = activePurchase.purchaseToken,
                    purchaseTimeMs = activePurchase.purchaseTime,
                    isAutoRenewing = activePurchase.isAutoRenewing,
                    lastVerifiedAt = System.currentTimeMillis()
                ))
                return SubscriptionStatus.Active
            }
        }
        return SubscriptionStatus.Inactive
    }

    override fun onPurchasesUpdated(
        result: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            purchases?.forEach { scope.launch { handlePurchase(it) } }
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (purchase.isAcknowledged) return

        val ackParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(ackParams)
        querySubscriptionStatus()
    }
}

enum class SubscriptionStatus { Active, Inactive, Pending }
```

## 31-2. 온디바이스 서명 검증

### 31-2-1. 1계층 — Play Billing 서명 검증 (기본)

```kotlin
fun verifySignatureLocally(purchase: Purchase): Boolean {
    // Play Billing이 제공하는 signedData와 signature를 Public Key로 검증
    val publicKey = BuildConfig.PLAY_PUBLIC_KEY  // Play Console에서 복사, 빌드 시 주입
    val keySpec = X509EncodedKeySpec(Base64.decode(publicKey, Base64.DEFAULT))
    val key = KeyFactory.getInstance("RSA").generatePublic(keySpec)

    val sig = Signature.getInstance("SHA1withRSA")
    sig.initVerify(key)
    sig.update(purchase.originalJson.toByteArray())
    return sig.verify(Base64.decode(purchase.signature, Base64.DEFAULT))
}
```

**자체 영수증 검증 서버 없음**. Play의 signedData·signature만으로 온디바이스 검증 (Infra_Ops v1.0 FINAL 정합).

**알고리즘 주의**: `SHA1withRSA`는 Google Play Billing Library 공식 `Security.java` 샘플 코드 기준이다. Google이 이 샘플을 유지하는 한 본 구현은 공식 권고 방식. 단, 이 1계층만으로는 루팅/Frida 메모리 변조 환경에서 우회 가능하므로 §31-2-2 Play Integrity API 2계층으로 보강한다 (Patch 38).

### 31-2-2. 2계층 — Play Integrity API `classicRequest` 로컬 무결성 검증 (Patch 38)

**목적**: 루팅·Frida·Magisk·에뮬레이터 환경에서 유료 기능 크랙 시도 탐지. 서버 0 원칙 유지.

**채택 근거**:
- 2차 외부 검증 라운드(2026-04-24) 스타크 Lane 5 지적: "2026년 현재 `SHA1withRSA` 단독 검증은 Frida 메모리 변조에 취약. Play Integrity API `classicRequest` 로컬 토큰 검증 로직 필수."
- Google Play Integrity API `classicRequest` 모드는 **서버 없이 로컬 검증 가능**. 헌법 1조 "스토어 공식 API 허용" 범위 내.
- 헌법 4조 "자가 작동"과 정합: 네트워크 단절 시 2계층 검증은 스킵되고 1계층만 작동 (fail-open, 사용자 가치 손실 없음).

**구현 스케치**:

```kotlin
class IntegrityVerifier(private val context: Context) {

    private val integrityManager = IntegrityManagerFactory.create(context)

    suspend fun verifyDeviceIntegrity(purchaseToken: String): IntegrityResult {
        val nonce = Base64.encodeToString(
            (purchaseToken + System.currentTimeMillis()).toByteArray(),
            Base64.URL_SAFE or Base64.NO_WRAP
        )

        return try {
            val request = IntegrityTokenRequest.builder()
                .setNonce(nonce)
                .build()

            val response = integrityManager.requestIntegrityToken(request).await()
            val token = response.token()

            // 로컬 토큰 파싱 (서버 검증 없음, payload만 읽음)
            val payload = parseIntegrityTokenLocally(token)

            when {
                payload.deviceIntegrity.contains("MEETS_BASIC_INTEGRITY") -> IntegrityResult.Valid
                payload.deviceIntegrity.contains("MEETS_DEVICE_INTEGRITY") -> IntegrityResult.Valid
                else -> IntegrityResult.Compromised(payload.deviceIntegrity)
            }
        } catch (e: IntegrityServiceException) {
            // 네트워크 단절·Google Play 미지원 디바이스 → 1계층만 신뢰 (헌법 4조)
            IntegrityResult.Unknown(e.errorCode)
        }
    }
}

sealed class IntegrityResult {
    object Valid : IntegrityResult()
    data class Compromised(val flags: List<String>) : IntegrityResult()
    data class Unknown(val errorCode: Int) : IntegrityResult()
}
```

**판정 정책**:

| 결과 | 구독 활성화 | 사용자 경험 |
|---|---|---|
| `Valid` | ✅ 활성 | 정상 Premium |
| `Compromised` | ❌ 거부 | "이 기기 환경에서는 구독을 활성화할 수 없습니다. 정상 환경에서 재시도해 주세요." |
| `Unknown` (네트워크 단절·미지원) | ✅ 활성 (1계층 통과 시) | 정상 Premium (헌법 4조 fail-open) |

**서버 0 보증**: `classicRequest`는 Google Play Services에서 반환된 토큰을 **로컬에서 직접 파싱**한다. 자체 서버로 토큰을 보내 검증받는 `standardRequest` 모드는 사용하지 않음. 이 구분이 헌법 1조 정합의 핵심이다.

**방어 범위 (스타크 지적 대응)**:
- ✅ 루팅 탐지 (`MEETS_BASIC_INTEGRITY` 실패)
- ✅ 커스텀 ROM 탐지
- ✅ Frida Gadget 주입 탐지 (Google Play Protect 신호 연계)
- ✅ 에뮬레이터 탐지
- ❌ 런타임 메모리 후크 (완전 방어 불가, 이건 서버 측 재검증 없이는 근본 해결 안 됨 — 헌법 1조와의 트레이드오프)

### 31-2-3. 2계층 추가 권한 (Patch 38)

Play Integrity API 호출을 위해 **런타임 추가 권한 없음**. `com.google.android.gms:play-services-integrity` Gradle 의존성만 추가. Manifest 변경 없음.

## 31-3. SubscriptionEntity

```kotlin
@Entity(tableName = "subscription")
data class SubscriptionEntity(
    @PrimaryKey val purchaseToken: String,
    val purchaseTimeMs: Long,
    val isAutoRenewing: Boolean,
    val lastVerifiedAt: Long
)
```

## 31-4. 구독 상태 UI

- 활성 구독: "Premium 사용 중 · 2026-05-24 갱신" 표시
- 미구독: "$2.49/월 — 모든 Surface 활성화" + "구독" 버튼
- 만료 예정: "7일 후 만료 — 갱신 설정 확인" 안내

## 31-5. iOS 진입 시 (v2.0.0 예정)

StoreKit 2 사용. RevenueCat 미채택 (메모리 #20).

```swift
// 예정 스케치
let products = try await Product.products(for: ["myphonecheck_monthly"])
let result = try await product.purchase()
switch result {
case .success(let verification):
    let transaction = try checkVerified(verification)
    await transaction.finish()
    // 온디바이스 검증만, 서버 없음
case .pending, .userCancelled:
    break
}
```

---
