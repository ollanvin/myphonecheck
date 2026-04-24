#!/usr/bin/env python3
"""
WO-V180-MIGRATE-002: mechanical split of MyPhoneCheck_Architecture_v1.7.1.md
into docs/00_governance/architecture/v1.8.0_cursor/ (UTF-8, LF, POSIX newline).
"""
from __future__ import annotations

import re
import shutil
from pathlib import Path

REPO = Path(__file__).resolve().parents[1]
SRC_DL = Path(r"C:\Users\user\Downloads\MyPhoneCheck_Architecture_v1.7.1.md")
SRC_CANON = REPO / "docs/00_governance/architecture/v1.7.1/MyPhoneCheck_Architecture_v1.7.1.md"
OUT = REPO / "docs/00_governance/architecture/v1.8.0_cursor"
WORKER = "v1.8.0_cursor"


def load_lines() -> list[str]:
    text = SRC_CANON.read_text(encoding="utf-8")
    text = text.replace("\r\n", "\n").replace("\r", "\n")
    if not text.endswith("\n"):
        text += "\n"
    return text.split("\n")


def slice_lines(lines: list[str], start: int, end: int) -> str:
    """1-based inclusive start/end."""
    chunk = lines[start - 1 : end]
    return "\n".join(chunk)


def join_ranges(lines: list[str], ranges: list[tuple[int, int]]) -> str:
    parts = [slice_lines(lines, a, b) for a, b in ranges]
    return "\n".join(parts)


def std_header(
    title_line: str,
    section_ref: str,
    layer: str,
    relpath: str,
    line_note: str,
) -> str:
    return (
        f"{title_line}\n\n"
        f"**원본 출처**: v1.7.1 {section_ref} ({line_note})\n"
        f"**v1.8.0 Layer**: {layer}\n"
        f"**의존**: `00_core/01_primary.md`\n"
        f"**변경 이력**: 본 파일은 v1.7.1 {section_ref} 원본 전문 이관본. 텍스트 변경 없음.\n"
        f"**파일 경로**: `docs/00_governance/architecture/{WORKER}/{relpath}`\n\n"
        f"---\n\n"
    )


