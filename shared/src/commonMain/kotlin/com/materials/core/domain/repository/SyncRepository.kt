package com.materials.core.domain.repository

import com.materials.core.domain.util.Resource

interface SyncRepository {
    suspend fun performFullSync(): Resource<Unit>
}
