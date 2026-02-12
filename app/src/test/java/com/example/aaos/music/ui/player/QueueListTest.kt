package com.example.aaos.music.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.aaos.music.core.ui.theme.CarMusicTheme
import com.example.aaos.music.domain.model.LocalSong
import com.example.aaos.music.ui.player.components.QueueList
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class QueueListTest {

    @get:Rule
    val compose = createComposeRule()

    private fun sampleQueue(): List<LocalSong> = listOf(
        LocalSong(
            id = 1,
            title = "Song 1",
            artist = "Artist 1",
            album = "Album 1",
            duration = 125_000, // 02:05
            contentUri = "content://media/external/audio/media/1",
            albumArtUri = null
        ),
        LocalSong(
            id = 2,
            title = "Song 2",
            artist = "Artist 2",
            album = "Album 2",
            duration = 240_000, // 04:00
            contentUri = "content://media/external/audio/media/2",
            albumArtUri = null
        ),
        LocalSong(
            id = 3,
            title = "Song 3",
            artist = "Artist 3",
            album = "Album 3",
            duration = 61_000, // 01:01
            contentUri = "content://media/external/audio/media/3",
            albumArtUri = null
        )
    )

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun clicking_item_invokes_onTrackClick_with_correct_index() {
        var clickedIndex: Int? = null

        compose.setContent {
            CarMusicTheme {
                SharedTransitionLayout {
                    val sharedScope = this
                    AnimatedVisibility(visible = true) {
                        val animatedScope = this
                        QueueList(
                            queue = sampleQueue(),
                            currentTrackId = null,
                            sharedTransitionScope = sharedScope,
                            animatedVisibilityScope = animatedScope,
                            onTrackClick = { clickedIndex = it }
                        )
                    }
                }
            }
        }

        // Click the second item by its title
        compose.onNodeWithText("Song 2").performClick()
        assertEquals("Expected zero-based index for 'Song 2'", 1, clickedIndex)
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun favorite_icon_renders_for_each_item() {
        val queue = sampleQueue()

        compose.setContent {
            CarMusicTheme {
                SharedTransitionLayout {
                    val sharedScope = this
                    AnimatedVisibility(visible = true) {
                        val animatedScope = this
                        QueueList(
                            queue = queue,
                            currentTrackId = null,
                            sharedTransitionScope = sharedScope,
                            animatedVisibilityScope = animatedScope,
                            onTrackClick = {}
                        )
                    }
                }
            }
        }

        // "Favorite" icon is present per item
        compose.onAllNodesWithContentDescription("Favorite").assertCountEquals(queue.size)
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun durations_are_formatted_correctly() {
        compose.setContent {
            CarMusicTheme {
                SharedTransitionLayout {
                    val sharedScope = this
                    AnimatedVisibility(visible = true) {
                        val animatedScope = this
                        QueueList(
                            queue = sampleQueue(),
                            currentTrackId = null,
                            sharedTransitionScope = sharedScope,
                            animatedVisibilityScope = animatedScope,
                            onTrackClick = {}
                        )
                    }
                }
            }
        }

        // formatDuration checks
        compose.onNodeWithText("02:05").assertExists()
        compose.onNodeWithText("04:00").assertExists()
        compose.onNodeWithText("01:01").assertExists()
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun highlights_current_track_without_breaking_rendering() {
        compose.setContent {
            CarMusicTheme {
                SharedTransitionLayout {
                    val sharedScope = this
                    AnimatedVisibility(visible = true) {
                        val animatedScope = this
                        QueueList(
                            queue = sampleQueue(),
                            currentTrackId = 2L, // mark "Song 2" as playing
                            sharedTransitionScope = sharedScope,
                            animatedVisibilityScope = animatedScope,
                            onTrackClick = {}
                        )
                    }
                }
            }
        }

        // We don't assert color/boldness (not in semantics), but the item renders fine
        compose.onNodeWithText("Song 2").assertExists()
        compose.onNodeWithText("Artist 2").assertExists()
    }
}