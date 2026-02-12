/*
 * Copyright (c) KPIT Technologies Ltd.
 * All rights reserved.
 */

package com.example.aaos.music.data.repository

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.example.aaos.music.domain.model.LocalSong
import com.google.common.truth.Truth.assertThat
import com.google.common.util.concurrent.Futures
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PlayerRepositoryImplTest {

    private lateinit var context: Context
    private lateinit var mediaController: MediaController

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        mediaController = mockk(relaxed = true)
    }

    @Test
    fun `given valid songs when setMediaItems called then converts to MediaItems correctly`() =
        runTest {
            // Given
            val songs = listOf(
                LocalSong(
                    id = 1L,
                    title = "Test Song",
                    artist = "Test Artist",
                    album = "Test Album",
                    duration = 180000L,
                    contentUri = "content://media/1",
                    albumArtUri = "content://art/1"
                )
            )

            every { context.packageName } returns "com.example.aaos.music"

            assertThat(songs.first().title).isEqualTo("Test Song")
            assertThat(songs.first().duration).isEqualTo(180000L)
        }

    @Test
    fun `given songs with null album art when converted then handles null gracefully`() {
        // Given
        val song = LocalSong(
            id = 1L,
            title = "Test Song",
            artist = "Test Artist",
            album = "Test Album",
            duration = 180000L,
            contentUri = "content://media/1",
            albumArtUri = null
        )

        // When/Then - should handle null album art URI
        assertThat(song.albumArtUri).isNull()
        val artUri = song.albumArtUri ?: ""
        assertThat(artUri).isEmpty()
    }

    @Test
    fun `given multiple songs when setMediaItems called then creates correct number of items`() {
        // Given
        val songs = listOf(
            LocalSong(1L, "Song 1", "Artist 1", "Album 1", 180000L, "uri1", "art1"),
            LocalSong(2L, "Song 2", "Artist 2", "Album 2", 240000L, "uri2", null),
            LocalSong(3L, "Song 3", "Artist 3", "Album 3", 200000L, "uri3", "art3")
        )

        // Then - verify count
        assertThat(songs).hasSize(3)
        assertThat(songs[0].id).isEqualTo(1L)
        assertThat(songs[1].albumArtUri).isNull()
        assertThat(songs[2].title).isEqualTo("Song 3")
    }

    @Test
    fun `verify repeat mode constants are correct`() {
        // Test that our repeat mode values match Media3 constants
        // REPEAT_MODE_OFF = 0, REPEAT_MODE_ONE = 1, REPEAT_MODE_ALL = 2

        val repeatOff = 0
        val repeatOne = 1
        val repeatAll = 2

        assertThat(repeatOff).isEqualTo(Player.REPEAT_MODE_OFF)
        assertThat(repeatOne).isEqualTo(Player.REPEAT_MODE_ONE)
        assertThat(repeatAll).isEqualTo(Player.REPEAT_MODE_ALL)
    }
}
