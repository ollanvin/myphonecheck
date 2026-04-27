package app.myphonecheck.mobile.feature.messagecheck.repository

import app.myphonecheck.mobile.core.globalengine.parsing.message.MessageCategory

/**
 * SMS Inbox 한 행의 분류 결과 (Architecture v2.0.0 §22).
 *
 * 본문은 UI 표시용 일부만 보유 — 영구 저장 0 (헌법 §2 In-Bound Zero).
 */
data class MessageEntry(
    val sender: String,
    val bodySnippet: String,
    val timestampMillis: Long,
    val category: MessageCategory,
    val hasUrl: Boolean,
    val isShortSender: Boolean,
)
