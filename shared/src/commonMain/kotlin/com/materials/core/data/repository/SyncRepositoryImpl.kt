package com.materials.core.data.repository

import com.materials.core.domain.repository.SyncRepository
import com.materials.core.domain.util.Resource
import com.materials.features.category.domain.repository.CategoryRepository
import com.materials.features.maker.domain.repository.MakerRepository
import com.materials.features.material.domain.repository.MaterialRepository
import com.materials.features.price_history.domain.repository.PriceHistoryRepository
import com.materials.features.provider.domain.repository.ProviderRepository
import com.materials.features.section.domain.repository.SectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SyncRepositoryImpl(
    private val categoryRepository: CategoryRepository,
    private val sectionRepository: SectionRepository,
    private val makerRepository: MakerRepository,
    private val providerRepository: ProviderRepository,
    private val materialRepository: MaterialRepository,
    private val priceHistoryRepository: PriceHistoryRepository
) : SyncRepository {

    override suspend fun performFullSync(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            println("SyncRepository: Starting full sync...")
            
            // 1. Categories
            categoryRepository.refreshCategories().also { if (it is Resource.Error) return@withContext it }
            
            // 2. Sections
            sectionRepository.refreshSections().also { if (it is Resource.Error) return@withContext it }
            
            // 3. Makers
            makerRepository.refreshMakers().also { if (it is Resource.Error) return@withContext it }
            
            // 4. Providers
            providerRepository.refreshProviders().also { if (it is Resource.Error) return@withContext it }
            
            // 5. Materials
            materialRepository.refreshMaterials().also { if (it is Resource.Error) return@withContext it }
            
            // 6. PriceHistory
            priceHistoryRepository.refreshPriceHistory().also { if (it is Resource.Error) return@withContext it }
            
            println("SyncRepository: Full sync finished successfully")
            Resource.Success(Unit)
        } catch (e: Exception) {
            println("SyncRepository: Error during full sync: ${e.message}")
            Resource.Error(e.message ?: "Unknown sync error")
        }
    }
}
