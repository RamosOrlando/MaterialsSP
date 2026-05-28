package com.materials

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.materials.core.presentation.navigation.NavigationRoot
import com.materials.core.presentation.theme.IndustrialTheme

@Composable
@Preview
fun App() {
    IndustrialTheme {
        NavigationRoot()
    }
}
