# Especificación Técnica: Pantalla de Materiales

Esta pantalla permite la gestión detallada del catálogo de materiales industriales, integrando datos de fabricantes (Makers), proveedores y un historial de precios.

## 1. Modelo de Datos del Material
El objeto `Material` se rige por las siguientes propiedades:
- `materialId` (PK): Identificador único estructurado.
- `name`: Nombre descriptivo (ej: "Tubo PVC").
- `unit`: Unidad de medida (ej: "Barra", "Unidad").
- `makerId`: Referencia al fabricante.
- `sectionId`: Referencia a la sección del catálogo.
- `price`: Último precio registrado (sincronizado vía Trigger).
- `quoteDate`: Fecha de la última cotización (DD/MM/YYYY).
- `specId`: (Opcional) ID de especificación técnica.
- `historyId`: (Opcional) ID del registro actual en PriceHistory.
- `providerId`: (Opcional) ID del proveedor que dio el precio.

### Convenciones de Nomenclatura (Nombre)
Aunque no afectan la lógica del código, se siguen las siguientes convenciones en el campo `name`:
- **Paréntesis `()`**: Se utilizan para incluir **datos adicionales** o aclaraciones sobre el material que no forman parte de su nombre principal.
- **Corchetes `[]`**: Se utilizan para hacer **referencia obligatoria a la columna `unit`**. Indican una especificación de magnitud o peso que complementa la unidad.
    - *Ejemplo:* Si `name` contiene `Cemento Gris [50 Kg]` y `unit` es `Bolsa`, se interpreta que la bolsa es de 50 Kg.

---

## 2. Lógica del `materialId`
El ID se genera automáticamente para garantizar la integridad y el orden del catálogo:
**Formato:** `{sectionId}-{correlativo}-{makerId}`

*   **sectionId**: Heredado de la sección actual (ej: `01-01`).
*   **correlativo**: Un número de 3 dígitos (ej: `001`) que identifica el tipo de material dentro de la sección. Se calcula contando los prefijos únicos existentes.
*   **makerId**: Introducido manualmente por el usuario.

**Ejemplo final:** `01-01-005-AMANCO`

---

## 3. Creación de Materiales
El proceso se realiza mediante un botón flotante (FAB) y un diálogo validado.

### Reglas de Negocio en Creación:
- **Validación de Texto:** El `nombre` y la `unidad` no pueden estar vacíos y se les aplica un `.trim()` para eliminar espacios accidentales.
- **Validación de Fabricante:** El `makerId` ingresado debe existir previamente en la tabla `Maker`.
- **Estado Inicial:** Al crear un material, los campos de precio e historial (`price`, `quoteDate`, `specId`, `historyId`, `providerId`) se inicializan como **NULL**.
- **Cálculo de Correlativo:** El sistema busca el último número de "slot" ocupado en la sección y sugiere el siguiente (`n + 1`).

---

## 4. Gestión de Precios
La actualización de precios afecta a dos tablas simultáneamente:
1.  **PriceHistory**: Se crea una nueva fila con el precio, fecha (formato `DD/MM/YYYY`), proveedor y usuario que realiza el cambio.
2.  **Material**: La base de datos (Supabase) mediante un trigger (`set_material_current_price`) actualiza automáticamente los campos de precio en la tabla principal basándose en la entrada más reciente del historial.

### Formato de Fecha
Para compatibilidad con los triggers de base de datos SQL:
- La aplicación envía y recibe fechas en formato string: **`DD/MM/YYYY`**.

---

## 5. Interfaz de Usuario (UI)
- **FAB de Creación:** Visible solo para roles con permisos de edición y cuando no hay selección múltiple activa.
- **Alertas (Snackbar):** Los errores de red o validaciones del servidor se muestran mediante notificaciones temporales en la parte inferior.
- **Búsqueda:** Filtra por nombre de material, fabricante o proveedor en tiempo real.
