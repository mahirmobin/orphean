package com.orphean.di;

import com.orphean.data.local.OrpheanDatabase;
import com.orphean.data.local.dao.SongDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideSongDaoFactory implements Factory<SongDao> {
  private final Provider<OrpheanDatabase> dbProvider;

  public DatabaseModule_ProvideSongDaoFactory(Provider<OrpheanDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SongDao get() {
    return provideSongDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideSongDaoFactory create(Provider<OrpheanDatabase> dbProvider) {
    return new DatabaseModule_ProvideSongDaoFactory(dbProvider);
  }

  public static SongDao provideSongDao(OrpheanDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSongDao(db));
  }
}
