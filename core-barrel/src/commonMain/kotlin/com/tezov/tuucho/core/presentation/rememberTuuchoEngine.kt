package com.tezov.tuucho.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

interface TuuchoEngineProtocol {

    suspend fun start(url: String)

    @Composable
    fun display()
}

@Composable
fun rememberTuuchoEngine(): TuuchoEngineProtocol {
    val engine = com.tezov.tuucho.core.presentation.ui.renderer.rememberTuuchoEngine()
    return remember {
        object : TuuchoEngineProtocol {

            override suspend fun start(url: String) {
                engine.start(url)
            }

            @Composable
            override fun display() {
                engine.display()
            }
        }
    }
}
