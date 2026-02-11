package com.example.aaos.music.ui.player.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.aaos.music.core.ui.R
import com.example.aaos.music.core.ui.components.album.AlbumArt
import com.example.aaos.music.domain.model.LocalSong
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QueueList(
    queue: List<LocalSong>,
    currentTrackId: Long?,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onTrackClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Surface(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), // Subtle border
                shape = RoundedCornerShape(16.dp)
            ),
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Album",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(queue, key = { _, song -> song.id }) { index, song ->
                        val isPlaying = song.id == currentTrackId

                        Box(modifier = Modifier.animateItem()) {
                            QueueItem(
                                song = song,
                                isPlaying = isPlaying,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                onClick = { onTrackClick(index) }
                            )
                        }
                        if (index < queue.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f)
                            )
                        }
                    }
                }
            }

            // Custom Scrollbar
            val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
            val visibleItemCount by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.size } }
            val totalItemCount by remember { derivedStateOf { listState.layoutInfo.totalItemsCount } }

            if (totalItemCount > visibleItemCount) {
                // Simple proportional calculation for demo
                val scrollProgress = if (totalItemCount == 0 || totalItemCount - visibleItemCount <= 0) 0f
                else firstVisibleItemIndex.toFloat() / (totalItemCount - visibleItemCount)


                // Visual Scrollbar Track/Thumb
                // This is a simplified visual representation.
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 6.dp, top = 60.dp, bottom = 16.dp)
                        .width(4.dp)
                        .fillMaxHeight(),
                     verticalArrangement = Arrangement.SpaceBetween // Just to span
                ) {
                   // Dynamic spacer to push the thumb down
                   // Note: Real implementations often use a custom Layout or Modifier.
                   // Here we just mock the thumb position.
                    Spacer(modifier = Modifier.weight(scrollProgress.coerceIn(0.01f, 0.99f)))
                    
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .padding(vertical = 2.dp)
                            .fillMaxHeight(0.2f) // Fixed size thumb related to height
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), CircleShape)
                    )
                    
                    Spacer(modifier = Modifier.weight(1f - scrollProgress.coerceIn(0.01f, 0.99f)))
                }
            }
        }
    }
}

@Composable
fun QueueItem(
    song: LocalSong,
    isPlaying: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit
) {
    // Subtle background for playing item, otherwise transparent
    val backgroundColor = if (isPlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else Color.Transparent
    val titleColor = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val artistColor = if (isPlaying) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 10.dp), // Slightly tighter padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album Art (Circular)
        AlbumArt(
            imageUrl = song.albumArtUri,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier.size(42.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Text Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = artistColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Favorite Icon (Heart) - Always visible as outline, could be filled if favorited
        Icon(
            painter = painterResource(id = R.drawable.favorite),
            contentDescription = "Favorite",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Duration
        Text(
            text = formatDuration(song.duration),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
             modifier = Modifier.width(40.dp) // Fixed width for alignment
        )
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
