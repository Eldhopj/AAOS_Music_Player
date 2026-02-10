package com.example.aaos.music.domain.repository

import com.example.aaos.music.domain.model.LocalSong
import kotlinx.coroutines.flow.Flow

interface LocalSongRepository {
    suspend fun getLocalSongs(): List<LocalSong>
}
