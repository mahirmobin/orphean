package com.orphean.domain.model

data class Album(
    val title: String,
    val artistName: String,
    val albumArtUri: String?,
    val songs: List<Song>
)
