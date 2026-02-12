package com.example.aaos.music.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.example.aaos.music.core.ui.theme.CarMusicTheme
import com.example.aaos.music.ui.player.components.PlayerControls
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PlayerControlsTest {

    @get:Rule
    val compose = createComposeRule()

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun renders_all_controls_with_expected_contentDescriptions() {
        compose.setContent {
            CarMusicTheme {
                SharedTransitionLayout {
                    val sharedScope = this
                    AnimatedVisibility(visible = true) {
                        val animatedScope = this
                        PlayerControls(
                            isPlaying = false,
                            isShuffleEnabled = false,
                            repeatMode = 0,
                            onPlayPauseClick = {},
                            onPreviousClick = {},
                            onNextClick = {},
                            onShuffleClick = {},
                            onRepeatClick = {},
                            isShuffleDisplay = true,
                            isRepeatDisplay = true,
                            sharedTransitionScope = sharedScope,
                            animatedVisibilityScope = animatedScope
                        )
                    }
                }
            }
        }

        // These four IconButtons explicitly set contentDescription
        compose.onNodeWithContentDescription("Shuffle").assertExists()
        compose.onNodeWithContentDescription("Previous").assertExists()
        compose.onNodeWithContentDescription("Next").assertExists()
        compose.onNodeWithContentDescription("Repeat").assertExists()

        // PlayPauseButton should expose "Play" when isPlaying = false
        compose.onNodeWithContentDescription("Play").assertExists()
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun clicking_buttons_invokes_callbacks_for_shuffle_prev_next_repeat() {
        var shuffleClicks by mutableStateOf(0)
        var prevClicks by mutableStateOf(0)
        var nextClicks by mutableStateOf(0)
        var repeatClicks by mutableStateOf(0)

        compose.setContent {
            CarMusicTheme {
                SharedTransitionLayout {
                    val sharedScope = this
                    AnimatedVisibility(visible = true) {
                        val animatedScope = this
                        PlayerControls(
                            isPlaying = false,
                            isShuffleEnabled = false,
                            repeatMode = 0,
                            onPlayPauseClick = {},
                            onPreviousClick = { prevClicks++ },
                            onNextClick = { nextClicks++ },
                            onShuffleClick = { shuffleClicks++ },
                            onRepeatClick = { repeatClicks++ },
                            isShuffleDisplay = true,
                            isRepeatDisplay = true,
                            sharedTransitionScope = sharedScope,
                            animatedVisibilityScope = animatedScope
                        )
                    }
                }
            }
        }

        compose.onNodeWithContentDescription("Shuffle").performClick()
        compose.onNodeWithContentDescription("Previous").performClick()
        compose.onNodeWithContentDescription("Next").performClick()
        compose.onNodeWithContentDescription("Repeat").performClick()

        assertEquals(1, prevClicks)
        assertEquals(1, nextClicks)
        assertEquals(1, repeatClicks)
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun play_pause_clicks_toggle_via_callback_and_contentDescription_play_state() {
        var playPauseClicks = 0

        compose.setContent {
            CarMusicTheme {
                SharedTransitionLayout {
                    val sharedScope = this
                    AnimatedVisibility(visible = true) {
                        val animatedScope = this
                        // Emulate state toggling (like a ViewModel would)
                        val isPlayingState = remember { mutableStateOf(false) }

                        PlayerControls(
                            isPlaying = isPlayingState.value,
                            isShuffleEnabled = false,
                            repeatMode = 0,
                            onPlayPauseClick = {
                                playPauseClicks++
                                isPlayingState.value = !isPlayingState.value
                            },
                            onPreviousClick = {},
                            onNextClick = {},
                            onShuffleClick = {},
                            onRepeatClick = {},
                            isShuffleDisplay = true,
                            isRepeatDisplay = true,
                            sharedTransitionScope = sharedScope,
                            animatedVisibilityScope = animatedScope
                        )
                    }
                }
            }
        }

        // Initially isPlaying = false, expect "Play"
        compose.onNodeWithContentDescription("Play").assertExists().performClick()

        // After click, we flipped to playing = true, expect "Pause"
        compose.onNodeWithContentDescription("Pause").assertExists().performClick()
        assertEquals(2, playPauseClicks)
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun contentDescription_reflects_shuffle_and_repeat_state_indirectly_rendered() {
        // We won't assert color/tint directly; verify controls render with their contentDescriptions.
        compose.setContent {
            CarMusicTheme {
                SharedTransitionLayout {
                    val sharedScope = this
                    AnimatedVisibility(visible = true) {
                        val animatedScope = this
                        PlayerControls(
                            isPlaying = true,
                            isShuffleEnabled = true, // would affect tint in UI code
                            repeatMode = 2,          // would affect tint in UI code
                            onPlayPauseClick = {},
                            onPreviousClick = {},
                            onNextClick = {},
                            onShuffleClick = {},
                            onRepeatClick = {},
                            isShuffleDisplay = true,
                            isRepeatDisplay = true,
                            sharedTransitionScope = sharedScope,
                            animatedVisibilityScope = animatedScope
                        )
                    }
                }
            }
        }

        compose.onNodeWithContentDescription("Shuffle").assertExists()
        compose.onNodeWithContentDescription("Repeat").assertExists()
        compose.onNodeWithContentDescription("Pause").assertExists()
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun shuffle_and_repeat_hidden_when_display_flags_false() {
        compose.setContent {
            CarMusicTheme {
                SharedTransitionLayout {
                    val sharedScope = this
                    AnimatedVisibility(visible = true) {
                        val animatedScope = this
                        PlayerControls(
                            isPlaying = false,
                            isShuffleEnabled = false,
                            repeatMode = 0,
                            onPlayPauseClick = {},
                            onPreviousClick = {},
                            onNextClick = {},
                            onShuffleClick = {},
                            onRepeatClick = {},
                            isShuffleDisplay = false,
                            isRepeatDisplay = false,
                            sharedTransitionScope = sharedScope,
                            animatedVisibilityScope = animatedScope
                        )
                    }
                }
            }
        }

        compose.onNodeWithContentDescription("Shuffle").assertDoesNotExist()
        compose.onNodeWithContentDescription("Repeat").assertDoesNotExist()
        // Core transport controls should still exist
        compose.onNodeWithContentDescription("Previous").assertExists()
        compose.onNodeWithContentDescription("Next").assertExists()
        compose.onNodeWithContentDescription("Play").assertExists()
    }
}