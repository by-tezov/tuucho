package com.tezov.tuucho.core.presentation.ui.render.projectable

import androidx.compose.ui.graphics.Color
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.ColorProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.ColorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.createColorProjection
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.TypeProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@TuuchoUiDsl
class ColorTypeProjectable : ProjectableProtocol {
    private val projections = mutableMapOf<String, ProjectionProtocols<Color>>()

    override val keys get() = projections.keys

    override suspend fun process(
        jsonElement: JsonElement?,
        key: String
    ) {
        projections[key]?.process(jsonElement)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ProjectionProtocols<Color>> newProjection(
        klass: KClass<out T>,
        key: String,
        mutable: Boolean,
        contextual: Boolean
    ) = (when (klass) {
        ColorProjection::class, ColorProjectionProtocol::class -> createColorProjection(key, mutable, contextual)
        else -> throw UiException.Default("not implemented")
    } as T).also { projections[it.key] = it }
}

fun TypeProjectorProtocol.color(
    block: ColorTypeProjectable.() -> Unit
): ColorTypeProjectable = ColorTypeProjectable().also {
    add(it)
    it.block()
}

inline fun <reified T : ProjectionProtocols<Color>> ColorTypeProjectable.projection(
    key: String,
    mutable: Boolean = false,
    contextual: Boolean = false
) = newProjection(klass = T::class, key = key, mutable = mutable, contextual = contextual)
