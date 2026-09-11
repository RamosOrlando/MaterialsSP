package com.materials.features.material.presentation

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import com.materials.core.presentation.theme.IndustrialTheme
import com.materials.core.presentation.util.AdaptivePreviews
import com.materials.core.common.domain.model.Material

@PreviewTest
@AdaptivePreviews
@Composable
fun MaterialScreenScreenshotTest() {
    IndustrialTheme {
        MaterialScreenContent(
            uiState = MaterialUiState.Success(
                listOf(
                    MaterialItem(
                        Material(
                            materialId = "1",
                            name = "Tubo PVC Presión 1/2\"",
                            unit = "Metro",
                            makerId = "MAKER-01",
                            sectionId = "1",
                            specId = null,
                            historyId = null,
                            providerId = null,
                            price = 12.5,
                            quoteDate = "2024-05-16",
                        ),
                        makerName = "Mexichem Amanco"

                    ),
                    MaterialItem(
                        Material(
                            materialId = "2",
                            name = "Codo 90° PVC 1/2\"",
                            unit = "Unidad",
                            makerId = "MAKER-02",
                            sectionId = "1",
                            specId = null,
                            historyId = null,
                            providerId = null,
                            price = 3.75,
                            quoteDate = "2024-05-15"
                        ),
                        makerName = "Pavco Wavin"
                    )
                )
            ),
            searchQuery = "",
            onEvent = {}
        )
    }
}
