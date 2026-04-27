package app.myphonecheck.mobile.feature.cardcheck.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * CardCheck Hilt 모듈 (Architecture v1.9.0 §27).
 *
 * 모든 클래스가 @Singleton + @Inject constructor 패턴이므로
 * 명시적 @Provides는 현 단계에서 필요 없음.
 * (PatternExtractor, Validator, SourceDetector, SourceLabelCache, LabelingService,
 *  CardTransactionRepository — 모두 자동 발견)
 *
 * DAO 제공: data/local-cache/di/LocalCacheModule (CardTransactionDao, CardSourceLabelDao).
 *
 * 향후 추가 인터페이스/팩토리가 생기면 본 모듈에 @Provides 추가.
 */
@Module
@InstallIn(SingletonComponent::class)
object CardCheckModule
