/*
 * Copyright (c) KPIT Technologies Ltd.
 * All rights reserved.
 */

package com.example.aaos.music.domain.usecase

import com.example.aaos.music.domain.model.LocalSong
import com.example.aaos.music.domain.repository.LocalSongRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetLocalSongsUseCaseTest {

    private lateinit var repository: LocalSongRepository
    private lateinit var useCase: GetLocalSongsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetLocalSongsUseCase(repository)
    }

    @Test
    fun `given repository returns songs when invoked then returns same list`() = runTest {
        // Given
        val expectedSongs = listOf(
            LocalSong(
                id = 1L,
                title = "Test Song 1",
                artist = "Test Artist 1",
                album = "Test Album 1",
                duration = 180000L,
                contentUri = "content://media/1",
                albumArtUri = "content://art/1"
            ),
            LocalSong(
                id = 2L,
                title = "Test Song 2",
                artist = "Test Artist 2",
                album = "Test Album 2",
                duration = 240000L,
                contentUri = "content://media/2",
                albumArtUri = null
            )
        )
        coEvery { repository.getLocalSongs() } returns expectedSongs

        // When
        val result = useCase()

        // Then
        assertThat(result).isEqualTo(expectedSongs)
        assertThat(result).hasSize(2)
        coVerify(exactly = 1) { repository.getLocalSongs() }
    }

    @Test
    fun `given repository returns empty list when invoked then returns empty list`() = runTest {
        // Given
        coEvery { repository.getLocalSongs() } returns emptyList()

        // When
        val result = useCase()

        // Then
        assertThat(result).isEmpty()
        coVerify(exactly = 1) { repository.getLocalSongs() }
    }

    @Test
    fun `given repository returns large list when invoked then returns all songs`() = runTest {
        // Given
        val largeSongList = (1..1000).map { index ->
            LocalSong(
                id = index.toLong(),
                title = "Song $index",
                artist = "Artist $index",
                album = "Album $index",
                duration = 200000L,
                contentUri = "content://media/$index",
                albumArtUri = "content://art/$index"
            )
        }
        coEvery { repository.getLocalSongs() } returns largeSongList

        // When
        val result = useCase()

        // Then
        assertThat(result).hasSize(1000)
        assertThat(result.first().title).isEqualTo("Song 1")
        assertThat(result.last().title).isEqualTo("Song 1000")
        coVerify(exactly = 1) { repository.getLocalSongs() }
    }

    @Test(expected = Exception::class)
    fun `given repository throws exception when invoked then propagates exception`() = runTest {
        // Given
        coEvery { repository.getLocalSongs() } throws Exception("Database error")

        // When
        useCase()

        // Then - exception should be thrown
    }

    @Test
    fun `given multiple invocations when called repeatedly then calls repository each time`() = runTest {
        // Given
        val songs = listOf(
            LocalSong(1L, "Song", "Artist", "Album", 180000L, "uri", null)
        )
        coEvery { repository.getLocalSongs() } returns songs

        // When
        useCase()
        useCase()
        useCase()

        // Then
        coVerify(exactly = 3) { repository.getLocalSongs() }
    }
}
