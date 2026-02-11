
package com.example.aaos.music.ui.player

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.aaos.music.core.ui.theme.CarMusicTheme
import com.example.aaos.music.core.ui.theme.GradientEnd
import com.example.aaos.music.core.ui.theme.GradientStart
import com.example.aaos.music.core.ui.theme.gradientColors
import com.example.aaos.music.core.ui.utils.rememberUiDimensions
import com.example.aaos.music.ui.player.components.PlayerControls
import com.example.aaos.music.ui.viewmodel.PlayerViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SmallPlayerScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onExpand: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isLhd by viewModel.isLhd.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadLocalMusic()
    }

    CarMusicTheme {
        val dims = rememberUiDimensions()
            Row(
                modifier = modifier
                    .padding(dims.smallScreenPlayerPadding),
                horizontalArrangement = Arrangement.spacedBy(dims.smallScreenPlayerPadding),
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
                        SmallPlayerControlsSection(
                            state = state,
                            onEvent = viewModel::handleEvent,
                            onExpand = onExpand,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                        SmallPlayerControlsSection(
                            state = state,
                            onEvent = viewModel::handleEvent,
                            onExpand = onExpand,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }

        }
    }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun SmallPlayerControlsSection(
    onExpand: () -> Unit,
    state: PlayerState,
    onEvent: (PlayerEvent) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val cornerRadius = 24
    val shape = RoundedCornerShape(cornerRadius.dp)

    val dims = rememberUiDimensions()
    with(sharedTransitionScope) {
        Column(
            modifier = modifier
                .width(dims.smallScreenPlayerWidth)
                .clip(shape)
                .wrapContentHeight()
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "container"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut()
                )
                .background(brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.gradientColors.start,
                        MaterialTheme.gradientColors.end
                    )
                )).border(
                    width = 1.dp,
                    shape = shape,
                    color = MaterialTheme.colorScheme.outline
                ),
            verticalArrangement = Arrangement.spacedBy(dims.smallScreenPlayerSourceButtonPaddingTop),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            with(sharedTransitionScope) {
                Spacer(modifier = Modifier.height(dims.smallScreenPlayerSourceButtonPaddingTop))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .width(dims.smallScreenPlayerSourceButtonWidth)
                        .height(dims.smallScreenPlayerSourceButtonHeight)
                ) {

                    // source selection
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

                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        modifier = Modifier
                            .padding(1.dp)
                            .width(dims.playerMinMaxButtonWidth)
                            .height(dims.playerMinMaxButtonHeight)
                            .clickable { onExpand() }
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "minmax"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = slowBoundsTransform
                            ),
                        painter = painterResource(id = R.drawable.maximise),
                        contentDescription = "image description",
                        contentScale = ContentScale.None
                    )

                }

                AlbumArt(
                    imageUrl = state.currentTrack?.albumArtUrl,
                    modifier = Modifier.size(dims.albumArtSizeSmallWidth, dims.albumArtSizeSmallHeight), // Large art
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )

                with(sharedTransitionScope) {
                    Text(
                        text = state.currentTrack?.title?.trim() ?: "Select a song",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "title"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = slowBoundsTransform
                            )

                    )

                    Text(
                        text = state.currentTrack?.artist?.trim() ?: "Artist Name",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "artist"),
                                animatedVisibilityScope = animatedVisibilityScope,
                                boundsTransform = slowBoundsTransform
                            )

                    )
                }

                Box(modifier = Modifier.size(dims.smallPlayerControlWidth, dims.smallPlayerControlHeight)) {
                    PlayerControls(
                        isPlaying = state.isPlaying,
                        isShuffleEnabled = state.isShuffleEnabled,
                        repeatMode = state.repeatMode,
                        onPlayPauseClick = { onEvent(PlayerEvent.PlayPauseClicked) },
                        onPreviousClick = { onEvent(PlayerEvent.SkipPrevClicked) },
                        onNextClick = { onEvent(PlayerEvent.SkipNextClicked) },
                        onShuffleClick = { onEvent(PlayerEvent.ToggleShuffle) },
                        onRepeatClick = { onEvent(PlayerEvent.CycleRepeat) },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                }
            }
        }
    }
}
