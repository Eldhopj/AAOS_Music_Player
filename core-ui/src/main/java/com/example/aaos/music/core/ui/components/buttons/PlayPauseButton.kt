package com.example.aaos.music.core.ui.components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(64.dp)
    ) {
        val icon = if (isPlaying) PauseIcon else PlayArrowIcon
        val description = if (isPlaying) "Pause" else "Play"
        
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
    }
}

private val PauseIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Pause",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(6f, 19f)
            horizontalLineToRelative(4f)
            verticalLineTo(5f)
            horizontalLineTo(6f)
            verticalLineToRelative(14f)
            close()
            moveTo(14f, 5f)
            verticalLineToRelative(14f)
            horizontalLineToRelative(4f)
            verticalLineTo(5f)
            horizontalLineToRelative(-4f)
            close()
        }
    }.build()

private val PlayArrowIcon: ImageVector
    get() = ImageVector.Builder(
        name = "PlayArrow",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(8.0f, 5.0f)
            verticalLineToRelative(14.0f)
            lineTo(19.0f, 12.0f)
            lineTo(8.0f, 5.0f)
            close()
        }
    }.build()
