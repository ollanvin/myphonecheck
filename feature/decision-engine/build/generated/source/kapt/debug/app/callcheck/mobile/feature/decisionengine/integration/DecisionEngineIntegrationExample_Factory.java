package app.callcheck.mobile.feature.decisionengine.integration;

import app.callcheck.mobile.feature.decisionengine.DecisionEngine;
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
public final class DecisionEngineIntegrationExample_Factory implements Factory<DecisionEngineIntegrationExample> {
  private final Provider<DecisionEngine> decisionEngineProvider;

  public DecisionEngineIntegrationExample_Factory(Provider<DecisionEngine> decisionEngineProvider) {
    this.decisionEngineProvider = decisionEngineProvider;
  }

  @Override
  public DecisionEngineIntegrationExample get() {
    return newInstance(decisionEngineProvider.get());
  }

  public static DecisionEngineIntegrationExample_Factory create(
      Provider<DecisionEngine> decisionEngineProvider) {
    return new DecisionEngineIntegrationExample_Factory(decisionEngineProvider);
  }

  public static DecisionEngineIntegrationExample newInstance(DecisionEngine decisionEngine) {
    return new DecisionEngineIntegrationExample(decisionEngine);
  }
}
