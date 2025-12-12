package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.DpProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.FloatProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.SpProjection
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.TypeProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@TuuchoUiDsl
class DimensionTypeProjectable : ProjectableProtocol {

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
            SpProjection.Static::class -> SpProjection.Static(key)
            SpProjection.Mutable::class -> SpProjection.Mutable(key)
            DpProjection.Static::class -> DpProjection.Static(key)
            DpProjection.Mutable::class -> DpProjection.Mutable(key)
            FloatProjection.Static::class -> FloatProjection.Static(key)
            FloatProjection.Mutable::class -> FloatProjection.Mutable(key)
            else -> throw UiException.Default("not implemented")
        } as T).also { projections[it.key] = it }
}

fun TypeProjectorProtocol.dimension(
    block: DimensionTypeProjectable.() -> Unit
): DimensionTypeProjectable = DimensionTypeProjectable().also {
    add(it)
    it.block()
}

inline fun <reified T : ProjectionProtocol> DimensionTypeProjectable.projection(
    key: String
) = newProjection(klass = T::class, key = key)
