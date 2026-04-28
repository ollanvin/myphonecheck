#!/usr/bin/env python3
"""Latin Hypercube 샘플링 — Phase 4 매트릭스 검증 케이스 선정 (헌법 §9-6 정합).

전수 풀: 4 device × 11 SIM × 17 시나리오 = 748 케이스.
LHS로 40~60 핵심 케이스 선정 (PR 게이트 ~10분, 야간 ~60분).

표준 라이브러리만 사용 (NumPy 의존 없음, CI 가벼움).

사용:
    python3 scripts/matrix/sample/sample_lhs.py --size 60 --seed 42
출력:
    JSON 배열 to stdout — 각 항목 {device, sim, scenario}.
"""
from __future__ import annotations

import argparse
import json
import random
import sys
from typing import List

DEVICES = ["pixel4Api28", "pixel5Api31", "tabletApi33", "pixel7Api34"]
SIMS = ["KR", "US", "JP", "DE", "GB", "IN", "BR", "CN", "TW", "SA", "TH"]
SCENARIOS = [f"S{n:02d}" for n in range(1, 18)]


def latin_hypercube(size: int, axes: List[List[str]], rng: random.Random) -> List[List[str]]:
    """단순 LHS — 각 축을 size 구간으로 나눠 stratified 샘플링.

    각 축의 모든 값이 균등 비율로 등장하도록 size를 axis len 배수에 맞춰 보정.
    """
    samples: List[List[str]] = []
    for axis in axes:
        # axis 값을 size에 맞게 균등 반복 후 셔플 (Latin Hypercube 원리: stratified shuffle)
        repeats = (size + len(axis) - 1) // len(axis)
        column = (axis * repeats)[:size]
        rng.shuffle(column)
        samples.append(column)
    return [list(row) for row in zip(*samples)]


def main() -> int:
    p = argparse.ArgumentParser(description="Phase 4 매트릭스 LHS 샘플러")
    p.add_argument("--size", type=int, default=60, help="샘플 케이스 수 (PR 권장 12~20, 야간 40~60)")
    p.add_argument("--seed", type=int, default=42, help="재현 가능성 위한 RNG seed")
    p.add_argument("--device-pool", default=",".join(DEVICES))
    p.add_argument("--sim-pool", default=",".join(SIMS))
    p.add_argument("--scenario-pool", default=",".join(SCENARIOS))
    args = p.parse_args()

    devices = args.device_pool.split(",")
    sims = args.sim_pool.split(",")
    scenarios = args.scenario_pool.split(",")

    rng = random.Random(args.seed)
    rows = latin_hypercube(args.size, [devices, sims, scenarios], rng)
    out = [{"device": d, "sim": s, "scenario": sc} for (d, s, sc) in rows]
    json.dump(out, sys.stdout, ensure_ascii=False, indent=2)
    sys.stdout.write("\n")
    return 0


if __name__ == "__main__":
    sys.exit(main())
