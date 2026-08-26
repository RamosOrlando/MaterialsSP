package com.materials.core.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.materials.features.auth.presentation.LoginScreen
import com.materials.features.auth.presentation.SignUpScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
    viewModel: NavigationViewModel = koinViewModel()
) {
    val initialScreen by viewModel.initialScreen.collectAsState()
    val userRole by viewModel.userRole.collectAsState()

    if (initialScreen == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val backstack = remember { mutableStateListOf<Screen>(initialScreen!!) }

    NavDisplay(
        backStack = backstack,
        onBack = { if (backstack.size > 1) backstack.removeLast() }
    ) { key ->
        NavEntry(key) {
            when (key) {
                is Screen.Login -> LoginScreen(
                    onLoginSuccess = {
                        viewModel.onLoginSuccess()
                        backstack.clear()
                        backstack.add(Screen.Category)
                    },
                    onNavigateToSignUp = {
                        backstack.add(Screen.SignUp)
                    }
                )
                is Screen.SignUp -> SignUpScreen(
                    onBackClick = { backstack.removeLast() },
                    onSignUpSuccess = {
                        viewModel.onLoginSuccess()
                        backstack.clear()
                        backstack.add(Screen.Category)
                    }
                )
                else -> MainScreen(
                    onLogout = {
                        backstack.add(Screen.Login)
                    },
                    initialScreen = key,
                    userRole = userRole,
                    modifier = modifier
                )
            }
        }
    }
}
