package com.materials.features.material.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import com.materials.core.presentation.theme.*
import com.materials.features.material.domain.model.MaterialWithPrices
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.ui.tooling.preview.Preview
import com.materials.features.material.domain.model.Material
import com.materials.features.maker.domain.model.Maker
import com.materials.features.material.domain.model.PriceWithProvider
import com.materials.features.price_history.domain.model.PriceHistory
import com.materials.features.provider.domain.model.Provider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialsSelectedScreen(
    materialIds: List<String>,
    onBackClick: () -> Unit = {},
    onPrintPreview: (List<String>, Map<String, Double>) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
    viewModel: MaterialsSelectedViewModel = koinViewModel { parametersOf(materialIds, null) }
) {
    val uiState by viewModel.uiState.collectAsState()
    val quantities by viewModel.quantities.collectAsState()

    MaterialsSelectedScreenContent(
        uiState = uiState,
        quantities = quantities,
        onQuantityChange = viewModel::updateQuantity,
        onBackClick = onBackClick,
        onPrintPreview = { onPrintPreview(materialIds, quantities) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialsSelectedScreenContent(
    uiState: MaterialsSelectedUiState,
    quantities: Map<String, Double>,
    onQuantityChange: (String, Double) -> Unit,
    onBackClick: () -> Unit,
    onPrintPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Seleccionados",
                        fontWeight = FontWeight.ExtraBold,
                        color = IndustrialCharcoalDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    if (uiState is MaterialsSelectedUiState.Success) {
                        IconButton(onClick = onPrintPreview) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "Vista Previa de Impresión",
                                tint = IndustrialOrange
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = IndustrialBackground
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is MaterialsSelectedUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = IndustrialOrange
                    )
                }
                is MaterialsSelectedUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.materials) { materialWithPrices ->
                            SelectedMaterialCard(
                                materialWithPrices = materialWithPrices,
                                quantity = quantities[materialWithPrices.material.materialId] ?: 1.0,
                                onQuantityChange = { q -> 
                                    onQuantityChange(materialWithPrices.material.materialId, q)
                                }
                            )
                        }
                    }
                }
                is MaterialsSelectedUiState.Error -> {
                    Text(
                        text = uiState.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun SelectedMaterialCard(
    materialWithPrices: MaterialWithPrices,
    quantity: Double,
    onQuantityChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val material = materialWithPrices.material
    var quantityText by remember(quantity) { mutableStateOf(if (quantity == quantity.toInt().toDouble()) quantity.toInt().toString() else quantity.toString()) }
    val bestPrice = materialWithPrices.prices.minByOrNull { it.priceHistory.price }?.priceHistory?.price ?: 0.0
    val total = quantity * bestPrice

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(IndustrialShapes.medium)
            .border(
                width = 1.dp,
                color = Color.LightGray.copy(alpha = 0.3f),
                shape = IndustrialShapes.medium
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = material.name,
                        color = IndustrialCharcoalDark,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "ID: ${material.materialId}${materialWithPrices.maker?.let { " • ${it.name}" } ?: ""}",
                        color = IndustrialCharcoalMedium,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                if (material.unit != null) {
                    Surface(
                        color = IndustrialOrange.copy(alpha = 0.1f),
                        shape = IndustrialShapes.small
                    ) {
                        Text(
                            text = material.unit,
                            color = IndustrialOrange,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cantidad",
                        color = IndustrialCharcoalMedium,
                        style = MaterialTheme.typography.labelSmall
                    )
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { 
                            quantityText = it
                            it.toDoubleOrNull()?.let { q -> onQuantityChange(q) }
                        },
                        modifier = Modifier.width(90.dp).padding(top = 4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndustrialOrange,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                        )
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Precio Unit.",
                        color = IndustrialCharcoalMedium,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "$${bestPrice}",
                        color = IndustrialCharcoalDark,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Total",
                        color = IndustrialCharcoalMedium,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "$${((total * 100).toInt() / 100.0)}",
                        color = IndustrialOrange,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MaterialsSelectedScreenPreview() {
    IndustrialTheme {
        MaterialsSelectedScreenContent(
            uiState = MaterialsSelectedUiState.Success(
                listOf(
                    MaterialWithPrices(
                        material = Material(
                            materialId = "1",
                            name = "Tubo PVC Presión 1/2\"",
                            unit = "Metro",
                            makerId = "MAKER-01",
                            sectionId = "1"
                        ),
                        maker = Maker("MAKER-01", "Mexichem Amanco"),
                        prices = listOf(
                            PriceWithProvider(
                                priceHistory = PriceHistory("1", "1", "1", 12.5, "2024-05-16", "Cesar"),
                                provider = Provider("1", "Suministros Industriales", city = "Madrid")
                            )
                        )
                    ),
                    MaterialWithPrices(
                        material = Material(
                            materialId = "2",
                            name = "Codo 90° PVC 1/2\"",
                            unit = "Unidad",
                            makerId = "MAKER-02",
                            sectionId = "1"
                        ),
                        maker = Maker("MAKER-02", "Pavco Wavin"),
                        prices = listOf(
                            PriceWithProvider(
                                priceHistory = PriceHistory("2", "2", "2", 3.75, "2024-05-15", "Juan"),
                                provider = Provider("2", "Ferretería Central", city = "Barcelona")
                            )
                        )
                    )
                )
            ),
            quantities = mapOf("1" to 1.0, "2" to 1.0),
            onQuantityChange = { _, _ -> },
            onBackClick = {},
            onPrintPreview = {}
        )
    }
}
