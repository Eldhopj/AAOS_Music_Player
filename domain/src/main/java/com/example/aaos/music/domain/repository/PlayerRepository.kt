package com.example.aaos.music.domain.repository

import kotlinx.coroutines.flow.Flow

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val albumArtUrl: String? = null,
    val duration: Long = 0L
)

interface PlayerRepository {
    val currentTrack: Flow<Track?>
    val isPlaying: Flow<Boolean>
    val playbackPosition: Flow<Long>
    
    suspend fun play()
    suspend fun pause()
    suspend fun skipNext()
    suspend fun skipPrevious()
    suspend fun seekTo(position: Long)
}
