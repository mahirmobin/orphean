package com.orphean.data.scanner

import android.content.Context
import android.provider.MediaStore
import com.orphean.data.local.entity.SongEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStoreScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun scanAudioFiles(): List<SongEntity> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<SongEntity>()
        
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DATE_ADDED
        )
        
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        
        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val rawTitle = cursor.getString(titleColumn) ?: "Unknown"
                val title = if (rawTitle.endsWith(".mp3", ignoreCase = true) || rawTitle.endsWith(".m4a", ignoreCase = true) || rawTitle.endsWith(".wav", ignoreCase = true) || rawTitle.endsWith(".flac", ignoreCase = true)) {
                    rawTitle.substringBeforeLast(".")
                } else rawTitle
                
                val artistId = cursor.getLong(artistIdColumn)
                var artist = cursor.getString(artistColumn) ?: ""
                if (artist == "<unknown>" || artist.lowercase() == "unknown") artist = ""
                
                if (artist.isBlank()) continue
                
                val albumId = cursor.getLong(albumIdColumn)
                var album = cursor.getString(albumColumn) ?: ""
                if (album == "<unknown>" || album.lowercase() == "unknown") album = ""
                val duration = cursor.getLong(durationColumn)
                val data = cursor.getString(dataColumn)
                val track = cursor.getInt(trackColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)
                
                val albumArtUri = "content://media/external/audio/albumart/$albumId"
                
                songs.add(
                    SongEntity(
                        id = id,
                        title = title,
                        artistId = artistId,
                        artistName = artist,
                        albumId = albumId,
                        albumName = album,
                        durationMs = duration,
                        data = data,
                        trackNumber = track,
                        dateAdded = dateAdded,
                        albumArtUri = albumArtUri
                    )
                )
            }
        }
        
        songs
    }
}
