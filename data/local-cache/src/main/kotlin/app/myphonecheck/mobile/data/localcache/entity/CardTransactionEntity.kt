package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * CardCheck 결제 거래 엔티티 (Architecture v1.9.0 §27-4).
 *
 * 글로벌 파싱 엔진 출력. 카드사·국가 분기 코드 0.
 *
 * 헌법 정합:
 *  - 2조 In-Bound Zero: 원문 SMS/Push는 메모리 처리 후 폐기. 본 엔티티에는 추출 필드만 저장.
 *  - 6조 Pricing Honesty: amount는 측정값 그대로 (가공·예측·환산 0).
 */
@Entity(
    tableName = "card_transaction",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["currencyCode"]),
        Index(value = ["sourceId"]),
    ],
)
data class CardTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceId: String,            // SMS 발신자 ID 또는 Notification package
    val sourceLabel: String,         // 사용자 정의 라벨 (라벨 미지정 시 sourceId)
    val cardIdentifier: String?,     // 카드 끝자리 또는 카드명 (nullable)
    val amount: Long,                // 통화별 최소 단위 정수 (cents, won, fils, ...)
    val currencyCode: String,        // ISO 4217 (USD, EUR, KRW, JPY, BHD, ...)
    val timestamp: Long,             // 거래 epoch ms (본문 timestamp 또는 SMS 수신 시각)
    val merchantName: String?,       // 가맹점명 (nullable)
    val source: String,              // "SMS" | "NOTIFICATION"
    val confidence: String,          // "HIGH" | "MEDIUM" | "LOW"
)
