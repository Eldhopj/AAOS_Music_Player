/*
 * Copyright (c) KPIT Technologies Ltd.
 * All rights reserved.
 */

package com.example.aaos.music.ui.player

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.aaos.music.domain.model.LocalSong
import com.example.aaos.music.domain.repository.PlayerRepository
import com.example.aaos.music.domain.repository.Track
import com.example.aaos.music.domain.usecase.GetLocalSongsUseCase
import com.example.aaos.music.domain.usecase.PlayerIntent
import com.example.aaos.music.domain.usecase.SendPlayerIntentUseCase
import com.example.aaos.music.ui.viewmodel.PlayerViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Comprehensive unit tests for PlayerViewModel
 * Tests all playback control features and state management
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var sendPlayerIntentUseCase: SendPlayerIntentUseCase
    private lateinit var getLocalSongsUseCase: GetLocalSongsUseCase
    private lateinit var playerRepository: PlayerRepository
    private lateinit var viewModel: PlayerViewModel

    private val testSongs = listOf(
        LocalSong(1L, "Song 1", "Artist 1", "Album 1", 180000L, "uri1", "art1"),
        LocalSong(2L, "Song 2", "Artist 2", "Album 2", 240000L, "uri2", "art2"),
        LocalSong(3L, "Song 3", "Artist 3", "Album 3", 200000L, "uri3", null)
    )

    private val testTrack = Track("1", "Song 1", "Artist 1", "art1", "180000L")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        sendPlayerIntentUseCase = mockk()
        getLocalSongsUseCase = mockk()
        playerRepository = mockk()

        // Setup default repository flows
        every { playerRepository.currentTrack } returns flowOf(null)
        every { playerRepository.isPlaying } returns flowOf(false)
        every { playerRepository.playbackPosition } returns flowOf(0L)
        every { playerRepository.shuffleModeEnabled } returns flowOf(false)
        every { playerRepository.repeatMode } returns flowOf(0)
    }

    private fun createViewModel(): PlayerViewModel {
        return PlayerViewModel(
            sendPlayerIntentUseCase,
            getLocalSongsUseCase,
            playerRepository
        )
    }


    @Test
    fun `given loadLocalMusic called when songs available then updates queue and loading state`() = runTest {
        // Given
        coEvery { getLocalSongsUseCase() } returns testSongs
        coEvery { playerRepository.setMediaItems(any()) } just runs
        viewModel = createViewModel()

        // When
        viewModel.loadLocalMusic()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.queue).hasSize(3)
            assertThat(state.queue).isEqualTo(testSongs)
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }

        coVerify { getLocalSongsUseCase() }
        coVerify { playerRepository.setMediaItems(testSongs) }
    }

    @Test
    fun `given loadLocalMusic called when empty result then updates with empty queue`() = runTest {
        // Given
        coEvery { getLocalSongsUseCase() } returns emptyList()
        coEvery { playerRepository.setMediaItems(any()) } just runs
        viewModel = createViewModel()

        // When
        viewModel.loadLocalMusic()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.queue).isEmpty()
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `given loadLocalMusic fails when exception thrown then sets error state`() = runTest {
        // Given
        val errorMessage = "Failed to load songs"
        coEvery { getLocalSongsUseCase() } throws Exception(errorMessage)
        viewModel = createViewModel()

        // When
        viewModel.loadLocalMusic()
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo(errorMessage)
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `given PlayPauseClicked when not playing then sends Play intent`() = runTest {
        // Given
        every { playerRepository.isPlaying } returns flowOf(false)
        coEvery { sendPlayerIntentUseCase(any()) } just runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.handleEvent(PlayerEvent.PlayPauseClicked)
        advanceUntilIdle()

        // Then
        coVerify { sendPlayerIntentUseCase(PlayerIntent.Play) }
    }

    @Test
    fun `given PlayPauseClicked when playing then sends Pause intent`() = runTest {
        // Given
        every { playerRepository.isPlaying } returns flowOf(true)
        coEvery { sendPlayerIntentUseCase(any()) } just runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.handleEvent(PlayerEvent.PlayPauseClicked)
        advanceUntilIdle()

        // Then
        coVerify { sendPlayerIntentUseCase(PlayerIntent.Pause) }
    }

    @Test
    fun `given SkipNextClicked when triggered then sends SkipNext intent`() = runTest {
        // Given
        coEvery { sendPlayerIntentUseCase(any()) } just runs
        viewModel = createViewModel()

        // When
        viewModel.handleEvent(PlayerEvent.SkipNextClicked)
        advanceUntilIdle()

        // Then
        coVerify { sendPlayerIntentUseCase(PlayerIntent.SkipNext) }
    }

    @Test
    fun `given SkipPrevClicked when triggered then sends SkipPrev intent`() = runTest {
        // Given
        coEvery { sendPlayerIntentUseCase(any()) } just runs
        viewModel = createViewModel()

        // When
        viewModel.handleEvent(PlayerEvent.SkipPrevClicked)
        advanceUntilIdle()

        // Then
        coVerify { sendPlayerIntentUseCase(PlayerIntent.SkipPrev) }
    }

    @Test
    fun `given SeekTo event when triggered then sends SeekTo intent with position`() = runTest {
        // Given
        val targetPosition = 45000L
        coEvery { sendPlayerIntentUseCase(any()) } just runs
        viewModel = createViewModel()

        // When
        viewModel.handleEvent(PlayerEvent.SeekTo(targetPosition))
        advanceUntilIdle()

        // Then
        coVerify { sendPlayerIntentUseCase(PlayerIntent.SeekTo(targetPosition)) }
    }

    @Test
    fun `given PlayByIndex event when triggered then sends PlayByIndex intent`() = runTest {
        // Given
        val index = 2
        coEvery { sendPlayerIntentUseCase(any()) } just runs
        viewModel = createViewModel()

        // When
        viewModel.handleEvent(PlayerEvent.PlayByIndex(index))
        advanceUntilIdle()

        // Then
        coVerify { sendPlayerIntentUseCase(PlayerIntent.PlayByIndex(index)) }
    }

    @Test
    fun `given ToggleShuffle when shuffle is off then sends SetShuffle true`() = runTest {
        // Given
        every { playerRepository.shuffleModeEnabled } returns flowOf(false)
        coEvery { sendPlayerIntentUseCase(any()) } just runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.handleEvent(PlayerEvent.ToggleShuffle)
        advanceUntilIdle()

        // Then
        coVerify { sendPlayerIntentUseCase(PlayerIntent.SetShuffle(true)) }
    }

    @Test
    fun `given ToggleShuffle when shuffle is on then sends SetShuffle false`() = runTest {
        // Given
        every { playerRepository.shuffleModeEnabled } returns flowOf(true)
        coEvery { sendPlayerIntentUseCase(any()) } just runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.handleEvent(PlayerEvent.ToggleShuffle)
        advanceUntilIdle()

        // Then
        coVerify { sendPlayerIntentUseCase(PlayerIntent.SetShuffle(false)) }
    }

    @Test
    fun `given CycleRepeat when mode is OFF then sends SetRepeat ALL (2)`() = runTest {
        // Given
        every { playerRepository.repeatMode } returns flowOf(0)
        coEvery { sendPlayerIntentUseCase(any()) } just runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.handleEvent(PlayerEvent.CycleRepeat)
        advanceUntilIdle()

        // Then
        coVerify { sendPlayerIntentUseCase(PlayerIntent.SetRepeat(2)) }
    }

    @Test
    fun `given CycleRepeat when mode is ALL (2) then sends SetRepeat ONE (1)`() = runTest {
        // Given
        every { playerRepository.repeatMode } returns flowOf(2)
        coEvery { sendPlayerIntentUseCase(any()) } just runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.handleEvent(PlayerEvent.CycleRepeat)
        advanceUntilIdle()

        // Then
        coVerify { sendPlayerIntentUseCase(PlayerIntent.SetRepeat(1)) }
    }

    @Test
    fun `given CycleRepeat when mode is ONE (1) then sends SetRepeat OFF (0)`() = runTest {
        // Given
        every { playerRepository.repeatMode } returns flowOf(1)
        coEvery { sendPlayerIntentUseCase(any()) } just runs
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.handleEvent(PlayerEvent.CycleRepeat)
        advanceUntilIdle()

        // Then
        coVerify { sendPlayerIntentUseCase(PlayerIntent.SetRepeat(0)) }
    }

    @Test
    fun `given repository emits playing state when observed then updates viewmodel state`() = runTest {
        // Given
        every { playerRepository.isPlaying } returns flowOf(true)
        viewModel = createViewModel()

        // When
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isPlaying).isTrue()
        }
    }

    @Test
    fun `given repository emits currentTrack when observed then updates viewmodel state`() = runTest {
        // Given
        every { playerRepository.currentTrack } returns flowOf(testTrack)
        viewModel = createViewModel()

        // When
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.currentTrack).isEqualTo(testTrack)
        }
    }

    @Test
    fun `given repository emits playback position when observed then updates viewmodel state`() = runTest {
        // Given
        val position = 30000L
        every { playerRepository.playbackPosition } returns flowOf(position)
        viewModel = createViewModel()

        // When
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.playbackPosition).isEqualTo(position)
        }
    }

    @Test
    fun `given repository emits shuffle enabled when observed then updates viewmodel state`() = runTest {
        // Given
        every { playerRepository.shuffleModeEnabled } returns flowOf(true)
        viewModel = createViewModel()

        // When
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isShuffleEnabled).isTrue()
        }
    }

    @Test
    fun `given repository emits repeat mode when observed then updates viewmodel state`() = runTest {
        // Given
        every { playerRepository.repeatMode } returns flowOf(2)
        viewModel = createViewModel()

        // When
        advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.repeatMode).isEqualTo(2)
        }
    }


    @Test
    fun `given state changes when viewmodel recreated then state should be observable`() = runTest {
        // Given
        every { playerRepository.isPlaying } returns flowOf(true)
        every { playerRepository.currentTrack } returns flowOf(testTrack)

        // When
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then - State should reflect repository emissions
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.isPlaying).isTrue()
            assertThat(state.currentTrack).isEqualTo(testTrack)
        }
    }
}
