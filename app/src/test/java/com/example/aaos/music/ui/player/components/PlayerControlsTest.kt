package com.example.aaos.music.ui.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalSharedTransitionApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34]) // Use a recent SDK
class PlayerControlsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val onPlayPauseClick: () -> Unit = mockk(relaxed = true)
    private val onPreviousClick: () -> Unit = mockk(relaxed = true)
    private val onNextClick: () -> Unit = mockk(relaxed = true)
    private val onShuffleClick: () -> Unit = mockk(relaxed = true)
    private val onRepeatClick: () -> Unit = mockk(relaxed = true)

    @Test
    fun playerControls_displays_all_buttons_when_enabled() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    PlayerControls(
                        isPlaying = false,
                        isShuffleEnabled = false,
                        repeatMode = 0,
                        onPlayPauseClick = onPlayPauseClick,
                        onPreviousClick = onPreviousClick,
                        onNextClick = onNextClick,
                        onShuffleClick = onShuffleClick,
                        onRepeatClick = onRepeatClick,
                        isShuffleDisplay = true,
                        isRepeatDisplay = true,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this
                    )
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Shuffle").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Previous").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Play").assertIsDisplayed() // Assuming "Play" is CD for play button when paused
        composeTestRule.onNodeWithContentDescription("Next").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Repeat").assertIsDisplayed()
    }

    @Test
    fun playerControls_hides_shuffle_and_repeat_when_disabled() {
        composeTestRule.setContent {
             SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    PlayerControls(
                        isPlaying = false,
                        isShuffleEnabled = false,
                        repeatMode = 0,
                        onPlayPauseClick = onPlayPauseClick,
                        onPreviousClick = onPreviousClick,
                        onNextClick = onNextClick,
                        onShuffleClick = onShuffleClick,
                        onRepeatClick = onRepeatClick,
                        isShuffleDisplay = false,
                        isRepeatDisplay = false,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this
                    )
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Shuffle").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Repeat").assertDoesNotExist()
    }

    @Test
    fun playerControls_invokes_callbacks() {
        composeTestRule.setContent {
             SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    PlayerControls(
                        isPlaying = true, // Playing -> CD should be "Pause"
                        isShuffleEnabled = false,
                        repeatMode = 0,
                        onPlayPauseClick = onPlayPauseClick,
                        onPreviousClick = onPreviousClick,
                        onNextClick = onNextClick,
                        onShuffleClick = onShuffleClick,
                        onRepeatClick = onRepeatClick,
                        isShuffleDisplay = true,
                        isRepeatDisplay = true,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this
                    )
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Pause").performClick()
        verify { onPlayPauseClick() }

        composeTestRule.onNodeWithContentDescription("Previous").performClick()
        verify { onPreviousClick() }

        composeTestRule.onNodeWithContentDescription("Next").performClick()
        verify { onNextClick() }
        
        composeTestRule.onNodeWithContentDescription("Shuffle").performClick()
        verify { onShuffleClick() }

        composeTestRule.onNodeWithContentDescription("Repeat").performClick()
        verify { onRepeatClick() }
    }
}