def write_md(path: Path, header: str, body: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    full = header + body
    if not full.endswith("\n"):
        full += "\n"
    path.write_text(full, encoding="utf-8", newline="\n")


def readme_dir(path: Path, purpose: str, scope: str, iface: str, files_hint: str) -> None:
    body = (
        f"## 목적\n\n{purpose}\n\n"
        f"## 책임 범위\n\n{scope}\n\n"
        f"## 외부 인터페이스\n\n{iface}\n\n"
        f"## 내부 파일 안내\n\n{files_hint}\n"
    )
    h = std_header(
        "# README",
        "§4-9 (디렉터리 README)",
        "Appendix",
        str(path.relative_to(OUT)).replace("\\", "/"),
        "신규",
    )
    write_md(path / "README.md", h, body)


def main() -> None:
    SRC_CANON.parent.mkdir(parents=True, exist_ok=True)
    if not SRC_CANON.exists():
        shutil.copy2(SRC_DL, SRC_CANON)

    lines = load_lines()
    n = len(lines)

    def w(rel: str, title: str, sec: str, layer: str, ranges: list[tuple[int, int]], note: str) -> None:
        body = join_ranges(lines, ranges)
        first = body.split("\n", 1)[0] if body else title
        if not first.startswith("#"):
            first = title
        h = std_header(first, sec, layer, rel, note)
        write_md(OUT / rel, h, body)

    def w1(rel: str, title: str, sec: str, layer: str, a: int, b: int, note: str) -> None:
        w(rel, title, sec, layer, [(a, b)], note)

    if OUT.exists():
        shutil.rmtree(OUT)
    OUT.mkdir(parents=True)

    # --- 00_core & appendix (§0 / 비전 분할)
    w1("00_core/01_primary.md", "# HTML 주석 블록", "§0 머리말", "Core", 1, 48, f"1–48 / 총{n}줄")
    w1("00_core/02_secondary.md", "# 문서 타이틀 블록", "§0 서문", "Core", 51, 74, "51–74")
    w1("00_core/03_tertiary.md", "# 구분선", "§0 서문", "Core", 75, 76, "75–76")
    w1("appendix/D_version_matrix.md", "## 0-A", "§0-A", "Appendix", 78, 121, "78–121")
    w1("appendix/A_audit_log.md", "## 0-B", "§0-B", "Appendix", 123, 157, "123–157")
    w1("appendix/B_patch_history.md", "## 0-B-2", "§0-B-2", "Appendix", 159, 188, "159–188")
    w1("appendix/C_limitations.md", "## 0-C", "§0-C~0-F", "Appendix", 190, 248, "190–248")
    w1("appendix/E_vision_record.md", "# Z.", "§Z", "Appendix", 4252, 4555, "4252–4555")

    w1("05_constitution.md", "# 1. 헌법", "§1", "Core", 250, 426, "250–426")

    w(
        "10_policy/01_permissions.md",
        "# 1. 헌법",
        "§1 + §24-6-1",
        "Policy",
        [(250, 426), (2796, 2866)],
        "250–426 + 2796–2866",
    )

    # Product design
    w1("06_product_design/01_goose_vs_egg.md", "# 2.", "§2", "Core", 429, 469, "429–469")
    w1("06_product_design/02_golden_egg.md", "# 3.", "§3", "Core", 471, 546, "471–546")
    w1("06_product_design/03_ux_domains.md", "# 4.", "§4", "Core", 548, 597, "548–597")
    w1("06_product_design/04_system_arch.md", "# 5.", "§5", "Core", 599, 700, "599–700")
    w1("06_product_design/05_product_strategy.md", "# 17.", "§17", "Core", 1784, 1843, "1784–1843")

    # Engine
    w1("07_engine/01_three_layer.md", "# 6.", "§6", "Engine", 703, 773, "703–773")
    w1("07_engine/02_self_discovery.md", "# 7.", "§7", "Engine", 775, 901, "775–901")
    w1("07_engine/03_nkb.md", "# 8.", "§8", "Engine", 903, 1215, "903–1215")
    w1("07_engine/04_analyzer.md", "# 9.", "§9", "Engine", 1217, 1320, "1217–1320")
    w1("07_engine/05_decision_formula.md", "# 10.", "§10", "Engine", 1322, 1468, "1322–1468")
    w1("07_engine/06_cold_start.md", "# 11.", "§11", "Engine", 1470, 1531, "1470–1531")
    w1("07_engine/07_self_evolution.md", "# 12.", "§12", "Engine", 1533, 1587, "1533–1587")
    w1("07_engine/08_sla.md", "# 14.", "§14", "Engine", 1589, 1650, "1589–1650")

    # Policy 27 splits
    w(
        "10_policy/04_data_safety.md",
        "# 27.",
        "§27-1,2,4,5",
        "Policy",
        [(3145, 3200), (3257, 3342)],
        "3145–3200 + 3257–3342",
    )
    w1("10_policy/05_permissions_declaration.md", "## 27-3", "§27-3", "Policy", 3201, 3256, "3201–3256")
    w1("10_policy/03_special_access.md", "### 27-3-5", "§27-3-5", "Policy", 3231, 3236, "3231–3236")
    w1("10_policy/06_store_policy.md", "## 33-2", "§33-2", "Policy", 3949, 3959, "3949–3959")
    w1("10_policy/07_country_i18n.md", "# 28.", "§28", "Policy", 3344, 3400, "3344–3400")

    w1("10_policy/02_sms_mode.md", "## 18-4", "§18-4", "Policy", 1897, 2194, "1897–2194")

    # Features / §18
    w(
        "20_features/21_call.md",
        "# 5.",
        "§5 + §18 CallCheck",
        "Feature",
        [(599, 700), (1847, 1896)],
        "599–700 + 1847–1896",
    )
    w1("20_features/22_message.md", "## 18-4", "§18-4", "Feature", 1897, 2194, "1897–2194")
    w1("20_features/23_mic.md", "## 18-6", "§18-6", "Feature", 2205, 2343, "2205–2343")
    w1("20_features/24_camera.md", "## 18-7", "§18-7", "Feature", 2345, 2416, "2345–2416")
    w(
        "20_features/25_smoke_scenarios.md",
        "# 18.",
        "§18 (스모크·보조 절)",
        "Feature",
        [(1847, 1896), (2196, 2204), (2418, 2427), (2429, 2461)],
        "분할 구간",
    )

    w1("30_billing.md", "# 31.", "§31", "Business", 3459, 3706, "3459–3706")

    w1("40_i18n/01_country_separation.md", "# 28.", "§28", "i18n", 3344, 3400, "3344–3400")
    w1("40_i18n/02_strings_xml.md", "# 25.", "§25", "i18n", 2946, 3034, "2946–3034")

    w(
        "50_test_infra/01_test_infra.md",
        "# 34.",
        "§34 (34-2~4)",
        "Test",
        [(3962, 3963), (3987, 4010)],
        "3962–3963 + 3987–4010",
    )
    w(
        "50_test_infra/02_smoke_scenarios.md",
        "## 18-1",
        "§18-1 + §19 + §20",
        "Test",
        [(1851, 1865), (2464, 2511), (2516, 2550)],
        "1851–1865 + 2464–2511 + 2516–2550",
    )
    w1("50_test_infra/03_permission_matrix.md", "## 34-1", "§34-1", "Test", 3964, 3986, "3964–3986")

    w1("60_implementation/01_day_by_day.md", "# 24.", "§24", "Implementation", 2725, 2944, "2725–2944")
    w(
        "60_implementation/02_stage0_freeze.md",
        "## 23-4",
        "§23-4,5 + §33-1",
        "Implementation",
        [(2701, 2722), (3750, 3947)],
        "2701–2722 + 3750–3947",
    )
    w1("60_implementation/03_interface_injection.md", "# 32.", "§32", "Implementation", 3708, 3747, "3708–3747")
    w1("60_implementation/04_memory_budget.md", "# 30.", "§30", "Implementation", 3402, 3456, "3402–3456")
    w(
        "60_implementation/05_repo_layout.md",
        "# 23.",
        "§23 (FREEZE 제외)",
        "Implementation",
        [(2613, 2700), (2713, 2722)],
        "2613–2700 + 2713–2722",
    )
    w1("60_implementation/06_ci_cd.md", "# 26.", "§26", "Implementation", 3037, 3142, "3037–3142")

    w1("70_business/01_business_model.md", "# 16.", "§16", "Business", 1719, 1782, "1719–1782")
    w1("70_business/02_kpi_mapping.md", "## 0-B", "§0-B KPI", "Business", 123, 142, "123–142")

    w1("80_verification/01_dry_run_checklist.md", "# 19.", "§19", "Verification", 2464, 2511, "2464–2511")
    w1("80_verification/02_success_criteria.md", "# 20.", "§20", "Verification", 2516, 2550, "2516–2550")
    w1("80_verification/03_open_issues.md", "# 21.", "§21", "Verification", 2553, 2594, "2553–2594")
    w1("80_verification/04_round5_consensus.md", "# 22.", "§22", "Verification", 2597, 2610, "2597–2610")
    w1("80_verification/05_central_mapping_zero.md", "# 15.", "§15", "Verification", 1652, 1716, "1652–1716")

    w1("95_integration/01_four_surfaces_integration.md", "# 36.", "§36", "Integration", 4103, 4249, "4103–4249")
    w1("95_integration/02_infrastructure_reference.md", "# 35.", "§35", "Integration", 4013, 4101, "4013–4101")

    # Root README
    root_readme = (
        "## 목적\n\n"
        "Cursor 워커 산출물: Architecture v1.7.1 단일 원본의 기계적 분할본 (WO-V180-MIGRATE-002).\n\n"
        "## 작성자\n\n"
        "워커: Cursor (자동화 스크립트 `scripts/migrate_arch_v180_cursor.py`).\n\n"
        "## 비교\n\n"
        "`v1.8.0_cowork/`, `v1.8.0_claudecode/`와 독립. 최종 `v1.8.0/`은 대표 선택 후 비전이 복사.\n\n"
        "## 내부 구조\n\n"
        "`INDEX.md`와 하위 디렉터리 README 참조.\n"
    )
    write_md(
        OUT / "README.md",
        std_header("# README", "§4-9", "Appendix", "README.md", "신규"),
        root_readme,
    )

    readme_dir(
        OUT / "00_core",
        "비전 작성분 HTML·타이틀·구분선 분할 보관.",
        "§0에서 추출한 서두. 본문 메타는 appendix.",
        "`INDEX.md`, 정책·엔진 문서가 상호 참조.",
        "`01_primary.md` HTML 주석, `02_secondary.md` 타이틀, `03_tertiary.md` 구분선.",
    )
    readme_dir(
        OUT / "appendix",
        "§0 메타·§Z 비전 기록.",
        "감사·패치·한계·버전 매트릭스·Z 부록.",
        "전 레이어에서 교차 참조.",
        "`A_*.md`~`E_*.md` 파일별 §0/§Z 출처.",
    )
    readme_dir(
        OUT / "06_product_design",
        "제품·UX·아키텍처 상위 설계.",
        "§2~§5, §17.",
        "엔진·피처·정책이 전제로 삼음.",
        "파일명 숫자 순 §2~§5, §17.",
    )
    readme_dir(
        OUT / "07_engine",
        "엔진·데이터·콜드스타트·SLA.",
        "§6~§12, §14.",
        "피처 모듈·테스트가 엔진 계약을 소비.",
        "01~08 파일이 §6~§14에 대응.",
    )
    readme_dir(
        OUT / "10_policy",
        "헌법·권한·데이터 세이프티·스토어.",
        "§1, §18-4, §24-6-1 Manifest, §27~§28, §33-2.",
        "구현·스토어 제출·i18n과 연결.",
        "파일명이 정책 주제별 원본 구간.",
    )
    readme_dir(
        OUT / "20_features",
        "Four Surfaces 및 스모크 시나리오 본문.",
        "§5·§18 분할.",
        "엔진·테스트·정책과 연동.",
        "21~25 파일이 Call/Message/Mic/Camera/스모크.",
    )
    readme_dir(
        OUT / "40_i18n",
        "국가/언어·strings.xml.",
        "§25, §28.",
        "UI·클러스터·정책이 참조.",
        "01 국가 분리, 02 문자열.",
    )
    readme_dir(
        OUT / "50_test_infra",
        "테스트 인프라·스모크·권한 매트릭스.",
        "§18-1, §19~§20, §34.",
        "CI·릴리즈 게이트.",
        "01 §34 본문(34-2~4), 02 스모크+드라이런+성공기준, 03 §34-1.",
    )
    readme_dir(
        OUT / "60_implementation",
        "레포 구조·일정·FREEZE·빌드.",
        "§23~§26, §30, §32~§33 Stage0.",
        "코드베이스 트리와 직접 매핑.",
        "01 Day-by-Day, 02 Stage0+FREEZE, 03 DI, 04 메모리, 05 레이아웃, 06 CI/CD.",
    )
    readme_dir(
        OUT / "70_business",
        "비즈니스·KPI.",
        "§16, §0-B KPI 표.",
        "청구·헌법 가격 조항과 연결.",
        "01 비즈니스 모델, 02 KPI 매핑.",
    )
    readme_dir(
        OUT / "80_verification",
        "검증·오픈이슈·합의.",
        "§15, §19~§22.",
        "릴리즈 전 체크리스트.",
        "01~05 검증·합의·중앙매핑.",
    )
    readme_dir(
        OUT / "90_declarations",
        "향후 선언 예약.",
        "v1.8.1 이후 명시 전용.",
        "다른 레이어가 확장 시 참조.",
        "현재 README만 유지.",
    )
    decl = (
        "v1.8.1 예정: Play·스토어 선언문 갱신 등은 별도 WO에서 본 디렉터리에 추가될 수 있음.\n"
        "본 마이그레이션(v1.8.0_cursor)에서는 원본 v1.7.1에 독립 `90_declarations` 본문이 없어 README만 둔다.\n"
    )
    write_md(
        OUT / "90_declarations/README.md",
        std_header("# README", "§3 구조", "Policy", "90_declarations/README.md", "신규"),
        decl,
    )
    readme_dir(
        OUT / "95_integration",
        "인프라 페어·Four Surfaces 통합.",
        "§35~§36.",
        "제품·운영 문서 쌍.",
        "01 §36, 02 §35.",
    )

    # INDEX.md (통계는 INDEX 포함 후 재계산)
    index_map = (
        "## 문서 맵\n\n"
        "- `README.md` — 루트 안내\n"
        "- `05_constitution.md` — §1\n"
        "- `00_core/` — 비전 작성분 서두\n"
        "- `06_product_design/` — §2~§5, §17\n"
        "- `07_engine/` — §6~§12, §14\n"
        "- `10_policy/` — §1+Manifest, §18-4, §27~§28, §33-2, Special Access 발췌\n"
        "- `20_features/` — §5+§18 분할\n"
        "- `30_billing.md` — §31\n"
        "- `40_i18n/` — §25, §28\n"
        "- `50_test_infra/` — §18-1, §19~§20, §34\n"
        "- `60_implementation/` — §23~§26, §30, §32, §33-1\n"
        "- `70_business/` — §16, §0-B KPI\n"
        "- `80_verification/` — §15, §19~§22\n"
        "- `90_declarations/` — 예약\n"
        "- `95_integration/` — §35~§36\n"
        "- `appendix/` — §0, §Z\n"
    )

    def index_body_fn(n_files: int, nbytes: int) -> str:
        return (
            f"# INDEX — {WORKER}\n\n"
            f"- **총 .md 파일**: {n_files}\n"
            f"- **총 크기**: {nbytes // 1024} KB ({nbytes} bytes)\n"
            f"- **원본**: `docs/00_governance/architecture/v1.7.1/MyPhoneCheck_Architecture_v1.7.1.md`\n\n"
            f"{index_map}"
        )

    md_files = sorted(OUT.rglob("*.md"))
    total_bytes = sum(f.stat().st_size for f in md_files)
    write_md(
        OUT / "INDEX.md",
        std_header("# INDEX", "§4-5", "Appendix", "INDEX.md", "신규"),
        index_body_fn(len(md_files) + 1, total_bytes + 4096),
    )
    md_files = sorted(OUT.rglob("*.md"))
    total_bytes = sum(f.stat().st_size for f in md_files)
    write_md(
        OUT / "INDEX.md",
        std_header("# INDEX", "§4-5", "Appendix", "INDEX.md", "신규"),
        index_body_fn(len(md_files), total_bytes),
    )
    print(f"Wrote {len(md_files)} markdown files under {OUT}")
    print(f"Total size ~{total_bytes // 1024} KB")


if __name__ == "__main__":
    main()
