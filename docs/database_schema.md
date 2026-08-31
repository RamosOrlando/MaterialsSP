# Esquema de Base de Datos - MaterialsSP

Este documento describe la estructura de datos, relaciones y convenciones utilizadas en el proyecto.

---

## 1. Módulo de Identidad y Autenticación (Supabase Auth)
La gestión de acceso se divide en el sistema interno de Supabase y las tablas de perfil extendidas en el esquema público.

### [System] Schema: `auth`
*   **users**: Tabla interna gestionada por Supabase Auth. Contiene `email`, `encrypted_password`, `id` (UUID), etc.

### [App] Schema: `public`
Estas tablas están vinculadas al `auth.users.id` mediante Foreign Keys.

#### Tabla: `profiles`
Extiende la información básica del usuario.
- `id` (UUID, PK): Referencia a `auth.users.id`.
- `full_name` (Text): Nombre completo.
- `role` (Enum/Text): Rol del usuario (ej: `ADMIN`, `MAKER`, `CLIENT`).
- `avatar_url` (Text, Nullable): Ruta a la imagen de perfil en Storage.

---

## 2. Módulo de Catálogo (Materiales e Industrial)
Tablas principales que conforman el núcleo de la aplicación.

#### Tabla: `Category`
- `categoryId` (Text, PK)
- `name` (Text, Unique)

#### Tabla: `Section`
- `sectionId` (Text, PK)
- `name` (Text, Unique)
- `categoryId` (Text, FK -> Category)

#### Tabla: `Maker` (Fabricante)
- `makerId` (Text, PK)
- `name` (Text)

#### Tabla: `Material`
- `materialId` (Text, PK)
- `name` (Text): Siguiendo convenciones `()` y `[]`.
- `unit` (Text)
- `makerId` (Text, FK -> Maker)
- `sectionId` (Text, FK -> Section)
- `price` (Double): Sincronizado por Trigger.
- `quoteDate` (Text): Formato `DD/MM/YYYY`.

---

## 3. Módulo de Historial y Precios

#### Tabla: `PriceHistory`
- `historyId` (Text, PK)
- `materialId` (Text, FK -> Material)
- `providerId` (Text, FK -> Provider)
- `price` (Double)
- `quoteDate` (Text)
- `username` (Text): Auditoría de quién registró el precio.

#### Tabla: `Provider`
- `providerId` (Text, PK)
- `name` (Text)
- `email`, `telephone`, `address`...

---

## 4. Módulo de Gestión de Usuarios y Suscripciones
Estructura extendida para el control de perfiles y monetización.

#### Tabla: `UserRole` (Catálogo)
- `roleId` (Smallint, PK, Auto-increment)
- `name` (Text)

#### Tabla: `UserProfession` (Catálogo)
- `professionId` (Smallint, PK, Auto-increment)
- `name` (Text, Unique)

#### Tabla: `UserPlan` (Ofertas)
- `planId` (Smallint, PK)
- `name`, `price`, `discountPrice`
- `isActive` (Boolean)
- `durationDays` (Smallint)

#### Tabla: `User` (Perfil Central)
- `userId` (Text, PK): FK a `auth.users.id`.
- `name`, `lastName`, `email`, `cellphone`
- `roleId` (FK -> UserRole)
- `professionId` (FK -> UserProfession)

#### Tabla: `SubscriptionHistory` (Transaccional)
- `subHistoryId` (Text, PK)
- `userId` (FK -> User)
- `planId` (FK -> UserPlan)
- `state` (Text): Ej: ACTIVE, EXPIRED.
- `pricePaid`, `discountAmount`

---

## Convenciones Globales
- **IDs**: Se utilizan UUIDs para el sistema de Auth y IDs estructurados (ej: `01-01-001`) para el catálogo.
- **Fechas**: Almacenadas como `Text` en formato `DD/MM/YYYY` para compatibilidad con triggers de búsqueda y ordenamiento SQL.
- **Auditoría**: Cada cambio de precio debe registrar el `username` del autor.
