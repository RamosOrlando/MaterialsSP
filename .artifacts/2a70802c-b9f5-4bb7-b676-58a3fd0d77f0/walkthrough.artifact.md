# Walkthrough - New Registration Screen

I have implemented a dedicated "Sign Up" screen to collect user details (Name, Email, Password) for registration. This improves the onboarding flow and allows capturing the user's full name.

## Changes Made

### 1. Domain & Data Layer Updates
- Updated `AuthRepository` to include the user's name in the `signUpWithEmail` method.
- [AuthRepository.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/domain/repository/AuthRepository.kt)
- [AuthRepositoryImpl.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/data/repository/AuthRepositoryImpl.kt): Now saves the `full_name` as user metadata in Supabase Auth.

### 2. New Registration Logic
- Created [SignUpViewModel.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/presentation/SignUpViewModel.kt) to handle the state and logic for the new screen, including password confirmation validation.
- Registered the new ViewModel in the dependency injection module: [di.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/di.kt).

### 3. New User Interface
- Created [SignUpScreen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/presentation/SignUpScreen.kt): A clean, Industrial-themed screen with fields for Name, Email, Password, and Password Confirmation.
- Updated [LoginScreen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/presentation/LoginScreen.kt): The "Regístrate" button now navigates to the new Sign Up screen.

### 4. Navigation Integration
- Added `SignUp` to the [Screen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/presentation/navigation/Screen.kt) sealed interface.
- Updated [NavigationRoot.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/presentation/navigation/NavigationRoot.kt) to handle the navigation flow and backstack management for the new screen.

## Verification Results

### Automated Tests
- Successfully compiled the shared module for Android: `./gradlew :shared:compileAndroidMain`

### Manual Verification
1. Open the app and go to the Login screen.
2. Click on the "**¿No tienes cuenta? Regístrate**" link.
3. Observe the new **Crear Cuenta** screen.
4. Fill in all fields, ensuring passwords match.
5. Click **REGISTRARSE**; the account is created, and the app navigates to the main Categories screen.
