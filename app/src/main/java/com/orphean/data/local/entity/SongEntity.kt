package com.orphean.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.orphean.domain.model.Song

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artistId: Long,
    val artistName: String,
    val albumId: Long,
    val albumName: String,
    val durationMs: Long,
    val data: String,
    val trackNumber: Int,
    val dateAdded: Long,
    val albumArtUri: String?,
    val playCount: Int = 0,
    val isFavorite: Boolean = false
) {
    fun toDomain() = Song(
        id = id,
        title = title,
        artistId = artistId,
        artistName = artistName,
        albumId = albumId,
        albumName = albumName,
        durationMs = durationMs,
        data = data,
        trackNumber = trackNumber,
        dateAdded = dateAdded,
        albumArtUri = albumArtUri,
        playCount = playCount,
        isFavorite = isFavorite
    )
}

fun Song.toEntity() = SongEntity(
    id = id,
    title = title,
    artistId = artistId,
    artistName = artistName,
    albumId = albumId,
    albumName = albumName,
    durationMs = durationMs,
    data = data,
    trackNumber = trackNumber,
    dateAdded = dateAdded,
    albumArtUri = albumArtUri,
    playCount = playCount,
    isFavorite = isFavorite
)
