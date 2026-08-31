# Solución al Bucle de Cierre de Sesión y Navegación Reactiva

El problema de "volver a la pantalla anterior" al cerrar sesión se debe a dos factores principales:
1. **Reutilización del ViewModel**: `LoginViewModel` mantiene su estado `isSuccess = true` de la sesión anterior, lo que provoca que al mostrarse de nuevo dispare inmediatamente la navegación hacia atrás.
2. **Navegación No Reactiva**: El `backstack` y el estado del usuario en los ViewModels no están sincronizados automáticamente con el estado de la sesión de Supabase.

## Cambios Propuestos

### 1. Autenticación Reactiva
Restauraremos el flujo reactivo para que toda la aplicación sepa cuándo el usuario cambia, sin necesidad de reinicios manuales o limpiezas de backstack propensas a errores.

#### [MODIFY] [AuthRepository](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/domain/repository/AuthRepository.kt)
- Reintroducir `getUserIdFlow(): Flow<String?>`.

#### [MODIFY] [AuthRepositoryImpl](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/data/repository/AuthRepositoryImpl.kt)
- Implementar `getUserIdFlow()` observando `sessionStatus` de Supabase.

### 2. Reinicio de Estado de Login
Aseguraremos que el formulario de inicio de sesión siempre comience desde cero.

#### [MODIFY] [LoginViewModel](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/presentation/LoginViewModel.kt)
- Añadir función `resetState()` para limpiar errores y estados de éxito.

#### [MODIFY] [LoginScreen](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/presentation/LoginScreen.kt)
- Usar `LaunchedEffect(Unit)` para llamar a `resetState()` al entrar en la pantalla.

### 3. Sincronización Global de Navegación

#### [MODIFY] [NavigationViewModel](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/presentation/navigation/NavigationViewModel.kt)
- Reintroducir la observación del estado de autenticación para que `initialScreen` y `userRole` se actualicen solos al hacer login/logout.

#### [MODIFY] [NavigationRoot](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/presentation/navigation/NavigationRoot.kt)
- Usar `remember(initialScreen)` para que el `backstack` se reinicie completamente cuando el estado de autenticación cambie en el ViewModel. Esto garantiza que no queden rastros de la sesión anterior.

## Plan de Verificación

### Pruebas Manuales
1. Iniciar sesión -> Debe mostrar `MainScreen` con datos correctos.
2. Cerrar sesión -> Debe ir a `LoginScreen` y mantenerse ahí (sin bucles).
3. Iniciar sesión con un usuario diferente -> Debe actualizar inmediatamente el encabezado sin reiniciar la app.
