package com.tezov.tuucho.core.presentation.ui.render.projector

import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.misc.IdProcessor
import com.tezov.tuucho.core.presentation.ui.render.protocol.ContextualUpdaterProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasContextualUpdaterProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.IdProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusInvalidateInvokerProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusInvalidateInvokerSetterProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusInvalidateProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.TypeProcessorProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@TuuchoUiDsl
interface TypeProjectorProtocols :
    IdProcessorProtocol,
    TypeProcessorProjectorProtocol,
    HasContextualUpdaterProtocol,
    ReadyStatusInvalidateProtocols {
    operator fun <T : ProjectionProcessorProtocol> T.unaryPlus() = this.also { add(it) }
}

private class TypeProjector(
    private val idProcessor: IdProcessorProtocol,
    override val type: String
) : TypeProjectorProtocols,
    IdProcessorProtocol by idProcessor {
    private val projections = mutableMapOf<String, ProjectionProcessorProtocol>()

    override var readyStatusInvalidateInvoker: ReadyStatusInvalidateInvokerProtocol? = null
        private set

    override val contextualUpdater: List<ContextualUpdaterProcessorProtocol>
        get() = buildList {
            projections.forEach { (_, value) ->
                if (value is ContextualUpdaterProcessorProtocol) {
                    add(value)
                }
            }
        }

    override fun add(
        projection: ProjectionProcessorProtocol
    ) {
        if (projections.contains(projection.key)) {
            throw UiException.Default("Error, key ${projection.key} already exist for type $type")
        }
        projections[projection.key] = projection
        (projection as? ReadyStatusInvalidateInvokerSetterProtocol)?.let { status ->
            status.setReadyStatusInvalidateInvoker(value = { readyStatusInvalidateInvoker?.invalidateReadyStatus() })
        }
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (jsonElement !is JsonObject) return
        idProcessor.process(jsonElement)
        projections.forEach { (key, projection) ->
            jsonElement[key]
                ?.let { childJsonElement ->
                    projection.process(childJsonElement)
                }
        }
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

private class ContextualTypeProjector(
    private val delegate: TypeProjectorProtocols
) : TypeProjectorProtocols by delegate,
    ContextualUpdaterProcessorProtocol {
    override val type get() = delegate.type

    override val contextualUpdater: List<ContextualUpdaterProcessorProtocol>
        get() = buildList {
            add(this@ContextualTypeProjector)
            addAll(delegate.contextualUpdater)
        }
}

suspend fun ComponentProjectorProtocols.option(
    block: suspend TypeProjectorProtocols.() -> Unit
): TypeProjectorProtocols = TypeProjector(
    type = TypeSchema.Value.option,
    idProcessor = IdProcessor()
).also { it.block() }

suspend fun ComponentProjectorProtocols.style(
    block: suspend TypeProjectorProtocols.() -> Unit
): TypeProjectorProtocols = TypeProjector(
    type = TypeSchema.Value.style,
    idProcessor = IdProcessor()
).also { it.block() }

suspend fun ComponentProjectorProtocols.content(
    block: suspend TypeProjectorProtocols.() -> Unit
): TypeProjectorProtocols = TypeProjector(
    type = TypeSchema.Value.content,
    idProcessor = IdProcessor()
).also { it.block() }

suspend fun ComponentProjectorProtocols.state(
    block: suspend TypeProjectorProtocols.() -> Unit
): TypeProjectorProtocols = TypeProjector(
    type = TypeSchema.Value.state,
    idProcessor = IdProcessor()
).also { it.block() }

val TypeProjectorProtocols.contextual
    get(): TypeProjectorProtocols = ContextualTypeProjector(
        delegate = this,
    )
