package com.orphean.domain.model

data class Song(
    val id: Long,
    val title: String,
    val artistId: Long,
    val artistName: String,
    val albumId: Long,
    val albumName: String,
    val durationMs: Long,
    val data: String,
    val trackNumber: Int,
    val dateAdded: Long,
    val albumArtUri: String? = null,
    val playCount: Int = 0,
    val isFavorite: Boolean = false
)
