package com.example.aaos.music.core.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class UiDimensions(

    val albumArtSizeSmallWidth: Dp,
    val albumArtSizeSmallHeight: Dp,
    val smallScreenPlayerHeight: Dp,
    val smallScreenPlayerWidth: Dp,
    val smallScreenPlayerPadding: Dp,
    val smallScreenPlayerSourceButtonWidth: Dp,
    val smallScreenPlayerSourceButtonHeight: Dp,
    val playerMinMaxButtonWidth: Dp,
    val playerMinMaxButtonHeight: Dp,
    val smallScreenPlayerSourceButtonPaddingTop: Dp,
    val previousIconButton: Dp,
    val nextIconButton: Dp,
    val previousIcon: Dp,
    val nextIcon: Dp,
    val playPauseIconButton: Dp,
    val smallPlayerControlWidth: Dp,
    val smallPlayerControlHeight: Dp,

    val albumArtSizeBigSize: Dp,
    val songInfoContainerSizeHeight: Dp,

)
@Composable
fun compresSize(small: Dp, medium: Dp) : Dp {
    val config = LocalConfiguration.current
    val sw = config.smallestScreenWidthDp ?: 0
    return when {
        (sw in 817..1080) ->  small// 1920x1080
        (sw in 720..816) ->  medium // 2220x816
        else -> 0
    } as Dp
}

@Composable
fun rememberUiDimensions(): UiDimensions {
    return UiDimensions(
        albumArtSizeSmallWidth = compresSize(214.dp, 162.dp),
        albumArtSizeSmallHeight = compresSize(214.dp, 162.dp),
        smallScreenPlayerHeight = compresSize(662.dp, 624.dp),
        smallScreenPlayerPadding = compresSize(24.dp, 14.dp),
        smallScreenPlayerWidth = compresSize(394.dp, 300.dp),
        smallScreenPlayerSourceButtonWidth = compresSize(356.dp, 270.dp),
        smallScreenPlayerSourceButtonHeight = compresSize(59.dp, 55.dp),
        smallScreenPlayerSourceButtonPaddingTop = compresSize(18.dp, 11.dp),
        previousIcon = compresSize(32.dp, 25.dp),
        nextIcon = compresSize(32.dp, 25.dp),
        nextIconButton = compresSize(48.dp, 32.dp),
        previousIconButton = compresSize(48.dp, 32.dp),
        playPauseIconButton = compresSize(72.dp, 60.dp),
        smallPlayerControlWidth = compresSize(344.dp, 270.dp),
        smallPlayerControlHeight = compresSize(102.dp, 85.dp),
        albumArtSizeBigSize= compresSize(350.dp, 250.dp),
        songInfoContainerSizeHeight = compresSize(290.dp, 190.dp),
        playerMinMaxButtonWidth= compresSize(48.dp, 48.dp),
        playerMinMaxButtonHeight= compresSize(54.dp, 50.dp),
    )
}