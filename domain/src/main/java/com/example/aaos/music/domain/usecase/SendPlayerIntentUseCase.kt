package com.example.aaos.music.domain.usecase

import com.example.aaos.music.domain.repository.PlayerRepository
import javax.inject.Inject

sealed class PlayerIntent {
    object Play : PlayerIntent()
    object Pause : PlayerIntent()
    object SkipNext : PlayerIntent()
    object SkipPrev : PlayerIntent()
    data class SeekTo(val position: Long) : PlayerIntent()
    data class PlayByIndex(val index: Int) : PlayerIntent()
    data class SetShuffle(val enabled: Boolean) : PlayerIntent()
    data class SetRepeat(val mode: Int) : PlayerIntent()
}

class SendPlayerIntentUseCase @Inject constructor(
    private val repository: PlayerRepository
) {
    suspend operator fun invoke(intent: PlayerIntent) {
        when (intent) {
            PlayerIntent.Play -> repository.play()
            PlayerIntent.Pause -> repository.pause()
            PlayerIntent.SkipNext -> repository.skipNext()
            PlayerIntent.SkipPrev -> repository.skipPrevious()
            is PlayerIntent.SeekTo -> repository.seekTo(intent.position)
            is PlayerIntent.PlayByIndex -> repository.playByIndex(intent.index)
            is PlayerIntent.SetShuffle -> repository.setShuffleMode(intent.enabled)
            is PlayerIntent.SetRepeat -> repository.setRepeatMode(intent.mode)
        }
    }
}
