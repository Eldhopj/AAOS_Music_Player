package com.example.aaos.music.feature.player

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aaos.music.domain.repository.Track

@Composable
fun MiniPlayer(
    state: PlayerState,
    onEvent: (PlayerEvent) -> Unit,
    isLhd: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLhd) {
            // Driver on Left -> Controls on Left (closest to driver?) or Right (closest to center stack?)
            // Usually "closest to driver" means the side of the screen closest to them.
            // If screen is to the right of driver (LHD), the LEFT side of the screen is closest.
            PlayPauseButton(isPlaying = state.isPlaying, onClick = { onEvent(PlayerEvent.PlayPauseClicked) })
            Spacer(modifier = Modifier.width(16.dp))
            TrackInfo(state.currentTrack)
        } else {
            // Driver on Right -> Screen to left of driver. RIGHT side of screen is closest.
            TrackInfo(state.currentTrack)
            Spacer(modifier = Modifier.weight(1f))
            PlayPauseButton(isPlaying = state.isPlaying, onClick = { onEvent(PlayerEvent.PlayPauseClicked) })
        }
    }
}

@Composable
fun PlayPauseButton(isPlaying: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = if (isPlaying) "Pause" else "Play")
    }
}

@Composable
fun TrackInfo(track: Track?) {
    Column {
        Text(
            text = track?.title ?: "No Track",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = track?.artist ?: "Unknown Artist",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
