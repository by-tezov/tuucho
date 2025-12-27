package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.MessageTextProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.createMessageTextProjection
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.MessageProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@TuuchoUiDsl
class MessageProjectable : ProjectableProtocol {
    private val projections = mutableMapOf<String, ProjectionProtocols<*>>()

    override val keys get() = projections.keys

    override suspend fun process(
        jsonElement: JsonElement?,
        key: String
    ) {
        projections[key]?.process(jsonElement)
    }

    @Suppress("UNCHECKED_CAST")
    fun <I, T : ProjectionProtocols<I>> newProjection(
        klass: KClass<out T>,
        key: String,
        mutable: Boolean
    ): T = (when (klass) {
        MessageTextProjectionProtocol::class -> createMessageTextProjection(key, mutable)
        else -> throw UiException.Default("not implemented")
    } as T).also { projections[it.key] = it }
}

fun MessageProjectorProtocol.text(
    block: MessageProjectable.() -> Unit,
): MessageProjectable = MessageProjectable().also {
    add(it)
    it.block()
}

inline fun <I, reified T : ProjectionProtocols<I>> MessageProjectable.projection(
    key: String,
    mutable: Boolean = false
) = newProjection(klass = T::class, key = key, mutable = mutable)
