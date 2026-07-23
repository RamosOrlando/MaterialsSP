# Plan: Pantalla de Recuperación y Cambio de Contraseña

Este plan detalla la creación de una nueva pantalla para que los usuarios puedan recuperar su contraseña (enviando un correo de restablecimiento) o cambiarla directamente usando Supabase, siguiendo una arquitectura limpia y un diseño adaptativo.

## User Review Required

> [!IMPORTANT]
> El flujo de recuperación de contraseña en Supabase requiere que el usuario haga clic en un enlace enviado a su correo. Para que esto funcione de forma óptima en dispositivos móviles, se requeriría configurar "Deep Links". Por ahora, implementaremos la lógica del lado de la aplicación (enviar correo y actualizar contraseña) y el usuario podrá usar el sitio web de Supabase o volver a la app si se maneja el enlace de redirección.

## Proposed Changes

### [Core: Navegación y Repositorio]

#### [MODIFY] [AuthRepository.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/domain/repository/AuthRepository.kt)
- Agregar `suspend fun resetPassword(email: String): Result<Unit>`
- Agregar `suspend fun updatePassword(newPassword: String): Result<Unit>`

#### [MODIFY] [AuthRepositoryImpl.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/data/repository/AuthRepositoryImpl.kt)
- Implementar los nuevos métodos usando `supabaseClient.auth`.

#### [MODIFY] [Screen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/presentation/navigation/Screen.kt)
- Agregar `data object ForgotPassword : Screen`

#### [MODIFY] [NavigationRoot.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/presentation/navigation/NavigationRoot.kt)
- Manejar la navegación hacia `ForgotPasswordScreen`.

### [Feature: Auth - Recuperación de Contraseña]

#### [NEW] [ForgotPasswordState.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/presentation/ForgotPasswordState.kt)
- Definir el estado de la UI (email, nueva contraseña, cargando, error, éxito).

#### [NEW] [ForgotPasswordViewModel.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/presentation/ForgotPasswordViewModel.kt)
- Lógica para enviar el correo de recuperación y actualizar la contraseña.

#### [NEW] [ForgotPasswordScreen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/presentation/ForgotPasswordScreen.kt)
- Pantalla adaptativa con campos para email y cambio de contraseña, manteniendo el estilo industrial.

#### [MODIFY] [LoginScreen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/presentation/LoginScreen.kt)
- Agregar un botón "¿Olvidaste tu contraseña?" que navegue a la nueva pantalla.

#### [MODIFY] [di.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/di.kt)
- Registrar `ForgotPasswordViewModel` en el módulo de Koin.

## Verification Plan

### Manual Verification
- Verificar que el botón en `LoginScreen` navega correctamente.
- Probar el envío de correo de recuperación (requiere configuración de SMTP en el proyecto de Supabase).
- Verificar que la UI se adapta correctamente a temas claro/oscuro y a diferentes tamaños de pantalla (Desktop/Mobile).
