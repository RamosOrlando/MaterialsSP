package com.materials.core.domain

import com.materials.features.category.domain.repository.CategoryRepository
import com.materials.features.section.domain.repository.SectionRepository
import com.materials.features.maker.domain.repository.MakerRepository
import com.materials.features.material.domain.repository.MaterialRepository
import com.materials.features.price_history.domain.repository.PriceHistoryRepository
import com.materials.features.provider.domain.repository.ProviderRepository
import com.materials.core.domain.use_case.PerformFullSyncUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class RealtimeSyncManager(
    private val categoryRepository: CategoryRepository,
    private val sectionRepository: SectionRepository,
    private val makerRepository: MakerRepository,
    private val materialRepository: MaterialRepository,
    private val providerRepository: ProviderRepository,
    private val priceHistoryRepository: PriceHistoryRepository,
    private val performFullSyncUseCase: PerformFullSyncUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var syncJob: kotlinx.coroutines.Job? = null

    fun startSyncing() {
        stopSyncing()
        syncJob = scope.launch {
            initialSync()
            
            categoryRepository.listenToRealtimeChanges().launchIn(this)
            sectionRepository.listenToRealtimeChanges().launchIn(this)
            makerRepository.listenToRealtimeChanges().launchIn(this)
            materialRepository.listenToRealtimeChanges().launchIn(this)
            providerRepository.listenToRealtimeChanges().launchIn(this)
            priceHistoryRepository.listenToRealtimeChanges().launchIn(this)
        }
    }

    fun stopSyncing() {
        syncJob?.cancel()
        syncJob = null
    }

    private suspend fun initialSync() {
        println("RealtimeSyncManager: Starting initialSync...")
        val result = performFullSyncUseCase.execute()
        println("RealtimeSyncManager: initialSync result: $result")
    }
}
