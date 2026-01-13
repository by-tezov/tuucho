package com.tezov.tuucho.core.presentation.ui.render.projector

import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.render.misc.IdProcessor
import com.tezov.tuucho.core.presentation.ui.render.misc.ResolveStatusProcessor
import com.tezov.tuucho.core.presentation.ui.render.protocol.ContextualUpdaterProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasContextualUpdaterProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.IdProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusInvalidateInvokerProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusInvalidateInvokerSetterProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusInvalidateProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.ResolveStatusProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.ComponentProcessorProjectorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.ProcessorProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@TuuchoUiDsl
interface ComponentProjectorProtocols :
    IdProcessorProtocol,
    ResolveStatusProcessorProtocol,
    ComponentProcessorProjectorProtocol,
    ReadyStatusInvalidateProtocols {
    operator fun <T : ProcessorProjectorProtocol> T.unaryPlus() = this.also { add(it) }
}

private class ComponentProjector(
    private val idProcessor: IdProcessorProtocol,
    private val statusProcessor: ResolveStatusProcessorProtocol
) : ComponentProjectorProtocols,
    IdProcessorProtocol by idProcessor,
    ResolveStatusProcessorProtocol by statusProcessor {
    private val projectors: MutableList<ProcessorProjectorProtocol> = mutableListOf()

    override var readyStatusInvalidateInvoker: ReadyStatusInvalidateInvokerProtocol? = null
        private set

    override val contextualUpdater: List<ContextualUpdaterProcessorProtocol>
        get() = buildList {
            projectors.forEach {
                if (it is HasContextualUpdaterProtocol) {
                    addAll(it.contextualUpdater)
                }
            }
        }

    override fun add(
        projector: ProcessorProjectorProtocol
    ) {
        projectors.add(projector)
        (projector as? ReadyStatusInvalidateInvokerSetterProtocol)?.let { status ->
            status.setReadyStatusInvalidateInvoker(value = { readyStatusInvalidateInvoker?.invalidateReadyStatus() })
        }
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (jsonElement !is JsonObject) return
        idProcessor.process(jsonElement)
        projectors.forEach { projector ->
            jsonElement[projector.type]
                ?.let { childJsonElement -> projector.process(childJsonElement) }
        }
        statusProcessor.update(jsonElement)
    }

    override fun setReadyStatusInvalidateInvoker(
        value: ReadyStatusInvalidateInvokerProtocol
    ) {
        readyStatusInvalidateInvoker = value
    }

    override fun invalidateReadyStatus() {
        readyStatusInvalidateInvoker?.invalidateReadyStatus()
    }
}

private class ContextualComponentProjector(
    private val delegate: ComponentProjectorProtocols
) : ComponentProjectorProtocols by delegate,
    ContextualUpdaterProcessorProtocol {
    override val type = TypeSchema.Value.component

    override val contextualUpdater: List<ContextualUpdaterProcessorProtocol>
        get() = buildList {
            add(this@ContextualComponentProjector)
            addAll(delegate.contextualUpdater)
        }
}

fun componentProjector(
    block: ComponentProjectorProtocols.() -> Unit
): ComponentProjectorProtocols = ComponentProjector(
    idProcessor = IdProcessor(),
    statusProcessor = ResolveStatusProcessor()
).also {
    it.block()
}

val ComponentProjectorProtocols.contextual
    get(): ComponentProjectorProtocols = ContextualComponentProjector(
        delegate = this,
    )
