# Implementation Plan - Persistent Login Session

Ensure the user remains logged in after closing and reopening the app by properly waiting for session restoration from local storage.

## User Review Required
> [!IMPORTANT]
> This change ensures that the app correctly identifies an existing session during startup. The "Splash" or initial loading state might last a few milliseconds longer while Supabase reads from storage, but it prevents the app from incorrectly redirecting to the Login screen.

## Proposed Changes

### Auth Feature
#### [MODIFY] [AuthRepositoryImpl.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/data/repository/AuthRepositoryImpl.kt)
- Update `isUserLoggedIn()` to use `supabaseClient.auth.awaitInitialization()`.
- Use `supabaseClient.auth.currentSessionOrNull()` for a more complete check than just the access token.

#### [MODIFY] [SupabaseClient.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/data/remote/SupabaseClient.kt)
- Ensure `SettingsSessionManager` is correctly initialized. (Already present, but will verify imports and configuration).

### Core Navigation
#### [MODIFY] [NavigationViewModel.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/presentation/navigation/NavigationViewModel.kt)
- No functional changes needed here as it already calls `isUserLoggedIn()`, which will now correctly wait for initialization.

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors.

### Manual Verification
1. Open the app and log in with email/password.
2. Once logged in, close the app completely (kill the task).
3. Reopen the app.
4. Verify that it navigates directly to the `Category` screen (or current screen) instead of the `Login` screen.
5. Repeat the process to ensure it's consistent.
