package com.example.aaos.music.domain.usecase

import com.example.aaos.music.domain.model.LocalSong
import com.example.aaos.music.domain.repository.LocalSongRepository
import javax.inject.Inject

class GetLocalSongsUseCase @Inject constructor(
    private val repository: LocalSongRepository
) {
    suspend operator fun invoke(): List<LocalSong> {
        return repository.getLocalSongs()
    }
}
