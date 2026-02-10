package com.example.aaos.music.domain.model

data class LocalSong(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val contentUri: String,
    val albumArtUri: String?
)
