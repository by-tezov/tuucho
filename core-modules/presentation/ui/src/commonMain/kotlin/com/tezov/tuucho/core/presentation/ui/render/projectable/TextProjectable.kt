package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.TextProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.createTextProjection
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.projector.TypeProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@TuuchoUiDsl
class TextTypeProjectable : ProjectableProtocol {
    private val projections = mutableMapOf<String, ProjectionProtocols<String>>()

    override val keys get() = projections.keys

    override suspend fun process(
        jsonElement: JsonElement?,
        key: String
    ) {
        projections[key]?.process(jsonElement)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ProjectionProtocols<String>> newProjection(
        klass: KClass<out T>,
        key: String,
        mutable: Boolean,
        contextual: Boolean
    ): T = (when (klass) {
        TextProjectionProtocol::class -> createTextProjection(key, mutable, contextual)
        else -> throw UiException.Default("not implemented")
    } as T).also { projections[it.key] = it }
}

fun TypeProjectorProtocol.text(
    block: TextTypeProjectable.() -> Unit
): TextTypeProjectable = TextTypeProjectable().also {
    add(it)
    it.block()
}

inline fun <reified T : ProjectionProtocols<String>> TextTypeProjectable.projection(
    key: String,
    mutable: Boolean = false,
    contextual: Boolean = false
) = newProjection(klass = T::class, key = key, mutable = mutable, contextual = contextual)
