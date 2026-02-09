package com.example.aaos.music.feature.player

import com.example.aaos.music.domain.repository.Track
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureScreenShot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w800dp-h480dp-port")
class MiniPlayerScreenshotTest {

    @get:Rule
    val roborazziRule = RoborazziRule(
        options = RoborazziRule.Options(
            outputDirectoryPath = "src/test/snapshots/images",
        )
    )

    @Test
    fun captureMiniPlayer_LHD_Playing() {
        val state = PlayerState(
            isPlaying = true, 
            currentTrack = Track(id = "1", title = "Song Title", artist = "Artist Name")
        )
        
        roborazziRule.captureScreenShot {
            MiniPlayer(state = state, onEvent = {}, isLhd = true)
        }
    }

    @Test
    fun captureMiniPlayer_RHD_Paused() {
        val state = PlayerState(
            isPlaying = false, 
            currentTrack = Track(id = "1", title = "Song Title", artist = "Artist Name")
        )
        
        roborazziRule.captureScreenShot {
            MiniPlayer(state = state, onEvent = {}, isLhd = false)
        }
    }
}
