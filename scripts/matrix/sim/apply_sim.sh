#!/usr/bin/env bash
# 헌법 §9-6 정합 SIM 매트릭스 적용 (adb emu gsm 시뮬레이션, 실 SIM 0).
#
# 사용:
#   scripts/matrix/sim/apply_sim.sh KR
#   scripts/matrix/sim/apply_sim.sh US
#
# 11개국: KR US JP DE GB IN BR CN TW SA TH (CORE 4 + EXTENDED 4 + SPECIALIZED 3).
# 본 스크립트는 SimMatrixContext.kt와 1:1 정합.

set -euo pipefail

SIM="${1:-KR}"

case "$SIM" in
  KR) MCC_MNC="45005";  ISO="kr"; LOCALE="ko-KR" ;;
  US) MCC_MNC="310260"; ISO="us"; LOCALE="en-US" ;;
  JP) MCC_MNC="44010";  ISO="jp"; LOCALE="ja-JP" ;;
  DE) MCC_MNC="26201";  ISO="de"; LOCALE="de-DE" ;;
  GB) MCC_MNC="23410";  ISO="gb"; LOCALE="en-GB" ;;
  IN) MCC_MNC="40410";  ISO="in"; LOCALE="hi-IN" ;;
  BR) MCC_MNC="72402";  ISO="br"; LOCALE="pt-BR" ;;
  CN) MCC_MNC="46000";  ISO="cn"; LOCALE="zh-CN" ;;
  TW) MCC_MNC="46692";  ISO="tw"; LOCALE="zh-TW" ;;
  SA) MCC_MNC="42001";  ISO="sa"; LOCALE="ar-SA" ;;
  TH) MCC_MNC="52001";  ISO="th"; LOCALE="th-TH" ;;
  *) echo "Unknown SIM: $SIM. Expected: KR US JP DE GB IN BR CN TW SA TH" >&2; exit 2 ;;
esac

echo "[matrix/sim] Applying SIM=$SIM mccMnc=$MCC_MNC iso=$ISO locale=$LOCALE"

adb shell setprop gsm.sim.operator.numeric "$MCC_MNC"
adb shell setprop gsm.sim.operator.iso-country "$ISO"
adb shell setprop gsm.operator.numeric "$MCC_MNC"
adb shell setprop gsm.operator.iso-country "$ISO"
adb shell settings put system system_locales "$LOCALE" || true

echo "[matrix/sim] SIM=$SIM applied. Verify: adb shell getprop | grep gsm.sim.operator"
