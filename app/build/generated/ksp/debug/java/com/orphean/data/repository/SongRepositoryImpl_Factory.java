package com.orphean.data.repository;

import com.orphean.data.local.dao.SongDao;
import com.orphean.data.scanner.MediaStoreScanner;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
    "KotlinInternalInJava"
})
public final class SongRepositoryImpl_Factory implements Factory<SongRepositoryImpl> {
  private final Provider<SongDao> songDaoProvider;

  private final Provider<MediaStoreScanner> mediaScannerProvider;

  public SongRepositoryImpl_Factory(Provider<SongDao> songDaoProvider,
      Provider<MediaStoreScanner> mediaScannerProvider) {
    this.songDaoProvider = songDaoProvider;
    this.mediaScannerProvider = mediaScannerProvider;
  }

  @Override
  public SongRepositoryImpl get() {
    return newInstance(songDaoProvider.get(), mediaScannerProvider.get());
  }

  public static SongRepositoryImpl_Factory create(Provider<SongDao> songDaoProvider,
      Provider<MediaStoreScanner> mediaScannerProvider) {
    return new SongRepositoryImpl_Factory(songDaoProvider, mediaScannerProvider);
  }

  public static SongRepositoryImpl newInstance(SongDao songDao, MediaStoreScanner mediaScanner) {
    return new SongRepositoryImpl(songDao, mediaScanner);
  }
}
