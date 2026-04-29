package app.myphonecheck.mobile.core.globalengine.decision

import app.myphonecheck.mobile.core.globalengine.search.SearchInput

/**
 * SearchInput → BlockListRepository / TagRepository 호환 (key, IdentifierType) 매핑 (v2.6.0 §11 정합).
 *
 * IdentifierType은 PHONE_E164 / SMS_SENDER / NOTIFICATION_PACKAGE 3 타입.
 * SearchInput.Url / MessageBody는 SMS_SENDER에 매핑 (Stage 4 본격 통합 시 IdentifierType 확장 가능).
 */
fun SearchInput.toIdentifierKey(): String = when (this) {
    is SearchInput.PhoneNumber -> value
    is SearchInput.Url -> value
    is SearchInput.MessageBody -> {
        when {
            extractedNumbers.isNotEmpty() -> extractedNumbers.first()
            extractedUrls.isNotEmpty() -> extractedUrls.first()
            else -> text.take(50)
        }
    }
    is SearchInput.AppPackage -> packageName
}

fun SearchInput.toIdentifierType(): IdentifierType = when (this) {
    is SearchInput.PhoneNumber -> IdentifierType.PHONE_E164
    is SearchInput.Url -> IdentifierType.SMS_SENDER  // Stage 4 본격 시점에 URL 전용 IdentifierType 추가 가능
    is SearchInput.MessageBody -> IdentifierType.SMS_SENDER
    is SearchInput.AppPackage -> IdentifierType.NOTIFICATION_PACKAGE
}

/** 차단 추가 — v2.6.0 §11 액션 1. */
suspend fun BlockListRepository.addBlock(input: SearchInput, source: String = "user") {
    add(input.toIdentifierKey(), input.toIdentifierType(), source)
}

/** 차단 해제 — v2.6.0 §11. */
suspend fun BlockListRepository.removeBlock(input: SearchInput) {
    remove(input.toIdentifierKey(), input.toIdentifierType())
}

/** 차단 여부 조회. */
suspend fun BlockListRepository.isBlocked(input: SearchInput): Boolean {
    return isBlocked(input.toIdentifierKey(), input.toIdentifierType())
}

/** 태그 부여 — v2.6.0 §11 액션 2. */
suspend fun TagRepository.setTagFor(input: SearchInput, tagText: String) {
    setTag(input.toIdentifierKey(), input.toIdentifierType(), tagText)
}

/** 태그 조회. */
suspend fun TagRepository.findTagFor(input: SearchInput): TagRecord? {
    return findByKey(input.toIdentifierKey(), input.toIdentifierType())
}
