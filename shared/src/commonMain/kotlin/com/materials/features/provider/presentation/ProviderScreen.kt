package com.materials.features.provider.presentation

import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.materials.core.presentation.theme.*
import com.materials.features.provider.domain.model.Provider
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProviderScreen(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProviderViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    ProviderScreenContent(
        uiState = uiState,
        searchQuery = searchQuery,
        onEvent = { viewModel.onEvent(it) },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun ProviderScreenContent(
    uiState: ProviderUiState,
    searchQuery: String,
    onEvent: (ProviderEvent) -> Unit,
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
                    text = "Proveedores",
                    fontWeight = FontWeight.ExtraBold,
                    color = IndustrialCharcoalDark,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        letterSpacing = (-0.5).sp
                    )
                )
            }
            Text(
                text = "Gestione sus proveedores de materiales.",
                color = IndustrialCharcoalMedium,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                modifier = Modifier.padding(start = 40.dp)
            )
        }

        // Search Bar
        ProviderSearchBar(
            query = searchQuery,
            onQueryChange = { onEvent(ProviderEvent.OnSearchQueryChanged(it)) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Body Area
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (val state = uiState) {
                is ProviderUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = IndustrialOrange)
                    }
                }
                is ProviderUiState.Success -> {
                    if (state.providers.isEmpty()) {
                        EmptyState()
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.providers, key = { it.providerId }) { provider ->
                                ProviderCard(provider = provider)
                            }
                        }
                    }
                }
                is ProviderUiState.Error -> {
                    ErrorState(message = state.message, onRetry = { onEvent(ProviderEvent.Refresh) })
                }
            }
        }
    }
}

@Composable
fun ProviderSearchBar(
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
                text = "Buscar Proveedor",
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
fun ProviderCard(
    provider: Provider,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(IndustrialShapes.medium)
            .background(Color.White)
            .clickable { /* Ver detalles del proveedor */ }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White.copy(alpha = 0.9f))
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Text(
                text = provider.name,
                color = IndustrialCharcoalDark,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 14.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.Start)
            )
            // Construimos la ubicación uniendo los campos de forma segura
            val locationText = remember(provider.address, provider.city) {
                listOfNotNull(provider.address, provider.city)
                    .joinToString(separator = " - ")
            }

            if (locationText.isNotEmpty()) {
                Text(
                    text = locationText,
                    color = IndustrialCharcoalMedium,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = IndustrialCharcoalMedium,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No se encontraron proveedores",
                color = IndustrialCharcoalDark,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Intenta buscar con otros términos",
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
fun ProviderScreenSuccessPreview() {
    IndustrialTheme {
        ProviderScreenContent(
            uiState = ProviderUiState.Success(
                listOf(
                    Provider(
                        providerId = "1",
                        name = "Suministros Industriales",
                        address = "Calle Montesinos No. 200",
                        city = "Madrid",
                        imagePath = ""
                    ),
                    Provider(
                        providerId = "2",
                        name = "Ferretería Central",
                        address = "Av. Brasil, entre Santa Barbara",
                        city = "Barcelona",
                        imagePath = ""
                    )
                )
            ),
            searchQuery = "",
            onEvent = {}
        )
    }
}
