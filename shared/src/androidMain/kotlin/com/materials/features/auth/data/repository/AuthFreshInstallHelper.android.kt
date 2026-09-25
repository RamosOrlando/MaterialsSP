package com.materials.features.auth.data.repository

actual suspend fun checkFreshInstallAndClearSessionIfNeeded(signOutAction: suspend () -> Unit) {
    // Android uninstallation clears all app data automatically.
}
