# 40_i18n/ — README

## 목적
i18n Layer — 국가/언어 분리, strings.xml 명세.

## 책임 범위
원본 §25 strings.xml + §28 국가/언어 분리. UI 언어 ≠ 판정 언어 분리 축.

## 외부 인터페이스
`07_engine/02_self_discovery.md` (ClusterProfile 국가 판정), `10_policy/07_country_i18n.md` (Policy 관점 동일 원본).

## 내부 파일 안내
- `01_country_separation.md` — §28. 국가/언어 분리 (Patch 05).
- `02_strings_xml.md` — §25. strings.xml 다국어 자원 명세.
