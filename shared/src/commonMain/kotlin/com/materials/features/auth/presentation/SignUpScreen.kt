package com.materials.features.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.materials.core.presentation.theme.*
import com.materials.core.presentation.util.AdaptivePreviews
import com.materials.features.user.domain.model.UserPlan
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: SignUpViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSignUpSuccess()
            viewModel.onEvent(SignUpEvent.ClearSuccess)
        }
    }

    LaunchedEffect(uiState.isCancelled) {
        if (uiState.isCancelled) {
            onBackClick()
            viewModel.onEvent(SignUpEvent.ClearSuccess)
        }
    }

    SignUpScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick
    )

    if (uiState.showSubscriptionDialog) {
        PlanSubscriptionDialog(
            userName = uiState.name,
            plans = uiState.activePlans,
            selectedPlanId = uiState.selectedPlanId,
            onPlanSelected = { viewModel.onEvent(SignUpEvent.OnPlanSelected(it)) },
            onConfirm = { viewModel.onEvent(SignUpEvent.OnConfirmPlan) },
            onCancel = { viewModel.onEvent(SignUpEvent.OnCancelSignUp) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreenContent(
    uiState: SignUpUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (SignUpEvent) -> Unit,
    onBackClick: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isCompact = !adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(600)

    var roleExpanded by remember { mutableStateOf(false) }
    var professionExpanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = if (isCompact) 600.dp else 480.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = IndustrialOrange
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Crear Cuenta",
                        style = MaterialTheme.typography.headlineSmall,
                        color = IndustrialOrange,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = IndustrialShapes.medium,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Nombre y Apellidos
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = uiState.name,
                                onValueChange = { onEvent(SignUpEvent.OnNameChanged(it)) },
                                label = { Text("Nombre") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                shape = IndustrialShapes.small,
                                colors = signUpTextFieldColors()
                            )
                            OutlinedTextField(
                                value = uiState.lastName,
                                onValueChange = { onEvent(SignUpEvent.OnLastNameChanged(it)) },
                                label = { Text("Apellidos") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                shape = IndustrialShapes.small,
                                colors = signUpTextFieldColors()
                            )
                        }

                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { onEvent(SignUpEvent.OnEmailChanged(it)) },
                            label = { Text("Correo Electrónico") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            singleLine = true,
                            shape = IndustrialShapes.small,
                            colors = signUpTextFieldColors()
                        )

                        OutlinedTextField(
                            value = uiState.cellphone,
                            onValueChange = { onEvent(SignUpEvent.OnCellphoneChanged(it)) },
                            label = { Text("Celular (Opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            singleLine = true,
                            shape = IndustrialShapes.small,
                            colors = signUpTextFieldColors()
                        )

                        // Role Dropdown
                        ExposedDropdownMenuBox(
                            expanded = roleExpanded,
                            onExpandedChange = { roleExpanded = !roleExpanded }
                        ) {
                            val selectedRole = uiState.roles.find { it.roleId == uiState.roleId }?.name ?: "Selecciona tu Rol"
                            OutlinedTextField(
                                value = selectedRole,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Rol") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                                shape = IndustrialShapes.small,
                                colors = signUpTextFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = roleExpanded,
                                onDismissRequest = { roleExpanded = false }
                            ) {
                                uiState.roles.forEach { role ->
                                    DropdownMenuItem(
                                        text = { Text(role.name) },
                                        onClick = {
                                            role.roleId?.let { onEvent(SignUpEvent.OnRoleSelected(it)) }
                                            roleExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Profession Dropdown
                        ExposedDropdownMenuBox(
                            expanded = professionExpanded,
                            onExpandedChange = { professionExpanded = !professionExpanded }
                        ) {
                            val selectedProfession = uiState.professions.find { it.professionId == uiState.professionId }?.name ?: "Selecciona tu Profesión"
                            OutlinedTextField(
                                value = selectedProfession,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Profesión") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = professionExpanded) },
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                                shape = IndustrialShapes.small,
                                colors = signUpTextFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = professionExpanded,
                                onDismissRequest = { professionExpanded = false }
                            ) {
                                uiState.professions.forEach { profession ->
                                    DropdownMenuItem(
                                        text = { Text(profession.name) },
                                        onClick = {
                                            profession.professionId?.let { onEvent(SignUpEvent.OnProfessionSelected(it)) }
                                            professionExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = { onEvent(SignUpEvent.OnPasswordChanged(it)) },
                            label = { Text("Contraseña") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            singleLine = true,
                            shape = IndustrialShapes.small,
                            colors = signUpTextFieldColors()
                        )

                        OutlinedTextField(
                            value = uiState.confirmPassword,
                            onValueChange = { onEvent(SignUpEvent.OnConfirmPasswordChanged(it)) },
                            label = { Text("Confirmar Contraseña") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary) },
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            singleLine = true,
                            shape = IndustrialShapes.small,
                            colors = signUpTextFieldColors()
                        )

                        if (uiState.error != null) {
                            Text(
                                text = uiState.error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Button(
                            onClick = { onEvent(SignUpEvent.OnSignUpClicked) },
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
                                Text("REGISTRARSE", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlanSubscriptionDialog(
    userName: String,
    plans: List<UserPlan>,
    selectedPlanId: Int?,
    onPlanSelected: (Int) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { 
            Column {
                Text(
                    text = "¡Bienvenido, $userName!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = IndustrialOrange,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Selecciona un Plan", 
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium
                ) 
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Elige el plan que mejor se adapte a tus necesidades para comenzar a usar el catálogo.",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                plans.forEach { plan ->
                    val isSelected = plan.planId == selectedPlanId
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        onClick = { plan.planId?.let { onPlanSelected(it) } },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) 
                                IndustrialOrange.copy(alpha = 0.1f) 
                            else 
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = if (isSelected) 
                            androidx.compose.foundation.BorderStroke(2.dp, IndustrialOrange) 
                        else null,
                        shape = IndustrialShapes.small
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = plan.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${plan.durationDays} días de acceso",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "Bs. ${plan.price}",
                                fontWeight = FontWeight.ExtraBold,
                                color = IndustrialOrange,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = selectedPlanId != null,
                colors = ButtonDefaults.buttonColors(containerColor = IndustrialOrange)
            ) {
                Text("Concluir")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Elegir plan más tarde", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
fun signUpTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedBorderColor = IndustrialOrange,
    cursorColor = IndustrialOrange,
    focusedLabelColor = IndustrialOrange
)

@AdaptivePreviews
@Composable
private fun SignUpScreenPreview() {
    IndustrialTheme {
        SignUpScreenContent(
            uiState = SignUpUiState(
                name = "John",
                lastName = "Doe",
                email = "john@example.com"
            ),
            snackbarHostState = SnackbarHostState(),
            onEvent = {},
            onBackClick = {}
        )
    }
}
