package com.materials.features.price_history.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.materials.core.presentation.theme.*
import com.materials.features.material.domain.model.MaterialWithPrices
import com.materials.features.material.domain.model.PriceWithProvider
import com.materials.features.material.domain.model.Material
import com.materials.features.maker.domain.model.Maker
import com.materials.features.provider.domain.model.Provider
import com.materials.features.price_history.domain.model.PriceHistory
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PriceHistoryScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: PriceHistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    PriceHistoryScreenContent(
        uiState = uiState,
        searchQuery = searchQuery,
        onEvent = { viewModel.onEvent(it) },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun PriceHistoryScreenContent(
    uiState: PriceHistoryUiState,
    searchQuery: String,
    onEvent: (PriceHistoryEvent) -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IndustrialBackground)
    ) {
        // Header Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = IndustrialCharcoalDark
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Historial de Precios",
                    fontWeight = FontWeight.ExtraBold,
                    color = IndustrialCharcoalDark,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        letterSpacing = (-0.5).sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Seguimiento de cotizaciones y cambios de costos.",
                color = IndustrialCharcoalMedium,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                modifier = Modifier.padding(start = 40.dp)
            )
        }

        // Search Bar
        PriceHistorySearchBar(
            query = searchQuery,
            onQueryChange = { onEvent(PriceHistoryEvent.OnSearchQueryChanged(it)) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Body Area
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (val state = uiState) {
                is PriceHistoryUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = IndustrialOrange)
                    }
                }
                is PriceHistoryUiState.Success -> {
                    if (state.history.isEmpty()) {
                        EmptyHistoryState()
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.history, key = { it.material.materialId }) { materialWithPrices ->
                                PriceHistoryItem(materialWithPrices = materialWithPrices)
                            }
                        }
                    }
                }
                is PriceHistoryUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.message, color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun PriceHistorySearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = {
            Text(
                text = "Buscar material o proveedor",
                color = IndustrialCharcoalMedium.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Icono buscar",
                tint = IndustrialCharcoalDark
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        tint = IndustrialCharcoalMedium
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
            }
        ),
        singleLine = true,
        shape = IndustrialShapes.small,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = IndustrialOrange,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = IndustrialCharcoalDark,
            unfocusedTextColor = IndustrialCharcoalDark
        )
    )
}

@Composable
fun PriceHistoryItem(
    materialWithPrices: MaterialWithPrices,
    modifier: Modifier = Modifier
) {
    val latestPricesByProvider = remember(materialWithPrices) {
        materialWithPrices.prices
            .groupBy { it.provider?.providerId }
            .mapValues { entry ->
                entry.value.maxByOrNull { it.priceHistory.quoteDate ?: "" }
            }
            .values
            .filterNotNull()
            .sortedByDescending { it.priceHistory.quoteDate ?: "" }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(IndustrialShapes.medium)
            .border(1.dp, Color.LightGray.copy(alpha = 0.3f), IndustrialShapes.medium),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = materialWithPrices.material.name,
                color = IndustrialCharcoalDark,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            materialWithPrices.maker?.let { maker ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = maker.name,
                    color = IndustrialSteelBlue,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            if (latestPricesByProvider.isEmpty()) {
                Text(
                    text = "No hay cotizaciones disponibles",
                    color = IndustrialCharcoalMedium,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    latestPricesByProvider.forEach { priceWithProvider ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Business,
                                    contentDescription = null,
                                    tint = IndustrialSteelBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = priceWithProvider.provider?.name ?: "Proveedor desconocido",
                                    color = IndustrialCharcoalDark,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            Text(
                                text = "$${priceWithProvider.priceHistory.price}",
                                color = IndustrialOrange,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Text(
                            text = "Fecha: ${priceWithProvider.priceHistory.quoteDate}",
                            color = IndustrialCharcoalMedium,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryState() {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = IndustrialCharcoalMedium,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Historial vacío",
                color = IndustrialCharcoalDark,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "No hay registros de precios aún.",
                color = IndustrialCharcoalMedium,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true, name = "Success")
@Composable
fun PriceHistoryScreenSuccessPreview() {
    IndustrialTheme {
        PriceHistoryScreenContent(
            uiState = PriceHistoryUiState.Success(
                listOf(
                    MaterialWithPrices(
                        material = Material(
                            materialId = "1",
                            name = "Cemento Holcim Fuerte",
                            unit = "Bulto 50kg",
                            makerId = "MAKER-1",
                            sectionId = "1"
                        ),
                        maker = Maker("MAKER-1", "Holcim"),
                        prices = listOf(
                            PriceWithProvider(
                                priceHistory = PriceHistory(
                                    historyId = "1",
                                    materialId = "1",
                                    providerId = "1",
                                    price = 185.50,
                                    quoteDate = "2024-05-16",
                                    username = "admin"
                                ),
                                provider = Provider(
                                    providerId = "1",
                                    name = "Materiales del Norte",
                                    city = "Monterrey"
                                )
                            ),
                            PriceWithProvider(
                                priceHistory = PriceHistory(
                                    historyId = "2",
                                    materialId = "1",
                                    providerId = "2",
                                    price = 190.00,
                                    quoteDate = "2024-05-15",
                                    username = "admin"
                                ),
                                provider = Provider(
                                    providerId = "2",
                                    name = "Ferretería El Martillo",
                                    city = "Monterrey"
                                )
                            )
                        )
                    )
                )
            ),
            searchQuery = "",
            onEvent = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
fun PriceHistoryScreenLoadingPreview() {
    IndustrialTheme {
        PriceHistoryScreenContent(
            uiState = PriceHistoryUiState.Loading,
            searchQuery = "",
            onEvent = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
fun PriceHistoryScreenErrorPreview() {
    IndustrialTheme {
        PriceHistoryScreenContent(
            uiState = PriceHistoryUiState.Error("Error al cargar el historial."),
            searchQuery = "",
            onEvent = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
fun PriceHistoryScreenEmptyPreview() {
    IndustrialTheme {
        PriceHistoryScreenContent(
            uiState = PriceHistoryUiState.Success(emptyList()),
            searchQuery = "Material inexistente",
            onEvent = {},
            onBackClick = {}
        )
    }
}
