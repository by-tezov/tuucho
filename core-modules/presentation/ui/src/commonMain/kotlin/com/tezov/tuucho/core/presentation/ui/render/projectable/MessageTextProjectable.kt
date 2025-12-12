package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.TextMessageProjection
import com.tezov.tuucho.core.presentation.ui.render.protocol.MessageProjectorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@TuuchoUiDsl
class MessageTextTypeProjectable : ProjectableProtocol {

    private val projections = mutableMapOf<String, ProjectionProtocol>()

    override val keys get() = projections.keys

    override suspend fun process(
        jsonElement: JsonElement?,
        key: String
    ) {
        projections[key]?.process(jsonElement)
    }

    fun <T : ProjectionProtocol> newProjection(
        klass: KClass<out T>,
        key: String
    ): T =
        @Suppress("UNCHECKED_CAST")
        (when (klass) {
            TextMessageProjection.Static::class -> TextMessageProjection.Static(key)
            TextMessageProjection.Mutable::class -> TextMessageProjection.Mutable(key)
            else -> throw UiException.Default("not implemented")
        } as T).also { projections[it.key] = it }
}

fun MessageProjectorProtocol.text(
    block: MessageTextTypeProjectable.() -> Unit,
): MessageTextTypeProjectable = MessageTextTypeProjectable().also {
    add(it)
    it.block()
}

inline fun <reified T : ProjectionProtocol> MessageTextTypeProjectable.projection(
    key: String
) = newProjection(klass = T::class, key = key)
