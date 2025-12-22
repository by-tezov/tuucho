package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.BooleanProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.BooleanProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.DpProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.DpProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.FloatProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.FloatProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.SpProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.SpProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.StringProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.StringProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.createBooleanProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.createDpProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.createFloatProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.createSpProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.createStringProjection
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.TypeProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@TuuchoUiDsl
class DimensionTypeProjectable : ProjectableProtocol {
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
        mutable: Boolean,
        contextual: Boolean
    ) = (when (klass) {
        SpProjection::class, SpProjectionProtocol::class -> createSpProjection(key, mutable, contextual)
        DpProjection::class, DpProjectionProtocol::class -> createDpProjection(key, mutable, contextual)
        FloatProjection::class, FloatProjectionProtocol::class -> createFloatProjection(key, mutable, contextual)
        BooleanProjection::class, BooleanProjectionProtocol::class -> createBooleanProjection(key, mutable, contextual)
        StringProjection::class, StringProjectionProtocol::class -> createStringProjection(key, mutable, contextual)
        else -> throw UiException.Default("not implemented")
    } as T).also { projections[it.key] = it }
}

fun TypeProjectorProtocol.dimension(
    block: DimensionTypeProjectable.() -> Unit
): DimensionTypeProjectable = DimensionTypeProjectable().also {
    add(it)
    it.block()
}

inline fun <I, reified T : ProjectionProtocols<I>> DimensionTypeProjectable.projection(
    key: String,
    mutable: Boolean = false,
    contextual: Boolean = false
) = newProjection(klass = T::class, key = key, mutable = mutable, contextual = contextual)
