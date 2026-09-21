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
public final class GetSongsUseCase_Factory implements Factory<GetSongsUseCase> {
  private final Provider<SongRepository> repositoryProvider;

  public GetSongsUseCase_Factory(Provider<SongRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetSongsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetSongsUseCase_Factory create(Provider<SongRepository> repositoryProvider) {
    return new GetSongsUseCase_Factory(repositoryProvider);
  }

  public static GetSongsUseCase newInstance(SongRepository repository) {
    return new GetSongsUseCase(repository);
  }
}
