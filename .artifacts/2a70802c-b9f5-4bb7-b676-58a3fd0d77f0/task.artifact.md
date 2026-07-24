# Tasks - New Registration Screen

- [x] Domain Layer
    - [x] Update `AuthRepository.kt` to include `name` in `signUpWithEmail`
- [x] Data Layer
    - [x] Update `AuthRepositoryImpl.kt` to save name as metadata
- [x] Navigation
    - [x] Add `SignUp` screen to `Screen.kt`
    - [x] Update `NavigationRoot.kt` to handle `Screen.SignUp`
- [x] Dependency Injection
    - [x] Register `SignUpViewModel` in `di.kt`
- [x] Presentation Layer
    - [x] Create `SignUpViewModel.kt`
    - [x] Create `SignUpScreen.kt`
    - [x] Update `LoginScreen.kt` to navigate to `SignUp`
- [x] Verification
    - [x] Verify build
