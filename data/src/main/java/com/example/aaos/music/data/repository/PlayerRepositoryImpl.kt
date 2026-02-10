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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
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
        val controller = getController()
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    trySend(controller.currentPosition)
                }
            }
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                trySend(newPosition.positionMs)
            }
        }
        
        controller.addListener(listener)
        
        // Initial position
        trySend(controller.currentPosition)
        
        // Polling for progress
        val pollJob = launch {
            while (true) {
                if (controller.isPlaying) {
                    trySend(controller.currentPosition)
                }
                delay(100L) // Update every 100ms
            }
        }
        
        awaitClose { 
            controller.removeListener(listener)
            pollJob.cancel()
        }
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

    override suspend fun playByIndex(index: Int) {
        val controller = getController()
        controller.seekToDefaultPosition(index)
        controller.play()
    }

    override suspend fun setMediaItems(songs: List<com.example.aaos.music.domain.model.LocalSong>) {
        val mediaItems = songs.map { song ->
            val extras = android.os.Bundle().apply {
                putLong("duration", song.duration)
            }

            val metadata = androidx.media3.common.MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                .setArtworkUri(android.net.Uri.parse(song.albumArtUri ?: ""))
                .setExtras(extras)
                .build()

            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.contentUri)
                .setMediaMetadata(metadata)
                .build()
        }
        val controller = getController()
        controller.setMediaItems(mediaItems)
        controller.prepare()
    }

    private fun MediaItem.toDomainTrack(): Track {
        val duration = mediaMetadata.extras?.getLong("duration") ?: 0L
        return Track(
            id = mediaId,
            title = mediaMetadata.title?.toString() ?: "Unknown",
            artist = mediaMetadata.artist?.toString() ?: "Unknown",
            albumArtUrl = mediaMetadata.artworkUri?.toString(),
            duration = duration
        )
    }
}
