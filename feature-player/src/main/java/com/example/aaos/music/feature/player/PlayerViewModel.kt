package com.example.aaos.music.feature.player

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
    }

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
