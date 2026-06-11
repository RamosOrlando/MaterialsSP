package com.materials

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.materials.core.presentation.navigation.NavigationRoot
import com.materials.core.presentation.theme.IndustrialTheme
import com.materials.features.auth.presentation.LoginScreen

@Composable
@Preview
fun App() {
    IndustrialTheme {
        NavigationRoot()
    }
}
