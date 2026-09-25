package com.materials.features.auth.data.repository

expect suspend fun checkFreshInstallAndClearSessionIfNeeded(signOutAction: suspend () -> Unit)
