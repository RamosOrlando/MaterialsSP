package com.materials.core.util.pdf

import com.materials.features.material.domain.model.Material
import platform.UIKit.*
import platform.Foundation.*
import platform.CoreGraphics.*

class IosPdfGenerator : PdfGenerator {
    override fun generateMaterialsPdf(
        materials: List<Material>,
        quantities: Map<String, Double>,
        title: String
    ): String? {
        // Implementation for iOS using UIGraphicsPDFRenderer or similar
        // For now, a placeholder that prints to console
        println("PDF generation started on iOS for ${materials.size} items")
        return null
    }
}
