package com.tezov.tuucho.core.presentation.ui.render.projector

import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.render.misc.IdProcessor
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasUpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.IdProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.RequestViewUpdateInvokerProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.RequestViewUpdateProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.RequestViewUpdateSetterProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.ComponentProcessorProjectorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.ProcessorProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

interface ComponentProjectorProtocols :
    IdProcessorProtocol,
    ComponentProcessorProjectorProtocol,
    RequestViewUpdateProtocols {
    operator fun <T : ProcessorProjectorProtocol> T.unaryPlus() = this.also { add(it) }
}

@TuuchoUiDsl
class ComponentProjector(
    private val idProcessor: IdProcessorProtocol,
) : ComponentProjectorProtocols,
    IdProcessorProtocol by idProcessor {
    private val projectors: MutableList<ProcessorProjectorProtocol> = mutableListOf()

    override var requestViewUpdateInvoker: RequestViewUpdateInvokerProtocol? = null
        private set

    override val updatables: List<UpdatableProcessorProtocol>
        get() = buildList {
            projectors.forEach {
                if (it is HasUpdatableProtocol) {
                    addAll(it.updatables)
                }
            }
        }

    override fun add(
        projector: ProcessorProjectorProtocol
    ) {
        projectors.add(projector)
        (projector as? RequestViewUpdateSetterProtocol)?.let { status ->
            status.setRequestViewUpdater(value = { requestViewUpdateInvoker?.invokeRequestViewUpdate() })
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
    }

    override fun setRequestViewUpdater(value: RequestViewUpdateInvokerProtocol) {
        requestViewUpdateInvoker = value
    }

    override fun invokeRequestViewUpdate() {
        requestViewUpdateInvoker?.invokeRequestViewUpdate()
    }
}

private class ContextualComponentProjector(
    private val delegate: ComponentProjectorProtocols
) : ComponentProjectorProtocols by delegate,
    UpdatableProcessorProtocol {
    override val type = TypeSchema.Value.component

    override val updatables: List<UpdatableProcessorProtocol>
        get() = buildList {
            add(this@ContextualComponentProjector)
            addAll(delegate.updatables)
        }
}

fun componentProjector(
    block: ComponentProjectorProtocols.() -> Unit
): ComponentProjectorProtocols = ComponentProjector(
    idProcessor = IdProcessor()
).also {
    it.block()
}

val ComponentProjectorProtocols.contextual
    get(): ComponentProjectorProtocols = ContextualComponentProjector(
        delegate = this,
    )
