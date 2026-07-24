package com.materials.core.util

import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()
