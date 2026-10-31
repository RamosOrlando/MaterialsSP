package com.materials.features.maker.domain.use_case

import com.materials.core.domain.util.ResultState
import com.materials.features.maker.domain.model.Maker
import com.materials.features.maker.domain.repository.MakerRepository

class SaveMakerUseCase(
    private val repository: MakerRepository
) {
    suspend fun execute(maker: Maker): ResultState<Unit> {
        return repository.saveMaker(maker)
    }
}
