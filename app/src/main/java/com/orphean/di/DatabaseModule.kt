package com.orphean.di

import android.content.Context
import androidx.room.Room
import com.orphean.data.local.OrpheanDatabase
import com.orphean.data.local.dao.SongDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideOrpheanDatabase(@ApplicationContext context: Context): OrpheanDatabase {
        return Room.databaseBuilder(
            context,
            OrpheanDatabase::class.java,
            "orphean_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSongDao(db: OrpheanDatabase): SongDao {
        return db.songDao
    }
}
