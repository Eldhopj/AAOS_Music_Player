package com.example.aaos.music.ui.player.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.aaos.music.core.ui.components.animation.slowBoundsTransform
import com.example.aaos.music.core.ui.components.text.MarqueeText

@Composable
fun SongInfo(
    title: String,
    artist: String,
    album: String?,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        Column(modifier = modifier.fillMaxWidth()) {
            if (album != null) {
                Text(
                    text = album.trim(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            MarqueeText(
                text = title.trim(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "title"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = slowBoundsTransform
                    )

            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = artist.trim(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "artist"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = slowBoundsTransform
                    )

            )
        }
    }
}
