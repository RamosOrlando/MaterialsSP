# Implementation Plan - Fix Provider List and UI

Fix the issue where providers from Supabase are not appearing and enhance the Provider screen UI.

## User Review Required
> [!IMPORTANT]
> The most likely reason the list is empty is **Row Level Security (RLS)** in Supabase. Please run the following SQL in your Supabase SQL Editor:
> ```sql
> -- Permitir lectura de proveedores para todos (anon y autenticados)
> CREATE POLICY "Permitir lectura de proveedores" ON public.provider FOR SELECT USING (true);
> ```

## Proposed Changes

### Presentation Layer
#### [MODIFY] [ProviderScreen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/presentation/ProviderScreen.kt)
- **UI Redesign**:
    - Replace the `Box` in `ProviderCard` with a `OutlinedCard` or `ElevatedCard`.
    - Add proper padding and icons for address and city.
    - Improve the visual hierarchy of the provider name and location.
    - Use `LazyColumn` or improve the `LazyVerticalGrid` configuration for better readability.

### Data Layer (Verification)
#### [MODIFY] [SupabaseProviderDataSource.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/provider/data/remote/SupabaseProviderDataSource.kt)
- Ensure the table name string exactly matches the Supabase definition (currently using `"provider"`).

## Verification Plan

### Automated Tests
- Build the project: `./gradlew :shared:compileAndroidMain`

### Manual Verification
1. Apply the SQL policy in Supabase.
2. Run the app and navigate to the Providers screen.
3. Verify that the list now populates and the cards have a modern, professional look.
