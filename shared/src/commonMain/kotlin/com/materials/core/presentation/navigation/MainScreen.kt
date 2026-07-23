package com.materials.core.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.materials.core.presentation.navigation.components.CatalogDetailPlaceholder
import com.materials.features.category.presentation.CategoryScreen
import com.materials.features.section.presentation.SectionScreen
import com.materials.features.maker.presentation.MakerScreen
import com.materials.features.material.presentation.MaterialScreen
import com.materials.features.material.presentation.MaterialsSelectedScreen
import com.materials.features.material.presentation.PrintPreviewScreen
import com.materials.features.price_history.presentation.PriceHistoryScreen
import com.materials.features.provider.presentation.ProviderScreen
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit = {},
    initialScreen: Screen = Screen.Category,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel()
) {
    val backstack = remember { mutableStateListOf<Screen>(initialScreen) }
    val currentScreen = backstack.last()
    val userEmail = viewModel.userEmail

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val shouldUseRail = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(600)

    val navItems = remember {
        listOf(
            NavigationItem("Catalogo", Icons.Default.GridView, Screen.Category),
            NavigationItem("Fabricantes", Icons.Default.Business, Screen.Maker),
            NavigationItem("Proveedores", Icons.Default.LocalShipping, Screen.Provider),
            NavigationItem("Historial", Icons.Default.History, Screen.PriceHistory)
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Catálogo Industrial",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (userEmail.isNotEmpty()) {
                            Text(
                                text = userEmail,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión") },
                            onClick = {
                                showMenu = false
                                onLogout()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.border(width = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            )
        },
        bottomBar = {
            if (!shouldUseRail) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    navItems.forEach { item ->
                        val isSelected = isNavItemSelected(currentScreen, item.screen)
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { handleNavClick(item.screen, currentScreen, backstack) },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (shouldUseRail) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    navItems.forEach { item ->
                        val isSelected = isNavItemSelected(currentScreen, item.screen)
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = { handleNavClick(item.screen, currentScreen, backstack) },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            NavDisplay(
                backStack = backstack,
                modifier = Modifier.weight(1f),
                onBack = { if (backstack.size > 1) backstack.removeLast() }
            ) { key ->
                NavEntry(key) {
                    when (key) {
                        Screen.Category, is Screen.Section, is Screen.Material -> {
                            CatalogPane(
                                screen = key,
                                shouldUseDualPane = shouldUseRail,
                                backstack = backstack,
                                onLogout = onLogout
                            )
                        }
                        is Screen.MaterialsSelected -> MaterialsSelectedScreen(
                            materialIds = key.materialIds,
                            onBackClick = { backstack.removeLast() },
                            onPrintPreview = { ids, quantities ->
                                backstack.add(Screen.PrintPreview(ids, quantities))
                            }
                        )
                        is Screen.PrintPreview -> PrintPreviewScreen(
                            materialIds = key.materialIds,
                            quantities = key.quantities,
                            onBackClick = { backstack.removeLast() }
                        )
                        Screen.Maker -> MakerScreen(
                            onBackClick = {
                                backstack.clear()
                                backstack.add(Screen.Category)
                            }
                        )
                        Screen.Provider -> ProviderScreen(
                            onBackClick = {
                                backstack.clear()
                                backstack.add(Screen.Category)
                            }
                        )
                        Screen.PriceHistory -> PriceHistoryScreen(
                            onBackClick = {
                                backstack.clear()
                                backstack.add(Screen.Category)
                            }
                        )
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogPane(
    screen: Screen,
    shouldUseDualPane: Boolean,
    backstack: SnapshotStateList<Screen>,
    onLogout: () -> Unit
) {
    if (shouldUseDualPane) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                CategoryScreen(
                    onCategoryClick = { id ->
                        if (screen is Screen.Category) {
                            backstack.add(element = Screen.Section(id))
                        } else {
                            // If already in detail, replace the detail
                            backstack.removeAt(backstack.size - 1)
                            backstack.add(element = Screen.Section(id))
                        }
                    },
                    onLogout = onLogout,
                    selectedCategoryId = when (screen) {
                        is Screen.Section -> screen.categoryId
                        // For material, we'd need to know its parent category... 
                        // for now let's just highlight if it's a section
                        else -> null
                    },
                    columns = 1
                )
            }
            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Box(modifier = Modifier.weight(2.4f).fillMaxHeight()) {
                when (screen) {
                    Screen.Category -> CatalogDetailPlaceholder()
                    is Screen.Section -> SectionScreen(
                        categoryId = screen.categoryId,
                        onSectionClick = { sectionId ->
                            backstack.add(element = Screen.Material(sectionId))
                        },
                        onBackClick = { backstack.removeAt(backstack.size - 1) }
                    )
                    is Screen.Material -> MaterialScreen(
                        sectionId = screen.sectionId,
                        onBackClick = { backstack.removeAt(backstack.size - 1) },
                        onMaterialsSelected = { ids ->
                            backstack.add(element = Screen.MaterialsSelected(ids))
                        },
                        columns = 1
                    )
                    else -> {}
                }
            }
        }
    } else {
        // Single Pane
        when (screen) {
            Screen.Category -> CategoryScreen(
                onCategoryClick = { id -> backstack.add(Screen.Section(id)) },
                onLogout = onLogout
            )
            is Screen.Section -> SectionScreen(
                categoryId = screen.categoryId,
                onSectionClick = { sectionId -> backstack.add(Screen.Material(sectionId)) },
                onBackClick = { backstack.removeLast() }
            )
            is Screen.Material -> MaterialScreen(
                sectionId = screen.sectionId,
                onBackClick = { backstack.removeLast() },
                onMaterialsSelected = { ids ->
                    backstack.add(Screen.MaterialsSelected(ids))
                }
            )
            else -> {}
        }
    }
}

private data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen
)

private fun isNavItemSelected(currentScreen: Screen, targetScreen: Screen): Boolean {
    return when (targetScreen) {
        is Screen.Category -> currentScreen is Screen.Category || currentScreen is Screen.Section || currentScreen is Screen.Material
        is Screen.Maker -> currentScreen is Screen.Maker
        is Screen.Provider -> currentScreen is Screen.Provider
        is Screen.PriceHistory -> currentScreen is Screen.PriceHistory
        else -> currentScreen == targetScreen
    }
}

private fun handleNavClick(targetScreen: Screen, currentScreen: Screen, backstack: MutableList<Screen>) {
    val isSelected = isNavItemSelected(currentScreen, targetScreen)
    if (!isSelected || (targetScreen is Screen.Category && (currentScreen is Screen.Section || currentScreen is Screen.Material))) {
        backstack.clear()
        backstack.add(targetScreen)
    }
}
