package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.ColorProjection
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.TypeProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@TuuchoUiDsl
class ColorTypeProjectable : ProjectableProtocol {

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
    ) =
        @Suppress("UNCHECKED_CAST")
        (when (klass) {
            ColorProjection.Static::class -> ColorProjection.Static(key)
            ColorProjection.Mutable::class -> ColorProjection.Mutable(key)
            else -> throw UiException.Default("not implemented")
        } as T).also { projections[it.key] = it }
}

fun TypeProjectorProtocol.color(
    block: ColorTypeProjectable.() -> Unit
): ColorTypeProjectable = ColorTypeProjectable().also {
    add(it)
    it.block()
}

inline fun <reified T : ProjectionProtocol> ColorTypeProjectable.projection(
    key: String,
) = newProjection(klass = T::class, key = key)
