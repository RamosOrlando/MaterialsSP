package com.materials.core.common.util.pdf

import com.materials.core.common.domain.model.Resource

interface PdfGenerator {
    fun generateResourcesPdf(
        resources: List<Resource>,
        quantities: Map<String, Double> = emptyMap(),
        title: String = "Listado de Materiales Seleccionados"
    ): String?
}
