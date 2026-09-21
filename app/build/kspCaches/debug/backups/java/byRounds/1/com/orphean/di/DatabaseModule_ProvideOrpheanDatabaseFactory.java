package com.orphean.di;

import android.content.Context;
import com.orphean.data.local.OrpheanDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideOrpheanDatabaseFactory implements Factory<OrpheanDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideOrpheanDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public OrpheanDatabase get() {
    return provideOrpheanDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvideOrpheanDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideOrpheanDatabaseFactory(contextProvider);
  }

  public static OrpheanDatabase provideOrpheanDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideOrpheanDatabase(context));
  }
}
