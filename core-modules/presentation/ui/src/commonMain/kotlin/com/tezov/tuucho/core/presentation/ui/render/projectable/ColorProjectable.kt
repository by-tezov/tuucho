package com.tezov.tuucho.core.presentation.ui.render.projectable

import androidx.compose.ui.graphics.Color
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.ColorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.createColorProjection
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasReadyStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasUpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.defaultStatus
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

interface ColorTypeProjectableProtocols : ProjectableProtocol, HasUpdatableProtocol, HasReadyStatusProtocol {
    fun <T : ProjectionProtocols<Color>> newProjection(
        klass: KClass<out T>,
        key: String,
        mutable: Boolean,
        contextual: Boolean
    ): T
}

@TuuchoUiDsl
class ColorProjectable : ColorTypeProjectableProtocols {
    private val projections = mutableMapOf<String, ProjectionProtocols<Color>>()

    override val keys get() = projections.keys

    override var isReady = defaultStatus
        private set

    override lateinit var onStatusChanged: () -> Unit

    override val updatables: List<UpdatableProtocol>
        get() = buildList {
            projections.values.forEach {
                if (it is UpdatableProtocol) {
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
    override fun <T : ProjectionProtocols<Color>> newProjection(
        klass: KClass<out T>,
        key: String,
        mutable: Boolean,
        contextual: Boolean
    ) = (when (klass) {
        ColorProjectionProtocol::class -> createColorProjection(key, mutable, contextual)
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

fun TypeProjectorProtocols.color(
    block: ColorTypeProjectableProtocols.() -> Unit
): ColorTypeProjectableProtocols = ColorProjectable().also {
    add(it)
    it.block()
}

inline fun <reified T : ProjectionProtocols<Color>> ColorTypeProjectableProtocols.projection(
    key: String,
    mutable: Boolean = false,
    contextual: Boolean = false
) = newProjection(klass = T::class, key = key, mutable = mutable, contextual = contextual)
