package com.materials.features.material.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.materials.core.presentation.theme.*
import com.materials.features.material.domain.model.Material
import com.materials.core.util.date.formatDateToDisplay
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MaterialScreen(
    sectionId: String? = null,
    onBackClick: () -> Unit = {},
    onMaterialsSelected: (List<String>) -> Unit = {},
    columns: Int? = null,
    modifier: Modifier = Modifier,
    viewModel: MaterialViewModel = koinViewModel()
) {
    LaunchedEffect(sectionId) {
        viewModel.onEvent(MaterialEvent.SetSection(sectionId))
    }

    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedIds by viewModel.selectedMaterialIds.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    
    var editingMaterial by remember { mutableStateOf<Material?>(null) }

    MaterialScreenContent(
        uiState = uiState,
        searchQuery = searchQuery,
        selectedIds = selectedIds,
        isRefreshing = isRefreshing,
        onEvent = { viewModel.onEvent(it) },
        onBackClick = onBackClick,
        onProceed = { onMaterialsSelected(selectedIds.toList()) },
        onEditMaterial = { editingMaterial = it },
        columns = columns,
        modifier = modifier
    )
    
    editingMaterial?.let { material ->
        EditMaterialDialog(
            material = material,
            onDismiss = { editingMaterial = null },
            onConfirm = { updatedMaterial ->
                viewModel.onEvent(MaterialEvent.UpdateMaterial(updatedMaterial))
                editingMaterial = null
            }
        )
    }
}

