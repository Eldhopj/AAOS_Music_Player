package com.example.aaos.music.domain.repository

import kotlinx.coroutines.flow.Flow

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val albumArtUrl: String? = null,
    val album: String? = null,
    val duration: Long = 0L
)

interface PlayerRepository {
    val currentTrack: Flow<Track?>
    val isPlaying: Flow<Boolean>
    val playbackPosition: Flow<Long>
    val shuffleModeEnabled: Flow<Boolean>
    val repeatMode: Flow<Int>
    
    suspend fun play()
    suspend fun pause()
    suspend fun skipNext()
    suspend fun skipPrevious()
    suspend fun seekTo(position: Long)
    suspend fun playByIndex(index: Int)
    suspend fun setShuffleMode(enabled: Boolean)
    suspend fun setRepeatMode(mode: Int)
    suspend fun setMediaItems(songs: List<com.example.aaos.music.domain.model.LocalSong>)
}
