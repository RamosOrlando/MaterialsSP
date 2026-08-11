# Implementation Plan - Make Maker non-nullable in MaterialWithPrices

Update `MaterialWithPrices` to reflect that `maker` is a mandatory field, consistent with the `Material` table constraints where `makerId` is non-nullable.

## User Review Required
> [!IMPORTANT]
> By making `maker` non-nullable in the domain model `MaterialWithPrices`, the repositories must guarantee that a `Maker` object is provided. If a maker is missing from the local database during a join operation, a fallback "Unknown" maker will be used to avoid app crashes.

## Proposed Changes

### Material Feature
#### [MODIFY] [MaterialWithPrices.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/material/domain/model/MaterialWithPrices.kt)
- Change `maker: Maker? = null` to `maker: Maker`.

#### [MODIFY] [MaterialRepositoryImpl.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/material/data/repository/MaterialRepositoryImpl.kt)
- Update `getMaterialsFlow` to provide a fallback `Maker` if the `makerId` lookup fails.

#### [MODIFY] [MaterialViewModel.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/material/presentation/MaterialViewModel.kt)
- Update `MaterialItem` to have a non-nullable `makerName: String`.
- Provide fallback name "Desconocido" in the mapping logic.

### Price History Feature
#### [MODIFY] [PriceHistoryScreen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/price_history/presentation/PriceHistoryScreen.kt)
- Update previews to comply with the non-nullable `maker`.

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors: `./gradlew :shared:compileAndroidMain`

### Manual Verification
1. Run the app and navigate to the Materials list.
2. Verify that the maker names are displayed correctly.
3. Navigate to Price History and verify the maker information is shown as before.
