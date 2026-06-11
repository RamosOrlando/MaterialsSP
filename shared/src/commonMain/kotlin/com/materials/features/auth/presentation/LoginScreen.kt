package com.materials.features.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.materials.core.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IndustrialBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MaterialsSP",
            style = MaterialTheme.typography.headlineLarge,
            color = IndustrialOrange,
            fontWeight = FontWeight.ExtraBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Gestión Industrial de Materiales",
            style = MaterialTheme.typography.bodyMedium,
            color = IndustrialCharcoalMedium
        )

        Spacer(modifier = Modifier.height(48.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
            shape = IndustrialShapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.titleLarge,
                    color = IndustrialCharcoalDark
                )

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { onEvent(LoginEvent.OnEmailChanged(it)) },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = IndustrialSteelBlue)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    shape = IndustrialShapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndustrialOrange,
                        cursorColor = IndustrialOrange,
                        focusedLabelColor = IndustrialOrange
                    )
                )

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { onEvent(LoginEvent.OnPasswordChanged(it)) },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = IndustrialSteelBlue)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                                tint = IndustrialSteelBlue
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    shape = IndustrialShapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndustrialOrange,
                        cursorColor = IndustrialOrange,
                        focusedLabelColor = IndustrialOrange
                    )
                )

                if (uiState.error != null) {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = { onEvent(LoginEvent.OnSignInClicked) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = IndustrialShapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = IndustrialOrange),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("ENTRAR", fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(
                    onClick = { onEvent(LoginEvent.OnSignUpClicked) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        "¿No tienes cuenta? Regístrate",
                        color = IndustrialSteelBlue,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    IndustrialTheme {
        LoginScreenContent(
            uiState = LoginUiState(
                email = "industrial@material.com",
                password = "password123"
            ),
            onEvent = {}
        )
    }
}

@Preview
@Composable
private fun LoginScreenLoadingPreview() {
    IndustrialTheme {
        LoginScreenContent(
            uiState = LoginUiState(
                isLoading = true
            ),
            onEvent = {}
        )
    }
}
