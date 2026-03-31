package app.callcheck.mobile.feature.callintercept;

import app.callcheck.mobile.feature.deviceevidence.DeviceEvidenceRepository;
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
public final class DeviceEvidenceProviderImpl_Factory implements Factory<DeviceEvidenceProviderImpl> {
  private final Provider<DeviceEvidenceRepository> deviceEvidenceRepositoryProvider;

  public DeviceEvidenceProviderImpl_Factory(
      Provider<DeviceEvidenceRepository> deviceEvidenceRepositoryProvider) {
    this.deviceEvidenceRepositoryProvider = deviceEvidenceRepositoryProvider;
  }

  @Override
  public DeviceEvidenceProviderImpl get() {
    return newInstance(deviceEvidenceRepositoryProvider.get());
  }

  public static DeviceEvidenceProviderImpl_Factory create(
      Provider<DeviceEvidenceRepository> deviceEvidenceRepositoryProvider) {
    return new DeviceEvidenceProviderImpl_Factory(deviceEvidenceRepositoryProvider);
  }

  public static DeviceEvidenceProviderImpl newInstance(
      DeviceEvidenceRepository deviceEvidenceRepository) {
    return new DeviceEvidenceProviderImpl(deviceEvidenceRepository);
  }
}
