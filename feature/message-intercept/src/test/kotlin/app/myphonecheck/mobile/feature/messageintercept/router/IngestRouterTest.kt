package app.myphonecheck.mobile.feature.messageintercept.router

import app.myphonecheck.mobile.core.globalengine.parsing.currency.TransactionSource
import app.myphonecheck.mobile.feature.cardcheck.repository.CardTransactionRepository
import app.myphonecheck.mobile.feature.messagecheck.data.MessageHubRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class IngestRouterTest {

    private lateinit var messageHub: MessageHubRepository
    private lateinit var cardTx: CardTransactionRepository
    private lateinit var router: IngestRouter

    @Before
    fun setup() {
        messageHub = mockk(relaxed = true)
        cardTx = mockk(relaxed = true)
        coEvery { cardTx.ingest(any(), any(), any(), any()) } returns 1L
        router = IngestRouter(messageHub, cardTx)
    }

    @Test
    fun `routeSms calls both MessageHub saveSms and CardTransaction ingest`() = runBlocking {
        router.routeSms("01012345678", "click https://example.com", 1000L)

        coVerify {
            messageHub.saveSms(
                senderNumber = "01012345678",
                content = "click https://example.com",
                receivedAt = 1000L,
            )
        }
        coVerify {
            cardTx.ingest(
                sourceId = "01012345678",
                body = "click https://example.com",
                receivedAt = 1000L,
                source = TransactionSource.SMS,
            )
        }
    }

    @Test
    fun `routeNotification calls both with combined title and body`() = runBlocking {
        router.routeNotification(
            packageName = "com.kbcard.cxh.appcard",
            senderLabel = "KB",
            title = "payment alert",
            body = "10000 KRW used",
            receivedAt = 2000L,
        )

        coVerify {
            messageHub.saveNotification(
                packageName = "com.kbcard.cxh.appcard",
                senderLabel = "KB",
                content = "payment alert\n10000 KRW used",
                receivedAt = 2000L,
            )
        }
        coVerify {
            cardTx.ingest(
                sourceId = "com.kbcard.cxh.appcard",
                body = "payment alert 10000 KRW used",
                receivedAt = 2000L,
                source = TransactionSource.NOTIFICATION,
            )
        }
    }

    @Test
    fun `routeNotification with null title still calls both with body only`() = runBlocking {
        router.routeNotification(
            packageName = "p",
            senderLabel = null,
            title = null,
            body = "body only",
            receivedAt = 100L,
        )

        coVerify {
            messageHub.saveNotification(
                packageName = "p",
                senderLabel = null,
                content = "body only",
                receivedAt = 100L,
            )
        }
        coVerify {
            cardTx.ingest(
                sourceId = "p",
                body = "body only",
                receivedAt = 100L,
                source = TransactionSource.NOTIFICATION,
            )
        }
    }

    @Test
    fun `routeNotification with null body uses title only`() = runBlocking {
        router.routeNotification(
            packageName = "p",
            senderLabel = "label",
            title = "title only",
            body = null,
            receivedAt = 200L,
        )

        coVerify {
            messageHub.saveNotification(
                packageName = "p",
                senderLabel = "label",
                content = "title only",
                receivedAt = 200L,
            )
        }
        coVerify {
            cardTx.ingest(
                sourceId = "p",
                body = "title only",
                receivedAt = 200L,
                source = TransactionSource.NOTIFICATION,
            )
        }
    }

    @Test
    fun `routeNotification with both null produces empty content`() = runBlocking {
        router.routeNotification(
            packageName = "p",
            senderLabel = null,
            title = null,
            body = null,
            receivedAt = 300L,
        )

        coVerify {
            messageHub.saveNotification(
                packageName = "p",
                senderLabel = null,
                content = "",
                receivedAt = 300L,
            )
        }
        coVerify {
            cardTx.ingest(
                sourceId = "p",
                body = "",
                receivedAt = 300L,
                source = TransactionSource.NOTIFICATION,
            )
        }
    }
}
