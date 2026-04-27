package app.myphonecheck.mobile.feature.settings.v2.repository

/**
 * UI 언어 선호 (Architecture v2.0.0 §29 + 헌법 §8-3 3단 fallback).
 *
 * 단일 사용자 선택. 기본값은 SIM_BASED — UiLanguageResolver의 1순위 fallback과 일치.
 */
enum class UiLanguagePreference {
    /** SIM countryIso 기반 — 코어 UiLanguageResolver 1순위. */
    SIM_BASED,

    /** 디바이스 시스템 Locale — 2순위. */
    DEVICE_SYSTEM,

    /** 영문 — 3순위 (최후 fallback이자 글로벌 기본). */
    ENGLISH,
}
