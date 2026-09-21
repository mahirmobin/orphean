package com.orphean.domain.usecase;

import com.orphean.domain.repository.SongRepository;
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
    "KotlinInternalInJava"
})
public final class SyncMediaUseCase_Factory implements Factory<SyncMediaUseCase> {
  private final Provider<SongRepository> repositoryProvider;

  public SyncMediaUseCase_Factory(Provider<SongRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SyncMediaUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SyncMediaUseCase_Factory create(Provider<SongRepository> repositoryProvider) {
    return new SyncMediaUseCase_Factory(repositoryProvider);
  }

  public static SyncMediaUseCase newInstance(SongRepository repository) {
    return new SyncMediaUseCase(repository);
  }
}
