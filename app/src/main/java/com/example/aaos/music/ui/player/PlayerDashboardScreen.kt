package com.example.aaos.music.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.autofill.contentType
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aaos.music.core.ui.R
import com.example.aaos.music.core.ui.components.album.AlbumArt
import com.example.aaos.music.core.ui.components.animation.slowBoundsTransform
import com.example.aaos.music.core.ui.components.buttons.OutlinedMediaButton
import com.example.aaos.music.core.ui.components.sliders.MusicProgressBar
import com.example.aaos.music.core.ui.theme.CarMusicTheme
import com.example.aaos.music.core.ui.theme.gradientColors
import com.example.aaos.music.core.ui.utils.rememberUiDimensions
import com.example.aaos.music.domain.model.LocalSong
import com.example.aaos.music.domain.repository.Track
import com.example.aaos.music.ui.player.components.PlayerControls
import com.example.aaos.music.ui.player.components.QueueList
import com.example.aaos.music.ui.player.components.SongInfo
import com.example.aaos.music.ui.viewmodel.PlayerViewModel

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
            .background(brush = Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.gradientColors.start,
                    MaterialTheme.gradientColors.end
                )
            ))
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
    QueueList(
        queue = state.queue,
        currentTrackId = state.currentTrack?.id?.toLongOrNull(),
        onTrackClick = { index -> onEvent(PlayerEvent.PlayByIndex(index)) },
        modifier = modifier.fillMaxSize(),
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope
    )
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
    val dims = rememberUiDimensions()
    with(sharedTransitionScope) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .padding(24.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "container"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut()
                ),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row() {
                AlbumArt(
                    imageUrl = state.currentTrack?.albumArtUrl,
                    modifier = Modifier.size(dims.albumArtSizeBigSize),
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
                Column() {
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

                        Spacer(modifier = Modifier.width(36.dp))
                        Image(
                            modifier = Modifier
                                .padding(1.dp)
                                .width(dims.playerMinMaxButtonWidth)
                                .height(dims.playerMinMaxButtonHeight)
                                .clickable { onShrink() }
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = "minmax"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = slowBoundsTransform
                                ),
                            painter = painterResource(id = R.drawable.minimise),
                            contentDescription = "image description",
                            contentScale = ContentScale.None
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().height(dims.songInfoContainerSizeHeight),
                        verticalAlignment = Alignment.Bottom
                    ) {

                        Spacer(modifier = Modifier.width(24.dp))

                        SongInfo(
                            title = state.currentTrack?.title ?: "Song title goes here till it then its marquee.....",
                            artist = state.currentTrack?.artist ?: "Artist Name",
                            album = "Album Name",
                            modifier = Modifier.weight(1f),
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }
            }

            // Bottom Section: Seekbar + Controls
            Column(modifier = Modifier.fillMaxWidth()) {
                val duration = state.currentTrack?.duration ?: 1L
                val position = state.playbackPosition.coerceAtMost(duration)
                var sliderPosition by remember(position) { mutableStateOf(position.toFloat()) }
                var isDragging by remember { mutableStateOf(false) }

                // Timestamps

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


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun MediaControlPanelPreview() {
    CarMusicTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true, label = "") {
                val sampleTrack = Track(
                    id = "1",
                    title = "Awesome Song",
                    artist = "The Best Band",
                    albumArtUrl = null,
                    duration = 240000L
                )
                val sampleQueue = listOf(
                    LocalSong(1L, "Song 1", "Artist A", "Album X", 200000L, "", null),
                    LocalSong(2L, "Song 2", "Artist B", "Album Y", 220000L, "", null)
                )
                val sampleState = PlayerState(
                    currentTrack = sampleTrack,
                    isPlaying = true,
                    playbackPosition = 60000L,
                    queue = sampleQueue,
                    isShuffleEnabled = false,
                    repeatMode = 0
                )

                MediaControlPanel(
                    state = sampleState,
                    onShrink = {},
                    onEvent = {},
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    modifier = Modifier
                )
            }
        }
    }
}
