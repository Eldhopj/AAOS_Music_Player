package com.example.aaos.music.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aaos.music.domain.repository.PlayerRepository
import com.example.aaos.music.domain.usecase.GetLocalSongsUseCase
import com.example.aaos.music.domain.usecase.PlayerIntent
import com.example.aaos.music.domain.usecase.SendPlayerIntentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val sendPlayerIntentUseCase: SendPlayerIntentUseCase,
    private val getLocalSongsUseCase: GetLocalSongsUseCase,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerState(isLoading = true))
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    init {
        observePlayerState()
    }

    fun loadLocalMusic() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                val songs = getLocalSongsUseCase()
                playerRepository.setMediaItems(songs)
                _state.update { it.copy(isLoading = false, queue = songs) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun handleEvent(event: PlayerEvent) {
        viewModelScope.launch {
            when (event) {
                PlayerEvent.PlayPauseClicked -> togglePlayPause()
                PlayerEvent.SkipNextClicked -> sendPlayerIntentUseCase(PlayerIntent.SkipNext)
                PlayerEvent.SkipPrevClicked -> sendPlayerIntentUseCase(PlayerIntent.SkipPrev)
                is PlayerEvent.SeekTo -> sendPlayerIntentUseCase(PlayerIntent.SeekTo(event.position))
                is PlayerEvent.PlayByIndex -> sendPlayerIntentUseCase(PlayerIntent.PlayByIndex(event.index))
                PlayerEvent.ToggleShuffle -> toggleShuffle()
                PlayerEvent.CycleRepeat -> cycleRepeat()
            }
        }
    }

    private fun toggleShuffle() {
        viewModelScope.launch {
            val isShuffle = _state.value.isShuffleEnabled
            sendPlayerIntentUseCase(PlayerIntent.SetShuffle(!isShuffle))
        }
    }

    private fun cycleRepeat() {
        viewModelScope.launch {
            // Repeat modes: 0 = OFF, 1 = ONE, 2 = ALL (Media3 constants)
            // Cycle: OFF -> ALL -> ONE -> OFF
            val currentMode = _state.value.repeatMode
            val newMode = when (currentMode) {
                0 -> 2 // OFF -> ALL
                2 -> 1 // ALL -> ONE
                else -> 0 // ONE -> OFF
            }
            sendPlayerIntentUseCase(PlayerIntent.SetRepeat(newMode))
        }
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            playerRepository.currentTrack.collect { track ->
                _state.update { it.copy(currentTrack = track) }
            }
        }
        viewModelScope.launch {
            playerRepository.isPlaying.collect { isPlaying ->
                _state.update { it.copy(isPlaying = isPlaying) }
            }
        }
        viewModelScope.launch {
            playerRepository.playbackPosition.collect { position ->
                _state.update { it.copy(playbackPosition = position) }
            }
        }
        viewModelScope.launch {
            playerRepository.shuffleModeEnabled.collect { enabled ->
                _state.update { it.copy(isShuffleEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            playerRepository.repeatMode.collect { mode ->
                _state.update { it.copy(repeatMode = mode) }
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
