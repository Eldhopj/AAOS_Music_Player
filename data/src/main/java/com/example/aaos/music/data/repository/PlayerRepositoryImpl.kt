package com.example.aaos.music.data.repository

import android.content.Context
import android.content.ComponentName
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.aaos.music.domain.repository.PlayerRepository
import com.example.aaos.music.domain.repository.Track
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.guava.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PlayerRepository {
    private val sessionToken = SessionToken(
        context,
        ComponentName(context, "com.example.aaos.music.service.media.MusicService")
    )

    private val controllerFuture: ListenableFuture<MediaController> =
        MediaController.Builder(context, sessionToken).buildAsync()

    private suspend fun getController(): MediaController {
        return if (controllerFuture.isDone) {
            controllerFuture.get()
        } else {
            controllerFuture.await()
        }
    }

    override val currentTrack: Flow<Track?> = callbackFlow {
        val listener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                trySend(mediaItem?.toDomainTrack())
            }
        }
        val controller = getController()
        controller.addListener(listener)
        trySend(controller.currentMediaItem?.toDomainTrack())
        
        awaitClose { controller.removeListener(listener) }
    }

    override val isPlaying: Flow<Boolean> = callbackFlow {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                trySend(isPlaying)
            }
        }
        val controller = getController()
        controller.addListener(listener)
        trySend(controller.isPlaying)
        awaitClose { controller.removeListener(listener) }
    }

    override val playbackPosition: Flow<Long> = callbackFlow {
        // Polling loop or periodic update would be better for position
        // For simplicity here, we just emit 0 or start/events
        // In prod, use a ticker flow combined with current position check
        trySend(0L) 
        awaitClose { }
    }

    override suspend fun play() {
        getController().play()
    }

    override suspend fun pause() {
        getController().pause()
    }

    override suspend fun skipNext() {
        getController().seekToNext()
    }

    override suspend fun skipPrevious() {
        getController().seekToPrevious()
    }

    override suspend fun seekTo(position: Long) {
        getController().seekTo(position)
    }

    private fun MediaItem.toDomainTrack(): Track {
        return Track(
            id = mediaId,
            title = mediaMetadata.title?.toString() ?: "Unknown",
            artist = mediaMetadata.artist?.toString() ?: "Unknown",
            albumArtUrl = mediaMetadata.artworkUri?.toString()
        )
    }
}
