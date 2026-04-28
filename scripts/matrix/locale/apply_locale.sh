#!/usr/bin/env bash
# 헌법 §9-6 정합 Locale 매트릭스 적용 (AppCompatDelegate.setApplicationLocales 위임 보조).
#
# 사용:
#   scripts/matrix/locale/apply_locale.sh ko-KR
#   scripts/matrix/locale/apply_locale.sh en-US
#
# 11 Locale: ko-KR ja-JP en-US en-GB de-DE hi-IN pt-BR zh-CN zh-TW ar-SA th-TH

set -euo pipefail

LOCALE="${1:-en-US}"

case "$LOCALE" in
  ko-KR|ja-JP|en-US|en-GB|de-DE|hi-IN|pt-BR|zh-CN|zh-TW|ar-SA|th-TH) ;;
  *) echo "Unknown LOCALE: $LOCALE. Expected one of: ko-KR ja-JP en-US en-GB de-DE hi-IN pt-BR zh-CN zh-TW ar-SA th-TH" >&2; exit 2 ;;
esac

echo "[matrix/locale] Applying LOCALE=$LOCALE"
adb shell settings put system system_locales "$LOCALE"
echo "[matrix/locale] Verify: adb shell settings get system system_locales"
