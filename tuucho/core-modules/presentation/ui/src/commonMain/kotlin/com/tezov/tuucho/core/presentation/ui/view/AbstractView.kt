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
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrue
import com.tezov.tuucho.core.presentation.ui.composable.shimmerComposable
import com.tezov.tuucho.core.presentation.ui.render.projector.ComponentProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol
import kotlinx.serialization.json.JsonObject
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
abstract class AbstractView(
    protected var screenContext: ScreenContextProtocol
) : ViewProtocol {
    lateinit var componentProjector: ComponentProjectorProtocols

    override val contextualUpdater get() = componentProjector.contextualUpdater

    private var isReady by mutableStateOf(false)
    private var readyStatusInvalidated: AtomicBoolean = AtomicBoolean(true)

    abstract fun getResolvedStatus(): Boolean

    override suspend fun initialize(
        componentObject: JsonObject
    ) {
        componentProjector = createComponentProjector().apply {
            process(componentObject)
            setReadyStatusInvalidateInvoker(value = { readyStatusInvalidated.store(true) })
        }
        updateIfNeeded()
    }

    abstract suspend fun createComponentProjector(): ComponentProjectorProtocols

    override fun updateIfNeeded() {
        if (readyStatusInvalidated.compareAndSet(expectedValue = true, newValue = false)) {
            isReady = componentProjector.hasBeenResolved.isTrue && getResolvedStatus()
        }
    }

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
