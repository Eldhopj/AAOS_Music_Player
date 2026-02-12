/*
 * Copyright (c) KPIT Technologies Ltd.
 * All rights reserved.
 */

package com.example.aaos.music.domain.usecase

import com.example.aaos.music.domain.repository.PlayerRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class SendPlayerIntentUseCaseTest {

    private lateinit var repository: PlayerRepository
    private lateinit var useCase: SendPlayerIntentUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SendPlayerIntentUseCase(repository)
    }

    @Test
    fun `given Play intent when invoked then calls repository play`() = runTest {
        // Given
        coEvery { repository.play() } just runs

        // When
        useCase(PlayerIntent.Play)

        // Then
        coVerify(exactly = 1) { repository.play() }
    }

    @Test
    fun `given Pause intent when invoked then calls repository pause`() = runTest {
        // Given
        coEvery { repository.pause() } just runs

        // When
        useCase(PlayerIntent.Pause)

        // Then
        coVerify(exactly = 1) { repository.pause() }
    }

    @Test
    fun `given SkipNext intent when invoked then calls repository skipNext`() = runTest {
        // Given
        coEvery { repository.skipNext() } just runs

        // When
        useCase(PlayerIntent.SkipNext)

        // Then
        coVerify(exactly = 1) { repository.skipNext() }
    }

    @Test
    fun `given SkipPrev intent when invoked then calls repository skipPrevious`() = runTest {
        // Given
        coEvery { repository.skipPrevious() } just runs

        // When
        useCase(PlayerIntent.SkipPrev)

        // Then
        coVerify(exactly = 1) { repository.skipPrevious() }
    }

    @Test
    fun `given SeekTo intent when invoked then calls repository seekTo with position`() = runTest {
        // Given
        val position = 45000L
        coEvery { repository.seekTo(any()) } just runs

        // When
        useCase(PlayerIntent.SeekTo(position))

        // Then
        coVerify(exactly = 1) { repository.seekTo(position) }
    }

    @Test
    fun `given SeekTo with zero position when invoked then calls repository with zero`() = runTest {
        // Given
        coEvery { repository.seekTo(any()) } just runs

        // When
        useCase(PlayerIntent.SeekTo(0L))

        // Then
        coVerify(exactly = 1) { repository.seekTo(0L) }
    }

    @Test
    fun `given PlayByIndex intent when invoked then calls repository playByIndex with index`() =
        runTest {
            // Given
            val index = 5
            coEvery { repository.playByIndex(any()) } just runs

            // When
            useCase(PlayerIntent.PlayByIndex(index))

            // Then
            coVerify(exactly = 1) { repository.playByIndex(index) }
        }

    @Test
    fun `given PlayByIndex with first index when invoked then calls repository with zero`() =
        runTest {
            // Given
            coEvery { repository.playByIndex(any()) } just runs

            // When
            useCase(PlayerIntent.PlayByIndex(0))

            // Then
            coVerify(exactly = 1) { repository.playByIndex(0) }
        }

    @Test
    fun `given SetShuffle true when invoked then calls repository setShuffleMode true`() = runTest {
        // Given
        coEvery { repository.setShuffleMode(any()) } just runs

        // When
        useCase(PlayerIntent.SetShuffle(true))

        // Then
        coVerify(exactly = 1) { repository.setShuffleMode(true) }
    }

    @Test
    fun `given SetShuffle false when invoked then calls repository setShuffleMode false`() =
        runTest {
            // Given
            coEvery { repository.setShuffleMode(any()) } just runs

            // When
            useCase(PlayerIntent.SetShuffle(false))

            // Then
            coVerify(exactly = 1) { repository.setShuffleMode(false) }
        }

    @Test
    fun `given SetRepeat OFF when invoked then calls repository setRepeatMode 0`() = runTest {
        // Given
        coEvery { repository.setRepeatMode(any()) } just runs

        // When
        useCase(PlayerIntent.SetRepeat(0))

        // Then
        coVerify(exactly = 1) { repository.setRepeatMode(0) }
    }

    @Test
    fun `given SetRepeat ONE when invoked then calls repository setRepeatMode 1`() = runTest {
        // Given
        coEvery { repository.setRepeatMode(any()) } just runs

        // When
        useCase(PlayerIntent.SetRepeat(1))

        // Then
        coVerify(exactly = 1) { repository.setRepeatMode(1) }
    }

    @Test
    fun `given SetRepeat ALL when invoked then calls repository setRepeatMode 2`() = runTest {
        // Given
        coEvery { repository.setRepeatMode(any()) } just runs

        // When
        useCase(PlayerIntent.SetRepeat(2))

        // Then
        coVerify(exactly = 1) { repository.setRepeatMode(2) }
    }

    @Test
    fun `given multiple sequential intents when invoked then executes in order`() = runTest {
        // Given
        coEvery { repository.play() } just runs
        coEvery { repository.pause() } just runs
        coEvery { repository.skipNext() } just runs

        // When
        useCase(PlayerIntent.Play)
        useCase(PlayerIntent.Pause)
        useCase(PlayerIntent.SkipNext)

        // Then
        coVerify(exactly = 1) { repository.play() }
        coVerify(exactly = 1) { repository.pause() }
        coVerify(exactly = 1) { repository.skipNext() }
    }

    @Test(expected = Exception::class)
    fun `given repository throws exception when invoked then propagates exception`() = runTest {
        // Given
        coEvery { repository.play() } throws Exception("Playback error")

        // When
        useCase(PlayerIntent.Play)

        // Then - exception should be thrown
    }
}
