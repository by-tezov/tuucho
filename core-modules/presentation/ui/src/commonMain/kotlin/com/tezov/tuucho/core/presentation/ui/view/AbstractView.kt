package com.tezov.tuucho.core.presentation.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    private var isReady = false
    private var requestUpdate: AtomicBoolean = AtomicBoolean(true)
    private val redrawTrigger = mutableIntStateOf(0)

    abstract fun getResolvedStatus(): Boolean

    override suspend fun initialize(
        componentObject: JsonObject
    ) {
        componentProjector = createComponentProjector().apply {
            process(componentObject)
            setRequestViewUpdater(value = { requestUpdate.store(true) })
        }
    }

    abstract suspend fun createComponentProjector(): ComponentProjectorProtocols

    override fun updateIfNeeded() {
        if (requestUpdate.compareAndSet(expectedValue = true, newValue = false)) {
            redrawTrigger.intValue += 1
        }
    }

    @Composable
    final override fun display(
        scope: Any?
    ) {
        isReady = remember(redrawTrigger.intValue) { getResolvedStatus() }
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
