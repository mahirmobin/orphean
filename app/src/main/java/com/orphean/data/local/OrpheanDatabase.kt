package com.orphean.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.orphean.data.local.entity.SongEntity
import com.orphean.data.local.dao.SongDao

@Database(
    entities = [SongEntity::class],
    version = 1,
    exportSchema = false
)
abstract class OrpheanDatabase : RoomDatabase() {
    abstract val songDao: SongDao
}
