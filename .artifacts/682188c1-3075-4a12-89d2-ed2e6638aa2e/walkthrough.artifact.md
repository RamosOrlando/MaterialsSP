# Walkthrough - Provider Management in Individual Edit

I have enhanced the individual edit dialog to intelligently handle provider information, allowing manual entry when the provider is unknown.

## Changes Made

### 1. Dynamic Provider UI (Dialog)
Modified `EditMaterialDialog` in [MaterialScreen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/material/presentation/MaterialScreen.kt):
- **Provider Label**: If the material already has a known provider (i.e., `providerName` is not null), it displays "Proveedor: [Nombre]" in the read-only section.
- **Provider ID TextField**: If the provider is unknown (`providerName` is null), a new `OutlinedTextField` appears specifically for the **Provider ID**. This allows users to link the material to a provider manually during a price update.

### 2. State & Callback Enhancements
- Updated `MaterialScreenContent` to pass the full `MaterialItem` (which includes the `providerName`) to the `onEditMaterial` callback.
- `MaterialScreen` now tracks `editingProviderName` to ensure the dialog receives the correct context.

### 3. Data Integrity
- The `providerId` is now correctly captured and trimmed before saving.
- The `onConfirm` logic ensures that manual `providerId` entries are persisted to both the `Material` and `PriceHistory` records.

## Verification Results

### Automated Tests
- Successfully compiled the shared module: `./gradlew :shared:compileAndroidMain`

### Manual Verification Path
1. Open the Materials screen.
2. Click the edit icon on a card with a visible provider name.
    - Confirm you see the provider's name in the labels and NO input field for it.
3. Click the edit icon on a card with "---" as the provider.
    - Confirm that a TextField for "ID Proveedor" appears.
4. Input a new ID and price, then save.
5. Re-open to confirm the ID was persisted.