@Composable
fun MaterialScreenContent(
    uiState: MaterialUiState,
    searchQuery: String,
    selectedIds: Set<String> = emptySet(),
    isRefreshing: Boolean = false,
    onEvent: (MaterialEvent) -> Unit,
    onBackClick: () -> Unit = {},
    onProceed: () -> Unit = {},
    onEditMaterial: (Material) -> Unit = {},
    columns: Int? = null,
    modifier: Modifier = Modifier
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val finalColumns = columns ?: with(adaptiveInfo.windowSizeClass) {
        when {
            isWidthAtLeastBreakpoint(840) -> 3
            isWidthAtLeastBreakpoint(600) -> 2
            else -> 1
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        containerColor = IndustrialBackground,
        floatingActionButton = {
            if (selectedIds.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onProceed,
                    containerColor = IndustrialOrange,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Check, contentDescription = null) },
                    text = { Text("Continuar (${selectedIds.size})") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                        text = "Materiales",
                        fontWeight = FontWeight.ExtraBold,
                        color = IndustrialCharcoalDark,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 28.sp,
                            letterSpacing = (-0.5).sp
                        )
                    )
                }
                Text(
                    text = "Catálogo detallado de materiales industriales.",
                    color = IndustrialCharcoalMedium,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(start = 40.dp)
                )
            }

            // Search Bar
            MaterialSearchBar(
                query = searchQuery,
                onQueryChange = { onEvent(MaterialEvent.OnSearchQueryChanged(it)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Body Area
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                when (val state = uiState) {
                    is MaterialUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = IndustrialOrange)
                        }
                    }

                    is MaterialUiState.Success -> {
                        if (state.materials.isEmpty()) {
                            if (isRefreshing) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = IndustrialOrange)
                                }
                            } else {
                                EmptyState()
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(finalColumns),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.materials, key = { it.material.materialId }) { materialItem ->
                                    val isSelected = selectedIds.contains(materialItem.material.materialId)
                                    MaterialCard(
                                        materialItem = materialItem,
                                        isSelected = isSelected,
                                        onSelect = {
                                            onEvent(
                                                MaterialEvent.ToggleMaterialSelection(
                                                    it
                                                )
                                            )
                                        },
                                        onEdit = { onEditMaterial(materialItem.material) }
                                    )
                                }
                            }
                        }
                    }

                    is MaterialUiState.Error -> {
                        ErrorState(
                            message = state.message,
                            onRetry = { onEvent(MaterialEvent.Refresh) })
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialSearchBar(
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
                text = "Buscar Material",
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
fun MaterialCard(
    materialItem: MaterialItem,
    isSelected: Boolean = false,
    onSelect: (String) -> Unit = {},
    onEdit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val material = materialItem.material
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(IndustrialShapes.medium)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) IndustrialOrange else Color.LightGray.copy(alpha = 0.3f),
                shape = IndustrialShapes.medium
            )
            .clickable { onSelect(material.materialId) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = material.name,
                            color = IndustrialCharcoalDark,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onEdit() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = IndustrialCharcoalMedium,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        
                        if (material.unit != null) {
                            Surface(
                                color = IndustrialOrange.copy(alpha = 0.1f),
                                shape = IndustrialShapes.small,
                                modifier = Modifier.padding(start = 8.dp)
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
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (materialItem.makerName != null) {
                                Text(
                                    text = materialItem.makerName,
                                    color = IndustrialCharcoalMedium,
                                    fontWeight = FontWeight.Medium,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = " • ",
                                    color = IndustrialCharcoalMedium,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                text = formatDateToDisplay(material.quoteDate),
                                color = IndustrialCharcoalDark,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Text(
                            text = if (material.price != null) "Bs. ${material.price}" else "---",
                            color = IndustrialOrange,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMaterialDialog(
    material: Material,
    onDismiss: () -> Unit,
    onConfirm: (Material) -> Unit
) {
    var name by remember { mutableStateOf(material.name) }
    var unit by remember { mutableStateOf(material.unit ?: "") }
    var specId by remember { mutableStateOf(material.specId ?: "") }
    var priceStr by remember { mutableStateOf(material.price?.toString() ?: "") }
    var quoteDate by remember { mutableStateOf(material.quoteDate ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Material", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unidad") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = specId,
                    onValueChange = { specId = it },
                    label = { Text("Spec ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Precio (Bs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quoteDate,
                    onValueChange = { quoteDate = it },
                    label = { Text("Fecha (e.g. 06-Ene-2026 o 06-01-2026)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        material.copy(
                            name = name,
                            unit = unit.ifBlank { null },
                            specId = specId.ifBlank { null },
                            price = priceStr.toDoubleOrNull(),
                            quoteDate = quoteDate.ifBlank { null }
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndustrialOrange)
            ) {
                Text("Guardar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = IndustrialCharcoalMedium)
            }
        },
        containerColor = Color.White
    )
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                tint = IndustrialCharcoalMedium,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No se encontraron materiales",
                color = IndustrialCharcoalDark,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Intenta ajustar los filtros o la búsqueda",
                color = IndustrialCharcoalMedium,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = IndustrialShapes.medium,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "Error",
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Error de Conexión",
                    fontWeight = FontWeight.Bold,
                    color = IndustrialCharcoalDark,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    color = IndustrialCharcoalMedium,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = IndustrialOrange),
                    shape = IndustrialShapes.small
                ) {
                    Text(text = "Reintentar", color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Success")
@Composable
fun MaterialScreenSuccessPreview() {
    IndustrialTheme {
        MaterialScreenContent(
            uiState = MaterialUiState.Success(
                listOf(
                    MaterialItem(
                        material = Material(
                            materialId = "1",
                            name = "Tubo PVC Presión 1/2\"",
                            unit = "Barra",
                            makerId = "MAKER-01",
                            sectionId = "1",
                            price = 12.5,
                            quoteDate = "12-06-2026"
                        ),
                        makerName = "Mexichem Amanco"
                    ),
                    MaterialItem(
                        material = Material(
                            materialId = "2",
                            name = "Codo 90° PVC 1/2\"",
                            unit = "Unidad",
                            makerId = "MAKER-02",
                            sectionId = "1",
                            price = 3.75,
                            quoteDate = "25-11-2026"
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

@Preview(showBackground = true, name = "Loading")
@Composable
fun MaterialScreenLoadingPreview() {
    IndustrialTheme {
        MaterialScreenContent(
            uiState = MaterialUiState.Loading,
            searchQuery = "",
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
fun MaterialScreenErrorPreview() {
    IndustrialTheme {
        MaterialScreenContent(
            uiState = MaterialUiState.Error("Error al cargar materiales"),
            searchQuery = "",
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
fun MaterialScreenEmptyPreview() {
    IndustrialTheme {
        MaterialScreenContent(
            uiState = MaterialUiState.Success(emptyList()),
            searchQuery = "",
            onEvent = {}
        )
    }
}
