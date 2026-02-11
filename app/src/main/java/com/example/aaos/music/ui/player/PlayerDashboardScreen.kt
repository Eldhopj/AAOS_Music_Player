package com.example.aaos.music.ui.player

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aaos.music.core.ui.R
import com.example.aaos.music.core.ui.components.album.AlbumArt
import com.example.aaos.music.core.ui.components.animation.slowBoundsTransform
import com.example.aaos.music.core.ui.components.sliders.MusicProgressBar
import com.example.aaos.music.ui.player.components.PlayerControls
import com.example.aaos.music.ui.player.components.QueueList
import com.example.aaos.music.ui.player.components.SongInfo

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PlayerDashboardScreen(
    onShrink: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isLhd by viewModel.isLhd.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadLocalMusic()
    }

    PlayerDashboardContent(
        state = state,
        onEvent = viewModel::handleEvent,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        onShrink = onShrink,
        isLhd = isLhd
    )

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PlayerDashboardContent(
    state: PlayerState,
    onShrink: () -> Unit,
    onEvent: (PlayerEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isLhd: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {

        if (isLhd) {
            MediaControlPanel(
                modifier = Modifier.weight(0.6f),
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                state = state,
                onShrink = onShrink,
                onEvent = onEvent
            )

            MediaSongList(
                state = state,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                onEvent = onEvent,
                modifier = Modifier.weight(0.4f)

            )
        } else {

            MediaSongList(
                state = state,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                onEvent = onEvent,
                modifier = Modifier.weight(0.4f)
            )

            MediaControlPanel(
                modifier = Modifier.weight(0.6f),
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                state = state,
                onShrink = onShrink,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun MediaSongList(
    state: PlayerState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onEvent: (PlayerEvent) -> Unit,
    modifier: Modifier,
) {
    val cornerRadius: Int = 24
    val shape = RoundedCornerShape(cornerRadius.dp)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(32.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2F374C), // top
                        Color(0xFF000000)  // bottom
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFF68696E),
                shape = shape,
            )
    ) {
        QueueList(
            queue = state.queue,
            currentTrackId = state.currentTrack?.id?.toLongOrNull(),
            onTrackClick = { index -> onEvent(PlayerEvent.PlayByIndex(index)) },
            modifier = Modifier.fillMaxSize(),
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope
        )
    }
}

@Composable
private fun MediaControlPanel(
    state: PlayerState,
    onShrink: () -> Unit,
    onEvent: (PlayerEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier
) {

    with(sharedTransitionScope) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .padding(32.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "container"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut()
                ),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section (Header / Status)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End, // Align to Right
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.media_source),
                    contentDescription = null,
                    modifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(key = "source"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = slowBoundsTransform
                    ),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(16.dp))
                Image(
                    modifier = Modifier
                        .padding(1.dp)
                        .width(48.dp)
                        .height(54.dp)
                        .clickable { onShrink() }
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "minmax"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        ),
                    painter = painterResource(id = R.drawable.maximise),
                    contentDescription = "image description",
                    contentScale = ContentScale.None
                )
            }

            // Main Content: Album Art + Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AlbumArt(
                    imageUrl = state.currentTrack?.albumArtUrl,
                    modifier = Modifier.size(200.dp), // Large art
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )

                Spacer(modifier = Modifier.width(24.dp))

                SongInfo(
                    title = state.currentTrack?.title ?: "Select a song",
                    artist = state.currentTrack?.artist ?: "",
                    album = null, // Track doesn't seem to have album field in shared code, inferred or null
                    modifier = Modifier.weight(1f),
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
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
                        text = formatTime(if (isDragging) sliderPosition.toLong() else position),
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
                    isRepeatDisplay = true,
                    isShuffleDisplay = true,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier.fillMaxWidth()
                )
            }
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


