# Walkthrough - Provider Creation Functionality

Se ha implementado la funcionalidad para crear nuevos proveedores directamente desde la pantalla de Proveedores, integrando validaciones de duplicados, IDs correlativos y teclados especializados.

## Cambios Realizados

### Capa de Dominio y Datos

- **[ProviderRepository.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/domain/repository/ProviderRepository.kt)**: Se añadió el método `saveProvider`.
- **[ProviderRemoteDataSource.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/data/remote/ProviderRemoteDataSource.kt)**: Se añadió `saveProvider`.
- **[SupabaseProviderDataSource.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/data/remote/SupabaseProviderDataSource.kt)**: Se implementó `saveProvider` usando la operación `upsert` de Supabase.
- **[ProviderRepositoryImpl.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/data/repository/ProviderRepositoryImpl.kt)**: Se implementó la lógica para guardar tanto en remoto como en la base de datos local.
- **[SaveProviderUseCase.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/domain/use_case/SaveProviderUseCase.kt)**: Nuevo caso de uso para encapsular la creación de proveedores.
- **[di.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/di.kt)**: Se registró el nuevo caso de uso en Koin.

### Capa de Presentación

- **[ProviderViewModel.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/presentation/ProviderViewModel.kt)**:
    - Se añadió `CreateProviderUiState` para gestionar el estado del diálogo.
    - Se implementó la validación de duplicidad basada en la combinación de **Nombre y Ciudad** (insensible a mayúsculas).
    - Se configuró la ciudad por defecto como **"Oruro"**.
    - Cálculo dinámico del ID correlativo (máximo ID + 1).
- **[ProviderScreen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/presentation/ProviderScreen.kt)**:
    - Se añadió un **Floating Action Button (FAB)** con icono de "+".
    - Se implementó `AddProviderDialog` con scroll interno y teclados optimizados:
        - **Teléfono**: Teclado numérico.
        - **Email**: Teclado de correo electrónico.
    - El ID se muestra como solo lectura y deshabilitado.

## Verificación de Resultados

### Verificación de Código
- Se ha confirmado con `analyze_file` que la estructura de llaves y funciones es correcta tras el refactor.
- La validación `trim()` se aplica a todos los campos antes de guardar.
- El diálogo gestiona correctamente los estados de carga y error.
