package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.BooleanProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.DpProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.FloatProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.SpProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.StringProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.createBooleanProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.createDpProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.createFloatProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.createSpProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.createStringProjection
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasReadyStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasUpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.defaultStatus
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

interface DimensionProjectableProtocols : ProjectableProtocol, HasUpdatableProtocol, HasReadyStatusProtocol {
    fun <I, T : ProjectionProtocols<I>> newProjection(
        klass: KClass<out T>,
        key: String,
        mutable: Boolean,
        contextual: Boolean
    ): T
}

@TuuchoUiDsl
class DimensionProjectable : DimensionProjectableProtocols {
    private val projections = mutableMapOf<String, ProjectionProtocols<*>>()

    override val keys get() = projections.keys

    override var isReady = defaultStatus
        private set

    override lateinit var onStatusChanged: () -> Unit

    override val updatables: List<UpdatableProtocol>
        get() = buildList {
            projections.values.forEach {
                if(it is UpdatableProtocol) {
                    add(it)
                }
            }
        }

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
        mutable: Boolean,
        contextual: Boolean
    ) = (when (klass) {
        SpProjectionProtocol::class -> createSpProjection(key, mutable, contextual)
        DpProjectionProtocol::class -> createDpProjection(key, mutable, contextual)
        FloatProjectionProtocol::class -> createFloatProjection(key, mutable, contextual)
        BooleanProjectionProtocol::class -> createBooleanProjection(key, mutable, contextual)
        StringProjectionProtocol::class -> createStringProjection(key, mutable, contextual)
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

fun TypeProjectorProtocols.dimension(
    block: DimensionProjectableProtocols.() -> Unit
): DimensionProjectableProtocols = DimensionProjectable().also {
    add(it)
    it.block()
}

inline fun <I, reified T : ProjectionProtocols<I>> DimensionProjectableProtocols.projection(
    key: String,
    mutable: Boolean = false,
    contextual: Boolean = false
) = newProjection(klass = T::class, key = key, mutable = mutable, contextual = contextual)
