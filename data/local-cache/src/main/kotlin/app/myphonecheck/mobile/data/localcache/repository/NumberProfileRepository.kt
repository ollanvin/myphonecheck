package app.myphonecheck.mobile.data.localcache.repository

import app.myphonecheck.mobile.core.model.ActionState
import app.myphonecheck.mobile.data.localcache.dao.DetailTagDao
import app.myphonecheck.mobile.data.localcache.dao.NumberProfileDao
import app.myphonecheck.mobile.data.localcache.entity.DetailTagEntity
import app.myphonecheck.mobile.data.localcache.entity.DetailTagSource
import app.myphonecheck.mobile.data.localcache.entity.NumberProfileBlockState
import app.myphonecheck.mobile.data.localcache.entity.NumberProfileEntity
import app.myphonecheck.mobile.data.localcache.entity.QuickLabel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

data class DetailTagModel(
    val tagName: String,
    val source: DetailTagSource,
    val createdAt: Long,
    val updatedAt: Long,
)

data class NumberProfileSnapshot(
    val normalizedNumber: String,
    val quickLabels: Set<QuickLabel>,
    val doNotMissFlag: Boolean,
    val actionState: ActionState,
    val userMemoShort: String?,
    val detailTags: List<DetailTagModel>,
    val lastInteractionAt: Long,
    val lastCallAt: Long?,
    val lastSmsAt: Long?,
) {
    val hasUserSignals: Boolean
        get() = quickLabels.isNotEmpty() ||
            detailTags.isNotEmpty() ||
            !userMemoShort.isNullOrBlank() ||
            actionState != ActionState.NONE
}

