package com.orphean.domain.usecase

import com.orphean.domain.repository.SongRepository
import javax.inject.Inject

class SyncMediaUseCase @Inject constructor(
    private val repository: SongRepository
) {
    suspend operator fun invoke() {
        repository.syncMediaScanner()
    }
}
