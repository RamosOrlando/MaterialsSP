package com.materials.features.auth.data.repository

actual suspend fun checkFreshInstallAndClearSessionIfNeeded(signOutAction: suspend () -> Unit) {
    // iOS uninstallation clears all app data automatically.
}
