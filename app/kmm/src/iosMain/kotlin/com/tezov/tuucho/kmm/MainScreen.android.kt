package com.tezov.tuucho.kmm

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol

actual fun MainScreen.getScreen(): ComposableScreenProtocol = object : ComposableScreenProtocol() {

    @Composable
    override fun show(scope: Any?) {
//        ComposeUIViewController { App() }
        //TODO
    }

}
