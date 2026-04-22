# Stage 1 — Push trash manual device test

**Refs:** WO-STAGE1-001

## Prerequisites

- Debug or release build with `:feature:push-trash` included.
- Test device with Android 8+ recommended (notification channels required for channel-level rules).

## Procedure

1. **Listener permission** — Open Settings → Push trash; enable the listener if prompted; confirm banner clears on resume.
2. **Observe traffic** — Generate notifications; confirm stats increase.
3. **Channel blocking** — Per-app settings: block one channel; confirm blocked notifications land in trash and are cancelled from shade.
4. **App-wide modes** — Exercise Allow all / Block all / Per-channel only per `PushTrashRepository.decide`.
5. **Trash bin** — Restore and delete; confirm rules and DB rows match expectations.
6. **Channel ID capture** — Record real channel IDs before adding to `ChannelLabelMapper.verifiedChannelLabels`.

## Record

- Device model, Android version, build type, screenshots or logs.