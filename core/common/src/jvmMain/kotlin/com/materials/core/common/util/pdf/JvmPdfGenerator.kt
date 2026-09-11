package com.materials.core.common.util.pdf

import com.materials.core.common.domain.model.Material

class JvmPdfGenerator : PdfGenerator {
    override fun generateMaterialsPdf(
        materials: List<Material>,
        quantities: Map<String, Double>,
        title: String
    ): String? {
        println("PDF generation started on JVM/Desktop for ${materials.size} items")
        return null
    }
}
