package com.materials.core.domain

import com.materials.features.category.domain.repository.CategoryRepository
import com.materials.features.section.domain.repository.SectionRepository
import com.materials.features.maker.domain.repository.MakerRepository
import com.materials.features.material.domain.repository.MaterialRepository
import com.materials.features.price_history.domain.repository.PriceHistoryRepository
import com.materials.features.provider.domain.repository.ProviderRepository
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
    private val priceHistoryRepository: PriceHistoryRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun startSyncing() {
        scope.launch {
            initialSync()
            
            categoryRepository.listenToRealtimeChanges().launchIn(scope)
            sectionRepository.listenToRealtimeChanges().launchIn(scope)
            makerRepository.listenToRealtimeChanges().launchIn(scope)
            materialRepository.listenToRealtimeChanges().launchIn(scope)
            providerRepository.listenToRealtimeChanges().launchIn(scope)
            priceHistoryRepository.listenToRealtimeChanges().launchIn(scope)
        }
    }

    private suspend fun initialSync() {
        println("Starting initialSync...")
        // Fetch in order to satisfy foreign keys
        val catResult = categoryRepository.refreshCategories()
        println("Category refresh: $catResult")
        
        val secResult = sectionRepository.refreshSections()
        println("Section refresh: $secResult")
        
        val makResult = makerRepository.refreshMakers()
        println("Maker refresh: $makResult")
        
        val proResult = providerRepository.refreshProviders()
        println("Provider refresh: $proResult")
        
        val matResult = materialRepository.refreshMaterials()
        println("Material refresh: $matResult")
        
        val priResult = priceHistoryRepository.refreshPriceHistory()
        println("PriceHistory refresh: $priResult")
        
        println("initialSync finished")
    }
}
