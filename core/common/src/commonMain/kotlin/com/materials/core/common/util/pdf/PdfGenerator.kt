package com.materials.core.common.util.pdf

import com.materials.core.common.domain.model.Material

interface PdfGenerator {
    fun generateMaterialsPdf(
        materials: List<Material>,
        quantities: Map<String, Double> = emptyMap(),
        title: String = "Listado de Materiales Seleccionados"
    ): String?
}
