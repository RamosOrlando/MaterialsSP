package com.materials.core.domain.use_case

import com.materials.core.domain.repository.SyncRepository
import com.materials.core.domain.util.Resource

class PerformFullSyncUseCase(
    private val repository: SyncRepository
) {
    suspend fun execute(): Resource<Unit> {
        return repository.performFullSync()
    }
}
