package com.tezov.tuucho.core.presentation.ui.render.projector

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui._system.subsetOrNull
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.protocol.ContextualUpdaterProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasContextualUpdaterProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasIdProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusInvalidateInvokerProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusInvalidateProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.MessageProcessorProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@TuuchoUiDsl
interface MessageProjectorProtocols :
    MessageProcessorProjectorProtocol,
    ReadyStatusInvalidateProtocols,
    HasIdProtocol {
    val lazyId: Lazy<String?>

    operator fun <T : ProjectionProcessorProtocol> T.unaryPlus() = this.also { add(it) }
}

private class MessageProjector(
    override val lazyId: Lazy<String?>,
    override val subset: String,
    private var onReceived: () -> Unit
) : MessageProjectorProtocols,
    TuuchoKoinComponent {
    private val projections = mutableMapOf<String, ProjectionProcessorProtocol>()

    override val id get() = lazyId.value

    override val type = TypeSchema.Value.message

    override var readyStatusInvalidateInvoker: ReadyStatusInvalidateInvokerProtocol? = null
        private set

    override fun add(
        projection: ProjectionProcessorProtocol
    ) {
        if (projections.contains(projection.key)) {
            throw UiException.Default("Error, key ${projection.key} already exist for type $type")
        }
        projections[projection.key] = projection
    }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (jsonElement !is JsonObject) return
        val subset = jsonElement.subsetOrNull ?: return
        if (subset != this.subset) return
        projections.forEach { (key, projection) ->
            jsonElement[key]
                ?.let { childJsonElement ->
                    projection.process(childJsonElement)
                }
        }
        onReceived.invoke()
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

private class ContextualMessageProjector(
    private val delegate: MessageProjectorProtocols
) : MessageProjectorProtocols by delegate,
    HasContextualUpdaterProtocol,
    ContextualUpdaterProcessorProtocol {
    override val type get() = delegate.type

    override val contextualUpdater get() = listOf(this)
}

fun ComponentProjectorProtocols.message(
    subset: String,
    onReceived: () -> Unit,
    block: MessageProjectorProtocols.() -> Unit
): MessageProjectorProtocols {
    val messageProjector = MessageProjector(
        lazyId = lazy { id },
        subset = subset,
        onReceived = onReceived
    )
    return ContextualMessageProjector(messageProjector)
        .also { it.block() }
}
