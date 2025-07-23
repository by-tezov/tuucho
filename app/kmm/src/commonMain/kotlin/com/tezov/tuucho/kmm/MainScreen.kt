package com.tezov.tuucho.kmm


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol

expect fun MainScreen.getScreen(): ComposableScreenProtocol

object MainScreen {

    @Composable
    fun show() {
        MaterialTheme {
            getScreen().show(this)
        }
    }
}


