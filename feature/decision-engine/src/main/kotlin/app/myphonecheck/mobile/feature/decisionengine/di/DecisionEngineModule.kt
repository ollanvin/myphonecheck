package app.myphonecheck.mobile.feature.decisionengine.di

import app.myphonecheck.mobile.feature.decisionengine.DecisionEngine
import app.myphonecheck.mobile.feature.decisionengine.DecisionEngineImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module for DecisionEngine.
 * Binds the interface to its implementation and provides singleton instances
 * of supporting components.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DecisionEngineModule {

    @Binds
    @Singleton
    abstract fun bindDecisionEngine(
        impl: DecisionEngineImpl,
    ): DecisionEngine
}
