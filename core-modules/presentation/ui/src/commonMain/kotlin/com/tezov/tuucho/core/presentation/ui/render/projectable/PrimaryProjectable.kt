package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.BooleanProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.StringProjection
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.TypeProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@TuuchoUiDsl
class PrimaryTypeProjectable : ProjectableProtocol {
    private val projections = mutableMapOf<String, ProjectionProtocol>()

    override val keys = projections.keys

    override suspend fun process(
        jsonElement: JsonElement?,
        key: String
    ) {
        projections[key]?.process(jsonElement)
    }

    fun <T : ProjectionProtocol> newProjection(
        klass: KClass<out T>,
        key: String,
    ) =
        @Suppress("UNCHECKED_CAST")
        (when (klass) {
            StringProjection.Static::class -> StringProjection.Static(key)
            StringProjection.Mutable::class -> StringProjection.Mutable(key)
            BooleanProjection.Static::class -> BooleanProjection.Static(key)
            BooleanProjection.Mutable::class -> BooleanProjection.Mutable(key)
            else -> throw UiException.Default("not implemented")
        } as T).also { projections[it.key] = it }
}

fun TypeProjectorProtocol.primaryType(
    block: PrimaryTypeProjectable.() -> Unit
): PrimaryTypeProjectable = PrimaryTypeProjectable().also {
    add(it)
    it.block()
}

inline fun <reified T : ProjectionProtocol> PrimaryTypeProjectable.projection(
    key: String
) = newProjection(klass = T::class, key = key)
