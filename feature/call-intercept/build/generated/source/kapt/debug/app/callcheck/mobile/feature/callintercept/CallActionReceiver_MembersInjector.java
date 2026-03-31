package app.callcheck.mobile.feature.callintercept;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class CallActionReceiver_MembersInjector implements MembersInjector<CallActionReceiver> {
  private final Provider<DecisionNotificationManager> decisionNotificationManagerProvider;

  private final Provider<BlocklistRepository> blocklistRepositoryProvider;

  public CallActionReceiver_MembersInjector(
      Provider<DecisionNotificationManager> decisionNotificationManagerProvider,
      Provider<BlocklistRepository> blocklistRepositoryProvider) {
    this.decisionNotificationManagerProvider = decisionNotificationManagerProvider;
    this.blocklistRepositoryProvider = blocklistRepositoryProvider;
  }

  public static MembersInjector<CallActionReceiver> create(
      Provider<DecisionNotificationManager> decisionNotificationManagerProvider,
      Provider<BlocklistRepository> blocklistRepositoryProvider) {
    return new CallActionReceiver_MembersInjector(decisionNotificationManagerProvider, blocklistRepositoryProvider);
  }

  @Override
  public void injectMembers(CallActionReceiver instance) {
    injectDecisionNotificationManager(instance, decisionNotificationManagerProvider.get());
    injectBlocklistRepository(instance, blocklistRepositoryProvider.get());
  }

  @InjectedFieldSignature("app.callcheck.mobile.feature.callintercept.CallActionReceiver.decisionNotificationManager")
  public static void injectDecisionNotificationManager(CallActionReceiver instance,
      DecisionNotificationManager decisionNotificationManager) {
    instance.decisionNotificationManager = decisionNotificationManager;
  }

  @InjectedFieldSignature("app.callcheck.mobile.feature.callintercept.CallActionReceiver.blocklistRepository")
  public static void injectBlocklistRepository(CallActionReceiver instance,
      BlocklistRepository blocklistRepository) {
    instance.blocklistRepository = blocklistRepository;
  }
}
