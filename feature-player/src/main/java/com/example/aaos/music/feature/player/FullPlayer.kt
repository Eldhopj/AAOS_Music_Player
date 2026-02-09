package com.example.aaos.music.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FullPlayer(
    state: PlayerState,
    onEvent: (PlayerEvent) -> Unit,
    isLhd: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLhd) {
            // Driver on Left. Center stack to their right.
            // Controls should be closer to driver (Left side of screen).
            // Art on Right.
            PlayerControlsSection(
                state = state, 
                onEvent = onEvent, 
                modifier = Modifier.weight(1f)
            )
            AlbumArtSection(
                modifier = Modifier.weight(1f)
            )
        } else {
            // Driver on Right. Center stack to their left.
            // Controls should be closer to driver (Right side of screen).
            // Art on Left.
            AlbumArtSection(
                modifier = Modifier.weight(1f)
            )
            PlayerControlsSection(
                state = state, 
                onEvent = onEvent, 
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AlbumArtSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Text("Album Art", color = Color.White)
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
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = state.currentTrack?.artist ?: "Unknown Artist",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Slider(
            value = 0.3f, 
            onValueChange = { /* seek event */ },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onEvent(PlayerEvent.SkipPrevClicked) }) {
                Text("Prev") 
            }
            
            Button(
                onClick = { onEvent(PlayerEvent.PlayPauseClicked) },
                modifier = Modifier.size(64.dp)
            ) {
                Text(if (state.isPlaying) "||" else ">")
            }
            
            IconButton(onClick = { onEvent(PlayerEvent.SkipNextClicked) }) {
                Text("Next")
            }
        }
    }
}
