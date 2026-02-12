package com.example.aaos.music.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.aaos.music.core.ui.theme.CarMusicTheme
import com.example.aaos.music.ui.player.components.SongInfo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SongInfoTest {

    @get:Rule
    val compose = createComposeRule()

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun renders_title_and_artist_when_album_is_null() {
        val title = "Shape of You"
        val artist = "Ed Sheeran"
        val album: String? = null

        compose.setContent {
            CarMusicTheme {
                SharedTransitionLayout {
                    AnimatedVisibility(visible = true) {
                        // Provide both scopes to SongInfo
                        SongInfo(
                            title = title,
                            artist = artist,
                            album = album,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this
                        )
                    }
                }
            }
        }

        // Title from MarqueeText should be rendered
        compose.onNodeWithText(title, substring = false).assertExists()

        // Artist always rendered
        compose.onNodeWithText(artist, substring = false).assertExists()

        // Since album == null, ensure some known album text is not present (defensive check).
        // Choose a string that we know is not used elsewhere in the UI.
        compose.onNodeWithText("<<no-album>>", substring = false).assertDoesNotExist()
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun renders_album_title_and_artist_when_album_is_present() {
        val title = "Blinding Lights"
        val artist = "The Weeknd"
        val album: String? = "After Hours"

        compose.setContent {
            CarMusicTheme {
                SharedTransitionLayout {
                    AnimatedVisibility(visible = true) {
                        SongInfo(
                            title = title,
                            artist = artist,
                            album = album,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this
                        )
                    }
                }
            }
        }

        // Album appears when provided
        compose.onNodeWithText(album!!, substring = false).assertExists()

        // Title via MarqueeText should render
        compose.onNodeWithText(title, substring = false).assertExists()

        // Artist is rendered
        compose.onNodeWithText(artist, substring = false).assertExists()
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun long_texts_still_render_nodes() {
        val longTitle =
            "This is an extremely long song title that should ellipsize gracefully in a single line to avoid overflow issues"
        val longArtist =
            "An Artist With An Incredibly Long Name That Might Not Fit In A Single Line"
        val longAlbum =
            "A Very Long Album Name That Will Also Likely Need Ellipsizing To Fit In The Available Space"

        compose.setContent {
            CarMusicTheme {
                SharedTransitionLayout {
                    AnimatedVisibility(visible = true) {
                        SongInfo(
                            title = longTitle,
                            artist = longArtist,
                            album = longAlbum,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this
                        )
                    }
                }
            }
        }

        // We can't assert visual ellipsis in unit tests without a screenshot,
        // but we can assert presence via semantics.
        compose.onNodeWithText(longAlbum, substring = false).assertExists()
        compose.onNodeWithText(longTitle, substring = false).assertExists()
        compose.onNodeWithText(longArtist, substring = false).assertExists()
    }
}