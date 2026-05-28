package com.materials.core.presentation.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
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
import com.materials.features.home.presentation.HomeScreen
import com.materials.features.orders.presentation.OrdersScreen
import com.materials.features.profile.presentation.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val backstack = remember { mutableStateListOf<Screen>(Screen.Category) }
    val currentScreen = backstack.last()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Catálogo Industrial",
                        color = IndustrialOrange,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { /* Menú de opciones */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = IndustrialCharcoalDark
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
                    Triple("Inicio", Icons.Default.Home, Screen.Home),
                    Triple("Categorías", Icons.Default.GridView, Screen.Category),
                    Triple("Pedidos", Icons.AutoMirrored.Filled.ReceiptLong, Screen.Orders),
                    Triple("Perfil", Icons.Default.Person, Screen.Profile)
                )

                items.forEach { (label, icon, screen) ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = {
                            if (currentScreen != screen) {
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
                                fontWeight = if (currentScreen == screen) FontWeight.Bold else FontWeight.Normal,
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
                    Screen.Home -> HomeScreen()
                    Screen.Category -> CategoryScreen()
                    Screen.Orders -> OrdersScreen()
                    Screen.Profile -> ProfileScreen()
                }
            }
        }
    }
}
