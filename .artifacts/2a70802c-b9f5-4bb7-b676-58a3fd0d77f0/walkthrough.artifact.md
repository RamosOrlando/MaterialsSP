# Sistema de Temas Adaptativo Completado

He migrado toda la aplicación a un sistema de temas dinámico que detecta y se adapta automáticamente al modo **Claro** u **Oscuro** de tu dispositivo (Android, iOS y Desktop).

## Cambios Principales

### 1. Refuerzo del Core del Tema
#### [IndustrialTheme.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/presentation/theme/IndustrialTheme.kt)
He ampliado la paleta de colores para incluir tokens semánticos completos. El modo oscuro ahora utiliza una paleta de grises carbón y acero que mantiene la legibilidad industrial.

### 2. Migración Global de Pantallas
He actualizado todas las pantallas del proyecto para eliminar colores fijos y usar tokens de `MaterialTheme.colorScheme`. Esto incluye:

- **Navegación:** `MainScreen.kt` ahora adapta las barras de navegación, menús y rieles laterales.
- **Catálogo:** `CategoryScreen.kt`, `SectionScreen.kt` y `MaterialScreen.kt` adaptan sus fondos, tarjetas y estados de carga.
- **Gestión:** `MakerScreen.kt`, `ProviderScreen.kt` y `PriceHistoryScreen.kt` ahora son 100% legibles en modo oscuro.
- **Selección:** `MaterialsSelectedScreen.kt` adapta sus campos de edición de cantidad y resúmenes de precios.

### 3. Buenas Prácticas Aplicadas
- **Tokens Semánticos:** Se reemplazaron constantes como `IndustrialBackground` por `MaterialTheme.colorScheme.background`.
- **Contraste Dinámico:** Los textos ahora usan `onSurface` y `onSurfaceVariant`, garantizando contraste sin importar el fondo.
- **Limpieza de Código:** Se eliminaron imports de colores que ya no se utilizan en las vistas.

## Cómo Probarlo

### En Desktop (macOS/Windows)
1. Ejecuta la aplicación.
2. Cambia el tema de tu sistema operativo en la configuración de apariencia.
3. Observa cómo la aplicación cambia instantáneamente entre el modo industrial claro y el modo oscuro profesional.

### En Android/iOS
- Al cambiar el modo del sistema en los ajustes rápidos, la aplicación se actualizará automáticamente sin necesidad de reiniciar.

> [!TIP]
> Esta arquitectura facilita futuras actualizaciones de diseño, ya que cualquier cambio en `IndustrialTheme.kt` se reflejará instantáneamente en toda la aplicación.
