package com.orphean.domain.usecase

import com.orphean.domain.model.Song
import com.orphean.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(): Flow<List<Song>> {
        return repository.getAllSongs()
    }
}
