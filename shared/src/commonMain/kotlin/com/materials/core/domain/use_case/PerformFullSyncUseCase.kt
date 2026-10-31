package com.materials.core.domain.use_case

import com.materials.core.domain.repository.SyncRepository
import com.materials.core.domain.util.ResultState

class PerformFullSyncUseCase(
    private val repository: SyncRepository
) {
    suspend fun execute(): ResultState<Unit> {
        return repository.performFullSync()
    }
}
