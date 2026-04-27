package app.myphonecheck.mobile.feature.cardcheck.repository

import app.myphonecheck.mobile.data.localcache.dao.CardTransactionDao
import app.myphonecheck.mobile.data.localcache.dao.CardTransactionMonthlyTotal
import app.myphonecheck.mobile.data.localcache.entity.CardTransactionEntity
import app.myphonecheck.mobile.feature.cardcheck.learning.SourceLabelCache
import app.myphonecheck.mobile.feature.cardcheck.parser.CardParseResult
import app.myphonecheck.mobile.feature.cardcheck.parser.Confidence
import app.myphonecheck.mobile.feature.cardcheck.parser.PatternExtractor
import app.myphonecheck.mobile.feature.cardcheck.parser.TransactionSource
import app.myphonecheck.mobile.feature.cardcheck.parser.Validator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CardCheck 거래 저장소 (Architecture v1.9.0 §27).
 *
 * 글로벌 파싱 엔진 결과를 Room DB에 저장 + 월별·통화별·소스별 집계 제공.
 *
 * 헌법 정합:
 *  - 1조 Out-Bound Zero: 모든 처리 디바이스 로컬.
 *  - 2조 In-Bound Zero: 원문 SMS/Push는 메모리 처리 후 폐기, 추출 필드만 저장.
 *  - 3조 결정권 중앙집중 금지: 사용자 라벨링 주도.
 *  - 6조 가격 정직성: amount는 측정값 그대로 (가공·예측·환산 0).
 */
@Singleton
class CardTransactionRepository @Inject constructor(
    private val transactionDao: CardTransactionDao,
    private val labelCache: SourceLabelCache,
    private val extractor: PatternExtractor,
    private val validator: Validator,
) {

    /**
     * SMS 또는 Push 본문을 파싱하여 거래로 저장. Known 발신자에 한정.
     *
     * @return 저장된 거래의 row id, 또는 amount 추출 실패 시 null
     */
    suspend fun ingest(
        sourceId: String,
        body: String,
        receivedAt: Long,
        source: TransactionSource,
    ): Long? {
        val sourceLabel = labelCache.find(sourceId) ?: return null

        val amount = extractor.extractCurrencyAmount(body) ?: return null
        val timestamp = extractor.extractTimestamp(body, receivedAt)
        val cardId = extractor.extractCardIdentifier(body)
        val merchant = extractor.extractMerchant(body)

        val parseResult = CardParseResult(
            sourceId = sourceId,
            amount = amount,
            timestamp = timestamp,
            cardIdentifier = cardId,
            merchant = merchant,
            source = source,
        )
        val confidence = validator.validate(parseResult)

        val entity = CardTransactionEntity(
            sourceId = sourceId,
            sourceLabel = sourceLabel,
            cardIdentifier = cardId,
            amount = amount.minorUnits,
            currencyCode = amount.currencyCode,
            timestamp = timestamp,
            merchantName = merchant,
            source = source.name,
            confidence = confidence.name,
        )
        return transactionDao.insert(entity)
    }

    fun observeAll(): Flow<List<CardTransactionEntity>> = transactionDao.observeAll()

    fun observeInRange(startMillis: Long, endMillis: Long): Flow<List<CardTransactionEntity>> =
        transactionDao.observeInRange(startMillis, endMillis)

    /**
     * 월별·통화별·소스별 집계.
     *
     * @param startMillis 월 시작 epoch ms (디바이스 timezone 기준)
     * @param endMillis 월 종료 epoch ms
     * @param includeLow LOW confidence 거래 포함 여부
     */
    fun observeMonthlyTotals(
        startMillis: Long,
        endMillis: Long,
        includeLow: Boolean = false,
    ): Flow<List<CardTransactionMonthlyTotal>> =
        transactionDao.observeMonthlyTotals(startMillis, endMillis, includeLow)

    suspend fun deleteById(id: Long) = transactionDao.deleteById(id)

    suspend fun count(): Int = transactionDao.count()
}
