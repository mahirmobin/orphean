package com.orphean.presentation.home;

import com.orphean.data.local.SettingsRepository;
import com.orphean.domain.repository.SongRepository;
import com.orphean.domain.usecase.GetSongsUseCase;
import com.orphean.domain.usecase.SyncMediaUseCase;
import com.orphean.player.PlayerController;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GetSongsUseCase> getSongsUseCaseProvider;

  private final Provider<SyncMediaUseCase> syncMediaUseCaseProvider;

  private final Provider<SongRepository> repositoryProvider;

  private final Provider<PlayerController> playerControllerProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public HomeViewModel_Factory(Provider<GetSongsUseCase> getSongsUseCaseProvider,
      Provider<SyncMediaUseCase> syncMediaUseCaseProvider,
      Provider<SongRepository> repositoryProvider,
      Provider<PlayerController> playerControllerProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.getSongsUseCaseProvider = getSongsUseCaseProvider;
    this.syncMediaUseCaseProvider = syncMediaUseCaseProvider;
    this.repositoryProvider = repositoryProvider;
    this.playerControllerProvider = playerControllerProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getSongsUseCaseProvider.get(), syncMediaUseCaseProvider.get(), repositoryProvider.get(), playerControllerProvider.get(), settingsRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<GetSongsUseCase> getSongsUseCaseProvider,
      Provider<SyncMediaUseCase> syncMediaUseCaseProvider,
      Provider<SongRepository> repositoryProvider,
      Provider<PlayerController> playerControllerProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new HomeViewModel_Factory(getSongsUseCaseProvider, syncMediaUseCaseProvider, repositoryProvider, playerControllerProvider, settingsRepositoryProvider);
  }

  public static HomeViewModel newInstance(GetSongsUseCase getSongsUseCase,
      SyncMediaUseCase syncMediaUseCase, SongRepository repository,
      PlayerController playerController, SettingsRepository settingsRepository) {
    return new HomeViewModel(getSongsUseCase, syncMediaUseCase, repository, playerController, settingsRepository);
  }
}
