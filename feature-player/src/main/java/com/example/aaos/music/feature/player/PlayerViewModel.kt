package com.example.aaos.music.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aaos.music.domain.usecase.PlayerIntent
import com.example.aaos.music.domain.usecase.SendPlayerIntentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val sendPlayerIntentUseCase: SendPlayerIntentUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    fun handleEvent(event: PlayerEvent) {
        viewModelScope.launch {
            when (event) {
                PlayerEvent.PlayPauseClicked -> togglePlayPause()
                PlayerEvent.SkipNextClicked -> sendPlayerIntentUseCase(PlayerIntent.SkipNext)
                PlayerEvent.SkipPrevClicked -> sendPlayerIntentUseCase(PlayerIntent.SkipPrev)
                is PlayerEvent.SeekTo -> sendPlayerIntentUseCase(PlayerIntent.SeekTo(event.position))
            }
        }
    }

    private fun togglePlayPause() {
        viewModelScope.launch {
            val isPlaying = _state.value.isPlaying
            if (isPlaying) {
                 sendPlayerIntentUseCase(PlayerIntent.Pause)
            } else {
                 sendPlayerIntentUseCase(PlayerIntent.Play)
            }
        }
    }
}
