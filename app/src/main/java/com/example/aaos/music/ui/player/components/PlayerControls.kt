package com.example.aaos.music.ui.player.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.aaos.music.core.ui.R
import com.example.aaos.music.core.ui.components.animation.slowBoundsTransform
import com.example.aaos.music.core.ui.components.buttons.PlayPauseButton
import com.example.aaos.music.core.ui.utils.rememberUiDimensions

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    isShuffleEnabled: Boolean,
    repeatMode: Int,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    isShuffleDisplay: Boolean = false,
    isRepeatDisplay: Boolean = false,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val dims = rememberUiDimensions()
    with(sharedTransitionScope) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isShuffleDisplay) {
                // Shuffle
                IconButton(onClick = onShuffleClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.shuffle),
                        contentDescription = "Shuffle",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Previous
            IconButton(
                onClick = onPreviousClick,
                modifier = Modifier.size(dims.previousIconButton)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.previous),
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(dims.previousIcon).sharedElement(
                        sharedContentState = rememberSharedContentState(key = "previous"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = slowBoundsTransform
                    )
                )
            }

            // Play/Pause
            PlayPauseButton(
                isPlaying = isPlaying,
                onClick = onPlayPauseClick,
                modifier = Modifier.size(dims.playPauseIconButton).sharedElement(
                    sharedContentState = rememberSharedContentState(key = "play"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = slowBoundsTransform
                )
            )

            // Next
            IconButton(
                onClick = onNextClick,
                modifier = Modifier.size(dims.nextIconButton)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(dims.nextIcon).sharedElement(
                        sharedContentState = rememberSharedContentState(key = "next"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = slowBoundsTransform
                    )
                )
            }

            if (isRepeatDisplay) {
                // Repeat
                IconButton(onClick = onRepeatClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.repeat),
                        contentDescription = "Repeat",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
