package com.example.aaos.music.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.aaos.music.core.ui.components.buttons.PlayPauseButton
import com.example.aaos.music.core.ui.components.sliders.MusicProgressBar
import com.example.aaos.music.core.ui.theme.CarMusicTheme

@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
    isLhd: Boolean = true // Default to LHD, can be passed from config if needed
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadLocalMusic()
    }

    CarMusicTheme {
        Row(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                }
            } else {
                if (isLhd) {
                    PlayerControlsSection(
                        state = state,
                        onEvent = viewModel::handleEvent,
                        modifier = Modifier.weight(1f)
                    )
                    AlbumArtSection(
                        albumArtUrl = state.currentTrack?.albumArtUrl,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    AlbumArtSection(
                        albumArtUrl = state.currentTrack?.albumArtUrl,
                        modifier = Modifier.weight(1f)
                    )
                    PlayerControlsSection(
                        state = state,
                        onEvent = viewModel::handleEvent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun AlbumArtSection(
    albumArtUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        if (albumArtUrl != null) {
            AsyncImage(
                model = albumArtUrl,
                contentDescription = "Album Art",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text("No Art", color = Color.White)
        }
    }
}

@Composable
fun PlayerControlsSection(
    state: PlayerState,
    onEvent: (PlayerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.currentTrack?.title ?: "No Track",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = state.currentTrack?.artist ?: "Unknown Artist",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        val duration = state.currentTrack?.duration ?: 1L
        var isDragging by remember { mutableStateOf(false) }
        var sliderValue by remember { mutableFloatStateOf(0f) }

        if (!isDragging) {
            sliderValue = if (duration > 0) state.playbackPosition.toFloat() / duration.toFloat() else 0f
        }

        MusicProgressBar(
            progress = sliderValue,
            onValueChange = {
                isDragging = true
                sliderValue = it
            },
            onValueChangeFinished = {
                isDragging = false
                onEvent(PlayerEvent.SeekTo((sliderValue * duration).toLong()))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onEvent(PlayerEvent.SkipPrevClicked) }) {
                Text("Prev") // Could replace with icon
            }

            PlayPauseButton(
                isPlaying = state.isPlaying,
                onClick = { onEvent(PlayerEvent.PlayPauseClicked) }
            )

            IconButton(onClick = { onEvent(PlayerEvent.SkipNextClicked) }) {
                Text("Next") // Could replace with icon
            }
        }
    }
}