@Singleton
class NumberProfileRepository @Inject constructor(
    private val numberProfileDao: NumberProfileDao,
    private val detailTagDao: DetailTagDao,
) {
    suspend fun touchCallInteraction(normalizedNumber: String, at: Long = System.currentTimeMillis()) {
        val existing = ensureProfile(normalizedNumber, at)
        numberProfileDao.upsert(
            existing.copy(
                lastInteractionAt = at,
                lastCallAt = at,
                updatedAt = at,
            ),
        )
    }

    suspend fun touchSmsInteraction(normalizedNumber: String, at: Long = System.currentTimeMillis()) {
        val existing = ensureProfile(normalizedNumber, at)
        numberProfileDao.upsert(
            existing.copy(
                lastInteractionAt = at,
                lastSmsAt = at,
                updatedAt = at,
            ),
        )
    }

    suspend fun toggleQuickLabel(normalizedNumber: String, label: QuickLabel) {
        val now = System.currentTimeMillis()
        val existing = ensureProfile(normalizedNumber, now)
        val labels = existing.quickLabelSet().toMutableSet()
        if (!labels.add(label)) {
            labels.remove(label)
        }
        val nextDoNotMiss = labels.any { it == QuickLabel.IMPORTANT || it == QuickLabel.PICK_UP }
        val nextBlockState = when {
            label == QuickLabel.DO_NOT_BLOCK && labels.contains(QuickLabel.DO_NOT_BLOCK) ->
                NumberProfileBlockState.DO_NOT_BLOCK
            label == QuickLabel.DO_NOT_BLOCK && existing.blockStateEnum() == NumberProfileBlockState.DO_NOT_BLOCK ->
                NumberProfileBlockState.NONE
            else -> existing.blockStateEnum()
        }
        numberProfileDao.upsert(
            existing.copy(
                quickLabels = QuickLabel.toStorage(labels),
                doNotMissFlag = nextDoNotMiss,
                blockState = nextBlockState.storageKey,
                updatedAt = now,
            ),
        )
    }

    suspend fun setBlockState(
        normalizedNumber: String,
        state: NumberProfileBlockState,
    ) {
        val now = System.currentTimeMillis()
        val existing = ensureProfile(normalizedNumber, now)
        val labels = existing.quickLabelSet().toMutableSet()
        if (state == NumberProfileBlockState.DO_NOT_BLOCK) {
            labels.add(QuickLabel.DO_NOT_BLOCK)
        } else if (state == NumberProfileBlockState.BLOCKED) {
            labels.remove(QuickLabel.DO_NOT_BLOCK)
        }
        numberProfileDao.upsert(
            existing.copy(
                quickLabels = QuickLabel.toStorage(labels),
                blockState = state.storageKey,
                updatedAt = now,
            ),
        )
    }

    suspend fun updateShortMemo(normalizedNumber: String, memo: String?) {
        val now = System.currentTimeMillis()
        val existing = ensureProfile(normalizedNumber, now)
        numberProfileDao.upsert(
            existing.copy(
                userMemoShort = memo?.trim()?.takeIf { it.isNotEmpty() },
                updatedAt = now,
            ),
        )
    }

    suspend fun addDetailTag(
        normalizedNumber: String,
        tagName: String,
        source: DetailTagSource = DetailTagSource.USER,
    ) {
        val normalizedTag = tagName.trim().take(24)
        if (normalizedTag.isBlank()) return
        val now = System.currentTimeMillis()
        ensureProfile(normalizedNumber, now)
        detailTagDao.upsert(
            DetailTagEntity(
                normalizedNumber = normalizedNumber,
                tagName = normalizedTag,
                source = source.storageKey,
                createdAt = now,
                updatedAt = now,
            ),
        )
    }

    suspend fun removeDetailTag(normalizedNumber: String, tagName: String) {
        detailTagDao.deleteTag(normalizedNumber, tagName)
    }

    suspend fun getSnapshot(normalizedNumber: String): NumberProfileSnapshot? {
        val profile = numberProfileDao.findByNumber(normalizedNumber) ?: return null
        val tags = detailTagDao.getByNumber(normalizedNumber)
        return buildSnapshot(profile, tags)
    }

    fun observeSnapshot(normalizedNumber: String): Flow<NumberProfileSnapshot?> =
        combine(
            numberProfileDao.observeByNumber(normalizedNumber),
            detailTagDao.observeByNumber(normalizedNumber),
        ) { profile, tags ->
            profile?.let { buildSnapshot(it, tags) }
        }

    fun observeAllSnapshots(): Flow<Map<String, NumberProfileSnapshot>> =
        combine(
            numberProfileDao.observeAll(),
            detailTagDao.observeAll(),
        ) { profiles, tags ->
            val groupedTags = tags.groupBy { it.normalizedNumber }
            profiles.associate { profile ->
                profile.normalizedNumber to buildSnapshot(
                    profile = profile,
                    tags = groupedTags[profile.normalizedNumber].orEmpty(),
                )
            }
        }

    private suspend fun ensureProfile(
        normalizedNumber: String,
        now: Long,
    ): NumberProfileEntity {
        val existing = numberProfileDao.findByNumber(normalizedNumber)
        if (existing != null) return existing
        val created = NumberProfileEntity(
            normalizedNumber = normalizedNumber,
            lastInteractionAt = now,
            createdAt = now,
            updatedAt = now,
        )
        numberProfileDao.upsert(created)
        return created
    }

    private fun buildSnapshot(
        profile: NumberProfileEntity,
        tags: List<DetailTagEntity>,
    ): NumberProfileSnapshot = NumberProfileSnapshot(
        normalizedNumber = profile.normalizedNumber,
        quickLabels = profile.quickLabelSet(),
        doNotMissFlag = profile.doNotMissFlag,
        actionState = profile.blockStateEnum().toActionState(),
        userMemoShort = profile.userMemoShort,
        detailTags = tags.map {
            DetailTagModel(
                tagName = it.tagName,
                source = DetailTagSource.fromStorage(it.source),
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        },
        lastInteractionAt = profile.lastInteractionAt,
        lastCallAt = profile.lastCallAt,
        lastSmsAt = profile.lastSmsAt,
    )

    private fun NumberProfileEntity.quickLabelSet(): Set<QuickLabel> = QuickLabel.fromStorage(quickLabels)

    private fun NumberProfileEntity.blockStateEnum(): NumberProfileBlockState =
        NumberProfileBlockState.fromStorage(blockState)

    private fun NumberProfileBlockState.toActionState(): ActionState = when (this) {
        NumberProfileBlockState.NONE -> ActionState.NONE
        NumberProfileBlockState.BLOCKED -> ActionState.BLOCKED
        NumberProfileBlockState.DO_NOT_BLOCK -> ActionState.DO_NOT_BLOCK
    }
}
