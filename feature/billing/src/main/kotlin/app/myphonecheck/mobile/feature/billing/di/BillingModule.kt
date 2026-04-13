package app.myphonecheck.mobile.feature.billing.di

import android.content.Context
import app.myphonecheck.mobile.core.security.tamper.TamperChecker
import app.myphonecheck.mobile.feature.billing.BillingManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Provides
    @Singleton
    fun provideBillingManager(
        @ApplicationContext context: Context,
        tamperChecker: TamperChecker,
    ): BillingManager {
        return BillingManager(context, tamperChecker)
    }
}
