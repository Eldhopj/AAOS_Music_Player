package com.example.aaos.music.ui.player.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.aaos.music.core.ui.R
import com.example.aaos.music.domain.model.LocalSong
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QueueList(
    queue: List<LocalSong>,
    currentTrackId: Long?,
    onTrackClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {
        Column {
             Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Select Album", // Or "Queue" or "Playlist"
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                 // Icon for album/list if needed, as per design
            }
            
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)

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
                            onClick = { onTrackClick(index) }
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                }
            }
        }
        
        // Custom Scrollbar
        val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
        val visibleItemCount by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.size } }
        val totalItemCount by remember { derivedStateOf { listState.layoutInfo.totalItemsCount } }

        if (totalItemCount > visibleItemCount) {
            val scrollbarHeight = 100.dp // Fixed height thumb or proportional
            // Simple proportional calculation for demo
            val scrollProperties = remember(firstVisibleItemIndex, totalItemCount, visibleItemCount) {
                if (totalItemCount == 0) 0f to 0f else {
                   val progress = firstVisibleItemIndex.toFloat() / (totalItemCount - visibleItemCount).coerceAtLeast(1)
                   progress
                }
            }
            
            // This is a simplified scrollbar visual. AAOS usually provides one or we implement a draggable one.
            // For now, visual only as requested "Visible Scrollbar".
             Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp, top = 60.dp, bottom = 16.dp)
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                 // Use a Box for the thumb that moves
            }
        }
    }
}

@Composable
fun QueueItem(
    song: LocalSong,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isPlaying) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent
    val contentColor = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album Art tiny
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondary) // Placeholder
        ) {
            // AsyncImage would go here
             // Using Logo as placeholder if no art
             if (isPlaying) {
                 // Maybe an icon overlay
             }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        
        // Favorite Icon (Heart) - Placeholder
        Icon(
            painter = painterResource(id = R.drawable.play), // Replace with Heart
            contentDescription = "Favorite",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp) // Intentionally Wrong icon for now
        )

         Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = formatDuration(song.duration),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDuration(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, seconds)
}
