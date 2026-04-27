# Stage 1-002 — CardCheck Manual Test Procedure

**Architecture**: v1.9.0 §27 (글로벌 파싱 엔진)
**WO**: WO-V190-STAGE1-002
**Module**: `:feature:card-check`
**DB**: Room v13 (`card_transaction`, `card_source_label`)

---

## Prerequisites

- Debug build (`./gradlew assembleDebug`) installed on device or emulator (Android 8+, API 26+).
- READ_SMS permission granted (provided by `:data:sms` module pipeline).
- Notification listener access granted (provided by `:feature:push-trash` module).
- Database upgrade from v12 → v13 occurs automatically on first launch (Migration12To13).
  - Existing push-trash, NKB, and other v12 tables are preserved (verified via WAL inspection).

---

## Procedure

### 1. Module discovery

1. Launch app.
2. Navigate to **Settings** → confirm a **CardCheck** card is visible below the Push Trash card.
3. Tap the CardCheck card → empty state screen renders with the user-promise notice and the month/filter chips.

### 2. User-driven labeling (시드 0 verification)

1. Confirm the CardCheck screen is empty initially. **No card brand or country is pre-listed.**
2. Trigger or wait for an SMS that resembles a card payment (test SMS injection or real payment notification).
3. The Source Detector should classify the unknown sender as `Suspect` (HIGH/MEDIUM threshold).
4. The labeling prompt UI must surface: **"Is this a card payment source?"** with the sender id in the prompt.
5. Type a label (e.g. "My VISA") and confirm.
6. Verify a row is added to `card_source_label` (DB inspection or repository count log).

### 3. Repeat capture (auto-classify after labeling)

1. Trigger another SMS from the same sender.
2. The labeling prompt should NOT appear (sender is now `Known`).
3. A new row is added to `card_transaction` with the labeled `sourceLabel`.
4. The CardCheck main screen now shows the currency card view and a transaction row.

### 4. Multi-currency separation

1. Trigger SMS from two different senders representing different currencies (e.g. USD `$25.50`, KRW `50,000원`, JPY `¥3,000`).
2. Label each sender.
3. The monthly totals section must show **separate currency cards** (USD/KRW/JPY) — no automatic FX conversion.
4. Each currency card displays the correct sum and transaction count.

### 5. Confidence filter

1. Trigger an SMS that lacks card identifier and merchant (only amount): expected `LOW` confidence.
2. By default, LOW rows are excluded from totals.
3. Toggle the "Include low-confidence rows" chip → totals update to include the LOW transactions.
4. Toggle off again → totals revert.

### 6. Month selector

1. Switch to **Last month** chip.
2. Totals and transaction list update to last month's bounds (system timezone, 1st 00:00 to last day 23:59:59).
3. Switch back to **This month** → reverts.

### 7. Diversity probe (글로벌 동작 보증)

Inject the following SMS bodies (via test harness or real device traffic) and verify each row appears with the expected `currencyCode`:

| # | SMS body | Expected currencyCode | Notes |
|---|---|---|---|
| 1 | `"신한카드 끝자리 1234 50,000원 GS25"` | `KRW` | suffix 원, 0 decimals |
| 2 | `"VISA $25.50 ending in 5678 Starbucks"` | `USD` | prefix $, 2 decimals |
| 3 | `"BHD 12.500 LULU"` | `BHD` | ISO prefix, 3 decimals |
| 4 | `"Sparkasse 15,75 € REWE"` | `EUR` | suffix €, comma decimal |
| 5 | `"Payment 100.00 USD at Amazon"` | `USD` | ISO 4217 suffix |
| 6 | `"招商银行 1,234元 京东购物"` | `CNY` | 한자 单位 元 |
| 7 | `"Bank Hapoalim 250.00 ₪ at SHUFERSAL"` | `ILS` | RTL 통화 ₪ |

Each row should appear in the transaction list with the correct `currencyCode` after labeling the source as a card payment source.

### 8. Constitutional compliance verification

1. **Out-Bound Zero (1조)**: Run the app with airplane mode enabled. Verify CardCheck still classifies, stores, and renders. No outbound network attempt should be observable.
2. **In-Bound Zero (2조)**: Inspect `card_transaction` rows and verify the original SMS body is NOT stored — only the extracted fields (`amount`, `currencyCode`, `merchantName`, etc.).
3. **Pricing Honesty (6조)**: Compare the displayed amount with the original SMS amount. They must match 1:1 with no FX conversion or rounding.

---

## Record

For each device test session, capture:

- Device model, Android version, build flavor (debug/release).
- Database file size before and after first transaction (from Settings → Apps → Storage).
- Screenshot of the labeling prompt and the multi-currency totals view.
- Logcat snippet showing the `Migration12To13` execution log (`LocalCacheModule` tag).
- Diversity probe results: which SMS bodies parsed, which fell through.

Place evidence under `docs/05_quality/evidence/stage1_card_check/<YYYYMMDD>/`.

---

## Known Limitations (Stage 1-002)

- Body datetime parsing is deferred to Stage 2+. Current implementation uses SMS receive timestamp.
- ML pattern learning is deferred to Stage 2+. Current detector uses a fixed score threshold.
- FX conversion is intentionally not implemented (user-promise: 1:1 with original SMS).
- Localization beyond English default is tracked separately (per user direction in WO-V190-STAGE1-002 정정).
