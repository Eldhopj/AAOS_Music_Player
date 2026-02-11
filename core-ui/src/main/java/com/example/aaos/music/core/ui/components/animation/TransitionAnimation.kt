package com.example.aaos.music.core.ui.components.animation

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween


@OptIn(ExperimentalSharedTransitionApi::class)
val slowBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
    tween(
        durationMillis = 600,   // <-- Slow it down (increase duration)
        easing = LinearOutSlowInEasing
    )
}
