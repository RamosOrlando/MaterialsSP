package com.materials.core.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.materials.core.presentation.theme.IndustrialBackground
import com.materials.core.presentation.theme.IndustrialCharcoalDark
import com.materials.core.presentation.theme.IndustrialCharcoalMedium
import com.materials.core.presentation.theme.IndustrialOrange
import com.materials.features.category.presentation.CategoryScreen
import com.materials.features.section.presentation.SectionScreen
import com.materials.features.maker.presentation.MakerScreen
import com.materials.features.material.presentation.MaterialScreen
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Catálogo Industrial",
                            color = IndustrialOrange,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (userEmail.isNotEmpty()) {
                            Text(
                                text = userEmail,
                                color = IndustrialCharcoalMedium,
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
                            tint = IndustrialCharcoalDark
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color.White)
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
                    containerColor = Color.White
                ),
                modifier = Modifier.border(width = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val items = listOf(
                    Triple("Catalogo", Icons.Default.GridView, Screen.Category),
                    Triple("Fabricantes", Icons.Default.Business, Screen.Maker),
                    Triple("Proveedores", Icons.Default.LocalShipping, Screen.Provider),
                    Triple("Historial", Icons.Default.History, Screen.PriceHistory)
                )

                items.forEach { (label, icon, screen) ->
                    val isSelected = when (screen) {
                        is Screen.Category if (currentScreen is Screen.Category || currentScreen is Screen.Section || currentScreen is Screen.Material) -> true
                        is Screen.Maker if currentScreen is Screen.Maker -> true
                        is Screen.Provider if currentScreen is Screen.Provider -> true
                        is Screen.PriceHistory if currentScreen is Screen.PriceHistory -> true
                        else -> currentScreen == screen
                    }
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected || (screen is Screen.Category && (currentScreen is Screen.Section || currentScreen is Screen.Material))) {
                                backstack.clear()
                                backstack.add(screen)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndustrialOrange,
                            selectedTextColor = IndustrialOrange,
                            indicatorColor = Color(0xFFFFDBCC).copy(alpha = 0.5f),
                            unselectedIconColor = IndustrialCharcoalMedium,
                            unselectedTextColor = IndustrialCharcoalMedium
                        )
                    )
                }
            }
        },
        containerColor = IndustrialBackground
    ) { innerPadding ->
        NavDisplay(
            backStack = backstack,
            modifier = Modifier.padding(innerPadding),
            onBack = { if (backstack.size > 1) backstack.removeLast() }
        ) { key ->
            NavEntry(key) {
                when (key) {
                    Screen.Category -> CategoryScreen(
                        onCategoryClick = { categoryId ->
                            backstack.add(Screen.Section(categoryId))
                        },
                        onLogout = onLogout
                    )
                    is Screen.Section -> SectionScreen(
                        categoryId = key.categoryId,
                        onSectionClick = { sectionId ->
                            backstack.add(Screen.Material(sectionId))
                        },
                        onBackClick = {
                            if (backstack.size > 1) {
                                backstack.removeLast()
                            } else {
                                backstack.clear()
                                backstack.add(Screen.Category)
                            }
                        }
                    )
                    is Screen.Material -> MaterialScreen(
                        sectionId = key.sectionId,
                        onBackClick = {
                            if (backstack.size > 1) {
                                backstack.removeLast()
                            } else {
                                backstack.clear()
                                backstack.add(Screen.Category)
                            }
                        }
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
