package com.orphean.presentation.player;

import com.orphean.domain.repository.SongRepository;
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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<PlayerController> playerControllerProvider;

  private final Provider<SongRepository> repositoryProvider;

  public PlayerViewModel_Factory(Provider<PlayerController> playerControllerProvider,
      Provider<SongRepository> repositoryProvider) {
    this.playerControllerProvider = playerControllerProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(playerControllerProvider.get(), repositoryProvider.get());
  }

  public static PlayerViewModel_Factory create(Provider<PlayerController> playerControllerProvider,
      Provider<SongRepository> repositoryProvider) {
    return new PlayerViewModel_Factory(playerControllerProvider, repositoryProvider);
  }

  public static PlayerViewModel newInstance(PlayerController playerController,
      SongRepository repository) {
    return new PlayerViewModel(playerController, repository);
  }
}
