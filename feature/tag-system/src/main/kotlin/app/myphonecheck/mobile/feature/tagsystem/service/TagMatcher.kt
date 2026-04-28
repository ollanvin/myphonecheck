package app.myphonecheck.mobile.feature.tagsystem.service

import app.myphonecheck.mobile.core.globalengine.decision.IdentifierType
import app.myphonecheck.mobile.core.globalengine.decision.TagRecord
import app.myphonecheck.mobile.core.globalengine.parsing.phone.PhoneNumberParser
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import app.myphonecheck.mobile.feature.tagsystem.repository.RoomTagRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tag 매칭 서비스 (Architecture v2.1.0 §32-4).
 *
 * Surface(CallCheck/MessageCheck/PushTrash)가 본 서비스를 통해 태그 조회 + lastSeen 자동 갱신.
 * RealTimeActionEngine은 별도로 TagRepository(코어 interface)만 사용하므로 결정 경로에서는 사용 안 함.
 */
@Singleton
class TagMatcher @Inject constructor(
    private val tagRepo: RoomTagRepository,
    private val phoneParser: PhoneNumberParser,
    private val simContextProvider: SimContextProvider,
) {

    suspend fun matchForCall(rawNumber: String): TagRecord? {
        val key = normalizePhone(rawNumber)
        val record = tagRepo.findByKey(key, IdentifierType.PHONE_E164) ?: return null
        tagRepo.recordSeen(key, IdentifierType.PHONE_E164)
        return record
    }

    suspend fun matchForSms(sender: String): TagRecord? {
        val record = tagRepo.findByKey(sender, IdentifierType.SMS_SENDER) ?: return null
        tagRepo.recordSeen(sender, IdentifierType.SMS_SENDER)
        return record
    }

    suspend fun matchForNotification(packageName: String): TagRecord? {
        val record = tagRepo.findByKey(packageName, IdentifierType.NOTIFICATION_PACKAGE) ?: return null
        tagRepo.recordSeen(packageName, IdentifierType.NOTIFICATION_PACKAGE)
        return record
    }

    private fun normalizePhone(rawNumber: String): String {
        if (rawNumber.isBlank()) return rawNumber
        val parsed = phoneParser.parse(rawNumber, simContextProvider.resolve())
        return if (parsed.isValid) parsed.e164 else rawNumber
    }
}
