package com.materials

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MaterialsSP",
        ) {
            App()
        }
    }
}