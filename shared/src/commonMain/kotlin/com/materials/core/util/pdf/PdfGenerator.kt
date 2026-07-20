package com.materials.core.util.pdf

import com.materials.features.material.domain.model.Material

interface PdfGenerator {
    fun generateMaterialsPdf(
        materials: List<Material>,
        quantities: Map<String, Double> = emptyMap(),
        title: String = "Listado de Materiales Seleccionados"
    ): String?
}
