package com.example.aaos.music.core.ui.components.album

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun AlbumArt(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    var isLoaded by remember { mutableStateOf(false) }
    
    // Animate scale when loaded
    val animatedScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isLoaded) 1f else 0.8f,
        animationSpec = tween(durationMillis = 500),
        label = "AlbumArtScale"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(animatedScale)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black,
                ambientColor = Color.Black
            )
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Album Art",
                contentScale = ContentScale.Crop,
                onSuccess = { isLoaded = true },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Placeholder
            Image(
                painter = ColorPainter(MaterialTheme.colorScheme.secondary),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            // Trigger animation even for placeholder for consistency
            LaunchedEffect(Unit) {
                isLoaded = true
            }
        }
    }
}
