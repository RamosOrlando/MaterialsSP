package com.materials.core.util.pdf

import com.materials.features.material.domain.model.MaterialWithPrices

interface PdfGenerator {
    fun generateMaterialsPdf(
        materials: List<MaterialWithPrices>,
        quantities: Map<String, Double> = emptyMap(),
        title: String = "Listado de Materiales Seleccionados"
    ): String?
}
