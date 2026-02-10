package com.example.aaos.music.ui.player

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aaos.music.core.ui.components.album.AlbumArt
import com.example.aaos.music.core.ui.components.sliders.MusicProgressBar
import com.example.aaos.music.core.ui.theme.CarMusicTheme
import com.example.aaos.music.ui.player.components.PlayerControls
import com.example.aaos.music.ui.player.components.QueueList
import com.example.aaos.music.ui.player.components.SongInfo

@Composable
fun PlayerDashboardScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadLocalMusic()
    }

    PlayerDashboardContent(
        state = state,
        onEvent = viewModel::handleEvent
    )
}

@Composable
fun PlayerDashboardContent(
    state: PlayerState,
    onEvent: (PlayerEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // LEFT PANE (60%)
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section (Header / Status) - dynamic based on design, simplified here
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                 // Placeholder for USB/Source indicators if needed
                 Spacer(modifier = Modifier.size(1.dp))
            }

            // Main Content: Album Art + Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AlbumArt(
                    imageUrl = state.currentTrack?.albumArtUrl,
                    modifier = Modifier.size(200.dp) // Large art
                )

                Spacer(modifier = Modifier.width(24.dp))

                SongInfo(
                    title = state.currentTrack?.title ?: "Select a song",
                    artist = state.currentTrack?.artist ?: "",
                    album = null, // Track doesn't seem to have album field in shared code, inferred or null
                    modifier = Modifier.weight(1f)
                )
            }

            // Bottom Section: Seekbar + Controls
            Column(modifier = Modifier.fillMaxWidth()) {
                val duration = state.currentTrack?.duration ?: 1L
                val position = state.playbackPosition.coerceAtMost(duration)
                var sliderPosition by remember(position) { mutableStateOf(position.toFloat()) }
                var isDragging by remember { mutableStateOf(false) }

                // Timestamps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(if(isDragging) sliderPosition.toLong() else position),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTime(duration),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                MusicProgressBar(
                    progress = if (duration > 0) (if (isDragging) sliderPosition else position.toFloat()) / duration else 0f,
                    onValueChange = { 
                        isDragging = true
                        sliderPosition = it * duration
                    },
                    onValueChangeFinished = {
                        onEvent(PlayerEvent.SeekTo(sliderPosition.toLong()))
                        isDragging = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                PlayerControls(
                    isPlaying = state.isPlaying,
                    isShuffleEnabled = state.isShuffleEnabled,
                    repeatMode = state.repeatMode,
                    onPlayPauseClick = { onEvent(PlayerEvent.PlayPauseClicked) },
                    onPreviousClick = { onEvent(PlayerEvent.SkipPrevClicked) },
                    onNextClick = { onEvent(PlayerEvent.SkipNextClicked) },
                    onShuffleClick = { onEvent(PlayerEvent.ToggleShuffle) },
                    onRepeatClick = { onEvent(PlayerEvent.CycleRepeat) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // VERTICAL DIVIDER (Optional, but good for separation)
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.outlineVariant,
                            Color.Transparent
                        )
                    )
                )
        )

        // RIGHT PANE (40%)
        Box(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)) // Slightly different bg for pane
        ) {
            QueueList(
                queue = state.queue,
                currentTrackId = state.currentTrack?.id?.toLongOrNull(),
                onTrackClick = { index -> onEvent(PlayerEvent.PlayByIndex(index)) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

// PREVIEWS

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 1024, heightDp = 768)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun PlayerDashboardPreview() {
    CarMusicTheme(darkTheme = true) {
        PlayerDashboardContent(
            state = PlayerState(
                currentTrack = com.example.aaos.music.domain.repository.Track(
                    id = "1",
                    title = "Shape of You",
                    artist = "Ed Sheeran",
                    duration = 240000L
                ),
                isPlaying = true,
                playbackPosition = 60000L,
                queue = listOf(
                    com.example.aaos.music.domain.model.LocalSong(1, "Song 1", "Artist 1", "Album 1", 200000, "", null),
                    com.example.aaos.music.domain.model.LocalSong(2, "Song 2", "Artist 2", "Album 2", 200000, "", null),
                    com.example.aaos.music.domain.model.LocalSong(3, "Song 3", "Artist 3", "Album 3", 200000, "", null)
                )
            ),
            onEvent = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light Mode")
@Composable
fun PlayerDashboardLightPreview() {
    CarMusicTheme(darkTheme = false) {
        Surface {
           PlayerDashboardContent(
            state = PlayerState(
                 currentTrack = com.example.aaos.music.domain.repository.Track(
                    id = "1",
                    title = "Light Mode Song",
                    artist = "Artist Name",
                    duration = 180000L
                )
            ),
            onEvent = {}
        ) 
        }
    }
}
