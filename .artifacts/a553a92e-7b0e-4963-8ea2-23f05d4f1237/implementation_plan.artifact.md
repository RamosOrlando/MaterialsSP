# Fix Premature Navigation during Forgot Password Flow

The issue occurs because Supabase's recovery OTP verification immediately signs the user in. The `NavigationViewModel` observes this authentication state change and automatically updates the `initialScreen` to `Screen.Category`, which triggers a global navigation jump in `NavigationRoot` before the user can enter their new password.

## Proposed Changes

### [Navigation]

#### [MODIFY] [NavigationViewModel.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/core/presentation/navigation/NavigationViewModel.kt)
Modify `observeAuthState` to prevent automatic navigation to the content screen (`Screen.Category`) if an initial screen has already been determined (e.g., if the user is currently on the `Login` screen). This ensures that navigation from Auth screens to Content screens is handled by the screen's own success callbacks, preventing premature jumps during multi-step auth flows like password recovery.

### [Auth]

#### [MODIFY] [LoginScreen.kt](file:///Users/orly/AndroidStudioProjects/MaterialsSP/shared/src/commonMain/kotlin/com/materials/features/auth/presentation/LoginScreen.kt)
Update the `ForgotPasswordDialog` interaction to trigger `onLoginSuccess()` when the user dismisses the dialog after a successful password reset. Since we are disabling the automatic navigation in `NavigationViewModel`, we must ensure the manual navigation happens once the recovery flow is complete.

## Verification Plan

### Manual Verification
1.  Navigate to the Login screen.
2.  Click on "¿Olvidaste tu contraseña?".
3.  Enter a valid email and click "Enviar Código".
4.  Enter the 8-digit OTP received and click "Verificar Código".
5.  **Verify**: The app should stay on the "Recuperar Contraseña" dialog, showing the fields for "Nueva Contraseña" and "Confirmar Contraseña", instead of jumping to the Main/Category screen.
6.  Enter a new password and confirm it.
7.  Click "Cambiar Contraseña".
8.  **Verify**: The dialog shows a success message.
9.  Click "Cerrar".
10. **Verify**: The app navigates to the `Category` screen (MainScreen).
