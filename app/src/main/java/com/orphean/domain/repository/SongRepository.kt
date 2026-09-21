package com.orphean.domain.repository

import com.orphean.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getAllSongs(): Flow<List<Song>>
    fun getFavoriteSongs(): Flow<List<Song>>
    fun getMostPlayedSongs(limit: Int): Flow<List<Song>>
    suspend fun syncMediaScanner()
    suspend fun toggleFavorite(song: Song)
    suspend fun incrementPlayCount(song: Song)
}
