package com.materials.core.domain.repository

import com.materials.core.domain.util.ResultState

interface SyncRepository {
    suspend fun performFullSync(): ResultState<Unit>
}
