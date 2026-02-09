package com.example.aaos.music.feature.player

import com.example.aaos.music.domain.repository.Track

data class PlayerState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val playbackPosition: Long = 0L,
    val error: String? = null
)

sealed class PlayerEvent {
    object PlayPauseClicked : PlayerEvent()
    object SkipNextClicked : PlayerEvent()
    object SkipPrevClicked : PlayerEvent()
    data class SeekTo(val position: Long) : PlayerEvent()
}
