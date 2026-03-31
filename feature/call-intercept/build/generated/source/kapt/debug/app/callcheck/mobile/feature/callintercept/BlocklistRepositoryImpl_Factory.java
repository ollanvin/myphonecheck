package app.callcheck.mobile.feature.callintercept;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class BlocklistRepositoryImpl_Factory implements Factory<BlocklistRepositoryImpl> {
  @Override
  public BlocklistRepositoryImpl get() {
    return newInstance();
  }

  public static BlocklistRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BlocklistRepositoryImpl newInstance() {
    return new BlocklistRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final BlocklistRepositoryImpl_Factory INSTANCE = new BlocklistRepositoryImpl_Factory();
  }
}
