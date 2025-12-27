package com.tezov.tuucho.core.presentation.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.presentation.ui.composable.shimmerComposable
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.ComponentProjectorProtocol
import com.tezov.tuucho.core.presentation.ui.screen.Screen
import com.tezov.tuucho.core.presentation.ui.view._system.ViewProtocol
import kotlinx.serialization.json.jsonObject

abstract class AbstractView(
    protected var screen: Screen,
    protected var path: JsonElementPath,
) : ViewProtocol {
    val componentObject get() = screen.componentObject.find(path).jsonObject

    lateinit var componentProjector: ComponentProjectorProtocol

    protected var isInitialized = false

    protected var isReady by mutableStateOf(false)

    protected abstract fun updateReadyStatus()

    suspend fun init() {
        componentProjector = createComponentProjectorProjection().also {
            it.process(componentObject)
        }
        updateReadyStatus()
        isInitialized = true
        screen.addView(this)
    }

    abstract suspend fun createComponentProjectorProjection(): ComponentProjectorProtocol

    override val updatables get() = componentProjector.updatables

    @Composable
    final override fun display(
        scope: Any?
    ) {
        if (isReady) {
            displayComponent(scope)
        } else {
            displayPlaceholder(scope)
        }
    }

    @Composable
    protected abstract fun displayComponent(
        scope: Any?
    )

    @Composable
    protected open fun displayPlaceholder(
        scope: Any?
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shimmerComposable(width = 1000f)
        )
    }
}
