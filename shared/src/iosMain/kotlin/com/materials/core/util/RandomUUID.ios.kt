package com.materials.core.util

import platform.Foundation.NSUUID
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

actual fun randomUUID(): String = NSUUID().UUIDString()

actual fun getCurrentDate(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "dd/MM/yyyy"
    return formatter.stringFromDate(NSDate())
}
