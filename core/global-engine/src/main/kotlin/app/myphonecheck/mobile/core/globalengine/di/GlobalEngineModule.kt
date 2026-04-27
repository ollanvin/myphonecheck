package app.myphonecheck.mobile.core.globalengine.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * :core:global-engine Hilt 모듈 (Architecture v2.0.0 §30).
 *
 * 모든 클래스가 @Singleton + @Inject constructor 패턴이므로 명시적 @Provides 불필요:
 *  - CurrencyAmountParser, CurrencyValidator, SourceDetector (parsing/currency/)
 *  - SourceLabelCache, LabelingService (parsing/currency/learning/)
 *  - SimContextProvider, SimChangeDetector (simcontext/)
 *  - CountryCurrencyMapper (object — singleton, DI 불필요)
 *  - UiLanguageResolver (사용자 preference 의존, factory 또는 사용처에서 instantiate)
 *
 * 향후 추가 인터페이스/팩토리가 생기면 본 모듈에 @Provides 추가.
 */
@Module
@InstallIn(SingletonComponent::class)
object GlobalEngineModule
