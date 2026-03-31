package app.callcheck.mobile.feature.decisionengine;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class DecisionEngineImpl_Factory implements Factory<DecisionEngineImpl> {
  private final Provider<RiskBadgeMapper> riskBadgeMapperProvider;

  private final Provider<ActionMapper> actionMapperProvider;

  private final Provider<SummaryGenerator> summaryGeneratorProvider;

  public DecisionEngineImpl_Factory(Provider<RiskBadgeMapper> riskBadgeMapperProvider,
      Provider<ActionMapper> actionMapperProvider,
      Provider<SummaryGenerator> summaryGeneratorProvider) {
    this.riskBadgeMapperProvider = riskBadgeMapperProvider;
    this.actionMapperProvider = actionMapperProvider;
    this.summaryGeneratorProvider = summaryGeneratorProvider;
  }

  @Override
  public DecisionEngineImpl get() {
    return newInstance(riskBadgeMapperProvider.get(), actionMapperProvider.get(), summaryGeneratorProvider.get());
  }

  public static DecisionEngineImpl_Factory create(Provider<RiskBadgeMapper> riskBadgeMapperProvider,
      Provider<ActionMapper> actionMapperProvider,
      Provider<SummaryGenerator> summaryGeneratorProvider) {
    return new DecisionEngineImpl_Factory(riskBadgeMapperProvider, actionMapperProvider, summaryGeneratorProvider);
  }

  public static DecisionEngineImpl newInstance(RiskBadgeMapper riskBadgeMapper,
      ActionMapper actionMapper, SummaryGenerator summaryGenerator) {
    return new DecisionEngineImpl(riskBadgeMapper, actionMapper, summaryGenerator);
  }
}
