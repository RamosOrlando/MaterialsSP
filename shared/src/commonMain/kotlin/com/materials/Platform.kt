package com.materials

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform