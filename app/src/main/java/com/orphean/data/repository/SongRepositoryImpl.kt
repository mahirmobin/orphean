package com.orphean.data.repository

import com.orphean.data.local.dao.SongDao
import com.orphean.data.scanner.MediaStoreScanner
import com.orphean.domain.model.Song
import com.orphean.domain.repository.SongRepository
import com.orphean.data.local.entity.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepositoryImpl @Inject constructor(
    private val songDao: SongDao,
    private val mediaScanner: MediaStoreScanner
) : SongRepository {

    override fun getAllSongs(): Flow<List<Song>> {
        return songDao.getAllSongs().map { entities -> 
            entities.map { it.toDomain() } 
        }
    }

    override fun getFavoriteSongs(): Flow<List<Song>> {
        return songDao.getFavoriteSongs().map { entities -> 
            entities.map { it.toDomain() } 
        }
    }

    override fun getMostPlayedSongs(limit: Int): Flow<List<Song>> {
        return songDao.getMostPlayedSongs(limit).map { entities -> 
            entities.map { it.toDomain() } 
        }
    }

    override suspend fun syncMediaScanner() {
        val deviceSongs = mediaScanner.scanAudioFiles()
        
        val existingSongs = songDao.getAllSongsSync()
        val existingMap = existingSongs.associateBy { it.id }
        
        val finalSongs = deviceSongs.map { deviceSong ->
            val existing = existingMap[deviceSong.id]
            if (existing != null) {
                deviceSong.copy(
                    playCount = existing.playCount,
                    isFavorite = existing.isFavorite
                )
            } else {
                deviceSong
            }
        }
        
        songDao.clearSongs()
        songDao.insertSongs(finalSongs)
    }

    override suspend fun toggleFavorite(song: Song) {
        songDao.updateSong(song.copy(isFavorite = !song.isFavorite).toEntity())
    }

    override suspend fun incrementPlayCount(song: Song) {
        songDao.updateSong(song.copy(playCount = song.playCount + 1).toEntity())
    }
}
