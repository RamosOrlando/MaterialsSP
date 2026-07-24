package com.materials.core.util

import java.util.UUID

actual fun randomUUID(): String = UUID.randomUUID().toString()
