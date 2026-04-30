package app.myphonecheck.mobile.feature.messageintercept.router

import app.myphonecheck.mobile.core.globalengine.parsing.currency.TransactionSource
import app.myphonecheck.mobile.feature.cardcheck.repository.CardTransactionRepository
import app.myphonecheck.mobile.feature.messagecheck.data.MessageHubRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WO-INGEST-WIRING-001 SMS/Push 인입 단일 라우터.
 *
 * 책임:
 *   - SMS/Push 본문을 MessageHub (PR #66) 와 CardTransaction (Stage 2-001) 양쪽으로 분기
 *   - 한 인입 = 두 영역 동시 처리 (단일 책임 위배 아님 — 라우팅이 단일 책임)
 *
 * 헌법 정합:
 *   - §1 §2: 온디바이스 only, 외부 송신 0
 *   - §3 §11: 자동 차단 0 (분류·저장만)
 *   - §7: One Engine, N Surfaces
 *   - §9-4: 카드사·국가 분기 0 (코어 엔진 의존)
 */
@Singleton
class IngestRouter @Inject constructor(
    private val messageHubRepository: MessageHubRepository,
    private val cardTransactionRepository: CardTransactionRepository,
) {

    suspend fun routeSms(senderNumber: String, body: String, receivedAt: Long) {
        messageHubRepository.saveSms(
            senderNumber = senderNumber,
            content = body,
            receivedAt = receivedAt,
        )
        cardTransactionRepository.ingest(
            sourceId = senderNumber,
            body = body,
            receivedAt = receivedAt,
            source = TransactionSource.SMS,
        )
    }

    suspend fun routeNotification(
        packageName: String,
        senderLabel: String?,
        title: String?,
        body: String?,
        receivedAt: Long,
    ) {
        val hubContent = listOfNotNull(title, body).joinToString("\n")
        messageHubRepository.saveNotification(
            packageName = packageName,
            senderLabel = senderLabel,
            content = hubContent,
            receivedAt = receivedAt,
        )
        val cardBody = listOfNotNull(title, body).joinToString(" ")
        cardTransactionRepository.ingest(
            sourceId = packageName,
            body = cardBody,
            receivedAt = receivedAt,
            source = TransactionSource.NOTIFICATION,
        )
    }
}
