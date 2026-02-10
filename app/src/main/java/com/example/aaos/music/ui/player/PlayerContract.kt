package com.example.aaos.music.ui.player

import com.example.aaos.music.domain.repository.Track
import com.example.aaos.music.domain.model.LocalSong

data class PlayerState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val playbackPosition: Long = 0L,
    val queue: List<LocalSong> = emptyList(),
    val isShuffleEnabled: Boolean = false,
    val repeatMode: Int = 0, // androidx.media3.common.Player.REPEAT_MODE_OFF = 0
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class PlayerEvent {
    object PlayPauseClicked : PlayerEvent()
    object SkipNextClicked : PlayerEvent()
    object SkipPrevClicked : PlayerEvent()
    object ToggleShuffle : PlayerEvent()
    object CycleRepeat : PlayerEvent()
    data class SeekTo(val position: Long) : PlayerEvent()
    data class PlayByIndex(val index: Int) : PlayerEvent()
}
