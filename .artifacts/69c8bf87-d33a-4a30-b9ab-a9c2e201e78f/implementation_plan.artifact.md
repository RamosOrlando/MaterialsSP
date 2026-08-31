# Plan: Add Provider Creation Functionality

El objetivo es añadir un Botón de Acción Flotante (FAB) en la pantalla de Proveedores para permitir la creación de nuevos proveedores, con validaciones de nombre y ciudad únicos e IDs correlativos.

## Cambios Propuestos

### Módulo de Proveedores (Provider)

#### [MODIFY] [ProviderRepository.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/domain/repository/ProviderRepository.kt)
- Añadir `suspend fun saveProvider(provider: Provider): Resource<Unit>`.

#### [MODIFY] [ProviderRemoteDataSource.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/data/remote/ProviderRemoteDataSource.kt)
- Añadir `suspend fun saveProvider(provider: Provider)`.

#### [MODIFY] [SupabaseProviderDataSource.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/data/remote/SupabaseProviderDataSource.kt)
- Implementar `saveProvider` usando `upsert` en Supabase.

#### [MODIFY] [ProviderRepositoryImpl.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/data/repository/ProviderRepositoryImpl.kt)
- Implementar `saveProvider` para guardar en remoto y localmente.

#### [NEW] [SaveProviderUseCase.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/domain/use_case/SaveProviderUseCase.kt)
- Crear el caso de uso para guardar proveedores.

#### [MODIFY] [di.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/di.kt)
- Registrar `SaveProviderUseCase` en Koin.

#### [MODIFY] [ProviderViewModel.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/presentation/ProviderViewModel.kt)
- Añadir `CreateProviderUiState` y eventos para gestionar la creación.
- Implementar lógica de validación (nombre + ciudad único, trim de campos).
- ID correlativo automático.
- Valor por defecto "Oruro" para la ciudad.

#### [MODIFY] [ProviderScreen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/presentation/ProviderScreen.kt)
- Añadir `Scaffold` con FAB.
- Implementar `AddProviderDialog` con teclados específicos para teléfono y email.

## Verificación Planificada

### Verificación Manual
1. Abrir la pantalla de Proveedores.
2. Presionar el FAB (+).
3. Verificar que la ciudad por defecto es "Oruro".
4. Intentar crear un proveedor que ya existe en la misma ciudad (insensible a mayúsculas).
5. Crear un proveedor nuevo y verificar que se guarda y aparece en la lista con un ID correlativo correcto.
6. Probar que los teclados de teléfono y email sean los correctos.
