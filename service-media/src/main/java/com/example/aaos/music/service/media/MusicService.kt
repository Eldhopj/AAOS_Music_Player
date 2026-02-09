package com.example.aaos.music.service.media

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaLibraryService() {

    @Inject
    lateinit var player: ExoPlayer

    private var mediaSession: MediaLibrarySession? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaLibrarySession.Builder(
            this,
            player,
            LibrarySessionCallback()
        ).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    // Callback for browsing logic
    private inner class LibrarySessionCallback : MediaLibrarySession.Callback {
        // Implement onGetLibraryRoot, onGetChildren for browsing
        // For local playback, we would fetch from repository
    }
}
