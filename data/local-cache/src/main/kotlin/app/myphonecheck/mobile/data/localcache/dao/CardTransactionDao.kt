package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.CardTransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 거래 집계 결과 (월별·통화별·소스별) 행.
 */
data class CardTransactionMonthlyTotal(
    val sourceId: String,
    val sourceLabel: String,
    val currencyCode: String,
    val totalMinorUnits: Long,
    val transactionCount: Int,
)

@Dao
interface CardTransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CardTransactionEntity): Long

    @Query("SELECT * FROM card_transaction ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<CardTransactionEntity>>

    @Query(
        "SELECT * FROM card_transaction " +
            "WHERE timestamp >= :startMillis AND timestamp < :endMillis " +
            "ORDER BY timestamp DESC",
    )
    fun observeInRange(
        startMillis: Long,
        endMillis: Long,
    ): Flow<List<CardTransactionEntity>>

    /**
     * 월별·통화별·소스별 집계.
     *
     * 통화 분리 — USD·KRW·JPY 동시 표시 가능 (멀티 통화 사용자 대응).
     */
    @Query(
        "SELECT sourceId, sourceLabel, currencyCode, " +
            "SUM(amount) AS totalMinorUnits, COUNT(*) AS transactionCount " +
            "FROM card_transaction " +
            "WHERE timestamp >= :startMillis AND timestamp < :endMillis " +
            "AND (:includeLow = 1 OR confidence != 'LOW') " +
            "GROUP BY sourceId, currencyCode " +
            "ORDER BY currencyCode ASC, totalMinorUnits DESC",
    )
    fun observeMonthlyTotals(
        startMillis: Long,
        endMillis: Long,
        includeLow: Boolean,
    ): Flow<List<CardTransactionMonthlyTotal>>

    @Query("SELECT COUNT(*) FROM card_transaction")
    suspend fun count(): Int

    @Delete
    suspend fun delete(entity: CardTransactionEntity)

    @Query("DELETE FROM card_transaction WHERE id = :id")
    suspend fun deleteById(id: Long)
}
