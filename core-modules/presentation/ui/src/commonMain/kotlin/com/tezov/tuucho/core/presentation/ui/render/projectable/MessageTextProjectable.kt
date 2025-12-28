package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.MessageTextProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.createMessageTextProjection
import com.tezov.tuucho.core.presentation.ui.render.projector.MessageProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasReadyStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.defaultStatus
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

interface MessageProjectableProtocols : ProjectableProtocol, HasReadyStatusProtocol {
    fun <I, T : ProjectionProtocols<I>> newProjection(
        klass: KClass<out T>,
        key: String,
        mutable: Boolean
    ): T
}

@TuuchoUiDsl
class MessageProjectable : MessageProjectableProtocols {
    private val projections = mutableMapOf<String, ProjectionProtocols<*>>()

    override val keys get() = projections.keys

    override var isReady = defaultStatus
        private set

    override lateinit var onStatusChanged: () -> Unit

    override suspend fun process(
        jsonElement: JsonElement?,
        key: String
    ) {
        projections[key]?.process(jsonElement)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <I, T : ProjectionProtocols<I>> newProjection(
        klass: KClass<out T>,
        key: String,
        mutable: Boolean
    ): T = (when (klass) {
        MessageTextProjectionProtocol::class -> createMessageTextProjection(key, mutable)
        else -> throw UiException.Default("not implemented")
    } as T).also {
        projections[it.key] = it
        (it as? HasReadyStatusProtocol)?.let { status ->
            status.onStatusChanged = {
                val previous = isReady
                isReady = isReady && status.isReady
                if (previous != isReady && this::onStatusChanged.isInitialized) {
                    onStatusChanged.invoke()
                }
            }
        }
    }
}

fun MessageProjectorProtocols.text(
    block: MessageProjectableProtocols.() -> Unit,
): MessageProjectableProtocols = MessageProjectable().also {
    add(it)
    it.block()
}

inline fun <I, reified T : ProjectionProtocols<I>> MessageProjectableProtocols.projection(
    key: String,
    mutable: Boolean = false
) = newProjection(klass = T::class, key = key, mutable = mutable)
