# Implementation Plan - Make `makerId` non-nullable in `MaterialEntity`

This plan details the changes required to change the nullability of `makerId` from `String?` to `String` in the `MaterialEntity` class and propagate this change throughout the project, including domain models and repositories.

## User Review Required

> [!IMPORTANT]
> This change assumes that every `Material` must have an associated `Maker`. The database schema (Supabase) already enforces this (`NOT NULL`), but the local Room entity and domain model were previously nullable.
> Changing this might cause issues if there are existing records with null `makerId` in the local database or if the UI allows creating materials without selecting a maker.

## Proposed Changes

### Data Layer

#### [MODIFY] [MaterialEntity.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/material/data/local/MaterialEntity.kt)
- Change `makerId: String?` to `makerId: String`.
- Update the `ForeignKey` for `MakerEntity` to use `onDelete = ForeignKey.RESTRICT` or `CASCADE` instead of `SET_NULL`, as a non-nullable column cannot be set to null. I'll use `CASCADE` to be safe if a maker is deleted, or `RESTRICT` if we want to prevent deletion. Given `makerId` is mandatory now, `SET_NULL` is invalid.

### Domain Layer

#### [MODIFY] [Material.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/material/domain/model/Material.kt)
- Change `makerId: String?` to `makerId: String`.

### Repository Layer

#### [MODIFY] [MaterialRepositoryImpl.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/material/data/repository/MaterialRepositoryImpl.kt)
- Update the `getMaterialsFlow` method to remove the unnecessary Elvis operator added in the previous fix, as `materialEntity.makerId` will now be non-nullable.

## Verification Plan

### Automated Tests
- Run `:shared:compileAndroidMain` to verify there are no compilation errors in the shared module.
- Run Room schema verification if possible (though mostly manual inspection of generated code).

### Manual Verification
- Verify that materials can still be fetched and displayed.
- Verify that creating/updating a material works correctly with the new non-nullable field.
