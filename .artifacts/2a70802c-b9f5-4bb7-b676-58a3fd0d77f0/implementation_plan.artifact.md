# Implementation Plan - New Registration Screen

Create a dedicated "Sign Up" screen to collect user details (Name, Email, Password) for registration, improving the onboarding flow.

## User Review Required
> [!IMPORTANT]
> The new registration screen will include validation for password confirmation and name. The `AuthRepository` will be updated to store the user's name in Supabase Auth metadata.

## Proposed Changes

### Auth Feature
#### [MODIFY] [AuthRepository.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/domain/repository/AuthRepository.kt)
- Update `signUpWithEmail` signature to include `name: String`.

#### [MODIFY] [AuthRepositoryImpl.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/data/repository/AuthRepositoryImpl.kt)
- Update `signUpWithEmail` to pass the name as user metadata using `data = buildJsonObject { put("full_name", name) }`.

#### [NEW] [SignUpViewModel.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/presentation/SignUpViewModel.kt)
- Create a new ViewModel to handle the registration state and logic.

#### [NEW] [SignUpScreen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/presentation/SignUpScreen.kt)
- Create a new Composable screen with fields for Name, Email, Password, and Confirm Password.

#### [MODIFY] [LoginScreen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/presentation/LoginScreen.kt)
- Update the "Regístrate" button to trigger navigation to the new screen instead of the current ViewModel action.

### Core Navigation
#### [MODIFY] [Screen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/presentation/navigation/Screen.kt)
- Add `SignUp` to the `Screen` sealed interface.

#### [MODIFY] [NavigationRoot.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/presentation/navigation/NavigationRoot.kt)
- Handle navigation to `Screen.SignUp`.

#### [MODIFY] [di.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/di.kt)
- Register `SignUpViewModel` in Koin.

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors.

### Manual Verification
1. Open the app and go to the Login screen.
2. Click on the "Regístrate" text.
3. Verify that the new Sign Up screen appears.
4. Fill in the name, email, and passwords.
5. Click "Registrarse".
6. Verify that the account is created (or an error is shown if the email exists).
7. Verify that upon success, it navigates to the main app (Category screen).
