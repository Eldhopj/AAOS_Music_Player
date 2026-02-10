package com.example.aaos.music.core.ui.components.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.aaos.music.core.ui.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable

@Composable
fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = if (isPlaying) R.drawable.pause else R.drawable.play
    val description = if (isPlaying) "Pause" else "Play"

    Image(
        painter = painterResource(id = icon),
        contentDescription = description,
        modifier = modifier
            .size(72.dp)
            .clickable(onClick = onClick)
    )
}
