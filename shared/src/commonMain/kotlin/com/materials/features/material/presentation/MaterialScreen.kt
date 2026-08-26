package com.materials.features.material.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.materials.core.presentation.theme.*
import com.materials.features.material.domain.model.Material
import com.materials.core.util.date.formatDateToDisplay
import com.materials.core.util.randomUUID
import com.materials.core.util.getCurrentDate
import com.materials.features.auth.domain.model.UserRole
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MaterialScreen(
    sectionId: String? = null,
    onBackClick: () -> Unit = {},
    onMaterialsSelected: (List<String>) -> Unit = {},
    userRole: UserRole? = null,
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
    val nextIndex by viewModel.nextIndex.collectAsState()
    val userEmail = viewModel.userEmail
    
    var editingMaterial by remember { mutableStateOf<Material?>(null) }
    var editingProviderName by remember { mutableStateOf<String?>(null) }
    var bulkEditingMaterials by remember { mutableStateOf<List<Material>?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState) {
        (uiState as? MaterialUiState.Success)?.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                actionLabel = "OK",
                duration = SnackbarDuration.Long
            )
            viewModel.onEvent(MaterialEvent.ClearError)
        }
    }

    MaterialScreenContent(
        uiState = uiState,
        searchQuery = searchQuery,
        selectedIds = selectedIds,
        isRefreshing = isRefreshing,
        snackbarHostState = snackbarHostState,
        onEvent = { viewModel.onEvent(it) },
        onBackClick = onBackClick,
        onProceed = { onMaterialsSelected(selectedIds.toList()) },
        onEditMaterial = { item -> 
            editingMaterial = item.material
            editingProviderName = item.providerName
        },
        onBulkEdit = { bulkEditingMaterials = it },
        userRole = userRole,
        modifier = modifier
    )
    
    editingMaterial?.let { material ->
        val providers = (uiState as? MaterialUiState.Success)?.providers ?: emptyList()
        EditMaterialDialog(
            material = material,
            providers = providers,
            userEmail = userEmail,
            nextIndex = nextIndex,
            onDismiss = { 
                editingMaterial = null
                editingProviderName = null
            },
            onConfirm = { updatedMaterial ->
                viewModel.onEvent(MaterialEvent.UpdateMaterial(updatedMaterial))
                editingMaterial = null
                editingProviderName = null
            }
        )
    }

    bulkEditingMaterials?.let { materials ->
        val first = materials.firstOrNull() ?: return@let
        BulkEditMaterialDialog(
            name = first.name,
            unit = first.unit,
            specId = first.specId,
            onDismiss = { bulkEditingMaterials = null },
            onConfirm = { newName, newUnit, newSpecId ->
                val trimmedName = newName.trim()
                val trimmedUnit = newUnit.trim()
                val trimmedSpecId = newSpecId?.trim()?.ifBlank { null }
                
                val updatedList = materials.map { 
                    it.copy(
                        name = trimmedName, 
                        unit = trimmedUnit,
                        specId = trimmedSpecId
                    ) 
                }
                viewModel.onEvent(MaterialEvent.BulkUpdateMaterials(updatedList))
                bulkEditingMaterials = null
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
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onEvent: (MaterialEvent) -> Unit,
    onBackClick: () -> Unit = {},
    onProceed: () -> Unit = {},
    onEditMaterial: (MaterialItem) -> Unit = {},
    onBulkEdit: (List<Material>) -> Unit = {},
    userRole: UserRole? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (selectedIds.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onProceed,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
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
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Materiales",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 28.sp,
                            letterSpacing = (-0.5).sp
                        )
                    )
                }
                Text(
                    text = "Catálogo detallado de materiales industriales.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    is MaterialUiState.Success -> {
                        if (state.materials.isEmpty()) {
                            if (isRefreshing) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                }
                            } else {
                                EmptyState()
                            }
                        } else {
                            val groupedMaterials = remember(state.materials) {
                                state.materials.groupBy { it.material.name + it.material.unit }
                            }

                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                groupedMaterials.forEach { (key, items) ->
                                    val firstItem = items.first()
                                    
                                    item(key = "header_$key") {
                                        MaterialHeaderCard(
                                            name = firstItem.material.name,
                                            unit = firstItem.material.unit,
                                            canEdit = userRole != UserRole.CLIENT,
                                            onEditClick = { onBulkEdit(items.map { it.material }) }
                                        )
                                    }

                                    items(items, key = { it.material.materialId }) { materialItem ->
                                        val isSelected = selectedIds.contains(materialItem.material.materialId)
                                        MakerCard(
                                            materialItem = materialItem,
                                            isSelected = isSelected,
                                            canEdit = userRole != UserRole.CLIENT,
                                            onSelect = {
                                                onEvent(
                                                    MaterialEvent.ToggleMaterialSelection(
                                                        it
                                                    )
                                                )
                                            },
                                            onEdit = { onEditMaterial(materialItem) },
                                            modifier = Modifier.padding(start = 16.dp)
                                        )
                                    }
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
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Icono buscar",
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
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
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun MaterialHeaderCard(
    name: String,
    unit: String,
    canEdit: Boolean = false,
    onEditClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = IndustrialShapes.medium
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            
            if (unit.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = IndustrialShapes.small,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = unit,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (canEdit) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Material",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MakerCard(
    materialItem: MaterialItem,
    isSelected: Boolean = false,
    canEdit: Boolean = true,
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
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                shape = IndustrialShapes.medium
            )
            .clickable { onSelect(material.materialId) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = materialItem.makerName,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "📅 ${formatDateToDisplay(material.quoteDate)}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = " • ",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = materialItem.providerName ?: "---",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (material.price != null) "Bs. ${material.price}" else "---",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    if (canEdit) {
                        IconButton(
                            onClick = { onEdit() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMaterialDialog(
    material: Material,
    providers: List<com.materials.features.provider.domain.model.Provider> = emptyList(),
    userEmail: String? = null,
    nextIndex: Int = 1,
    onDismiss: () -> Unit,
    onConfirm: (Material) -> Unit
) {
    val isPriceCreation = material.historyId == null
    val isPriceUpdate = material.historyId != null

    var providerId by remember { mutableStateOf(material.providerId ?: "") }
    var priceStr by remember { mutableStateOf(material.price?.toString() ?: "") }
    
    var priceError by remember { mutableStateOf<String?>(null) }
    var providerError by remember { mutableStateOf<String?>(null) }
    
    val quoteDate = remember(material.historyId) {
        getCurrentDate() // Siempre usamos la fecha actual al guardar un precio
    }
    
    val historyId = remember(material.historyId) {
        if (isPriceCreation) randomUUID() else material.historyId
    }

    val dialogTitle = if (isPriceCreation) "Crear Precio" else "Actualizar Precio"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Info Section
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = IndustrialShapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Material: ${material.name}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ID: ${material.materialId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (historyId != null) {
                            Text(
                                text = "Historial ID: $historyId",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (userEmail != null) {
                            Text(
                                text = "Usuario: $userEmail",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Fecha: ${formatDateToDisplay(quoteDate)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (isPriceCreation) {
                    OutlinedTextField(
                        value = providerId,
                        onValueChange = { 
                            providerId = it
                            providerError = null
                        },
                        label = { Text("ID Proveedor") },
                        isError = providerError != null,
                        supportingText = {
                            if (providerError != null) {
                                Text(text = providerError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { 
                        priceStr = it
                        priceError = null 
                    },
                    label = { Text("Precio (Bs.)") },
                    isError = priceError != null,
                    supportingText = {
                        if (priceError != null) {
                            Text(text = priceError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cleanPriceStr = priceStr.trim()
                    val parsedPrice = cleanPriceStr.toDoubleOrNull()
                    
                    val providerExists = providers.any { it.providerId == providerId.trim() }
                    
                    if (parsedPrice == null) {
                        priceError = "Introduce un número válido"
                    } else if (isPriceCreation && !providerExists) {
                        providerError = "El ID de proveedor no existe"
                    } else {
                        onConfirm(
                            material.copy(
                                historyId = historyId,
                                providerId = providerId.trim().ifBlank { null },
                                price = parsedPrice,
                                quoteDate = quoteDate
                            )
                        )
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkEditMaterialDialog(
    name: String,
    unit: String,
    specId: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String?) -> Unit
) {
    var newName by remember { mutableStateOf(name) }
    var newUnit by remember { mutableStateOf(unit) }
    var newSpecId by remember { mutableStateOf(specId ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Material (Grupal)", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Esta acción cambiará el nombre, unidad y Spec ID de todos los registros de este material.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newUnit,
                    onValueChange = { newUnit = it },
                    label = { Text("Unidad") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newSpecId,
                    onValueChange = { newSpecId = it },
                    label = { Text("Spec ID") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(newName, newUnit, newSpecId) }
            ) {
                Text("Actualizar Todo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No se encontraron materiales",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Intenta ajustar los filtros o la búsqueda",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Error de Conexión",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = IndustrialShapes.small
                ) {
                    Text(text = "Reintentar", color = MaterialTheme.colorScheme.onPrimary)
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
                            specId = null,
                            historyId = null,
                            providerId = null,
                            price = 12.5,
                            quoteDate = "12-06-2026"
                        ),
                        makerName = "Mexichem Amanco",
                        providerName = "Ferretería Central"
                    ),
                    MaterialItem(
                        material = Material(
                            materialId = "2",
                            name = "Codo 90° PVC 1/2\"",
                            unit = "Unidad",
                            makerId = "MAKER-02",
                            sectionId = "1",
                            specId = null,
                            historyId = null,
                            providerId = null,
                            price = 3.75,
                            quoteDate = "25-11-2026"
                        ),
                        makerName = "Pavco Wavin",
                        providerName = "Materiales del Norte"
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
