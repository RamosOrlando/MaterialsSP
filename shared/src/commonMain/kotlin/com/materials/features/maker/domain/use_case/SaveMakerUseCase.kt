package com.materials.features.maker.domain.use_case

import com.materials.core.domain.util.Resource
import com.materials.features.maker.domain.model.Maker
import com.materials.features.maker.domain.repository.MakerRepository

class SaveMakerUseCase(
    private val repository: MakerRepository
) {
    suspend fun execute(maker: Maker): Resource<Unit> {
        return repository.saveMaker(maker)
    }
}
