package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.TextProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.createTextProjection
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasReadyStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasUpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.defaultStatus
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

interface TextTypeProjectableProtocols : ProjectableProtocol, HasUpdatableProtocol, HasReadyStatusProtocol {
    fun <T : ProjectionProtocols<String>> newProjection(
        klass: KClass<out T>,
        key: String,
        mutable: Boolean,
        contextual: Boolean
    ): T
}

@TuuchoUiDsl
class TextTypeProjectable : TextTypeProjectableProtocols {
    private val projections = mutableMapOf<String, ProjectionProtocols<String>>()

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
    override fun <T : ProjectionProtocols<String>> newProjection(
        klass: KClass<out T>,
        key: String,
        mutable: Boolean,
        contextual: Boolean
    ): T = (when (klass) {
        TextProjectionProtocol::class -> createTextProjection(key, mutable, contextual)
        else -> throw UiException.Default("not implemented")
    } as T).also {
        projections[it.key] = it
        (it as? HasReadyStatusProtocol)?.let { status ->
            status.onStatusChanged = {
                val previous = isReady
                val next = projections.values.fold(defaultStatus) { acc, projection ->
                    if (projection is HasReadyStatusProtocol) {
                        acc && projection.isReady
                    } else {
                        acc
                    }
                }
                if (previous != next) {
                    isReady = next
                    if (this::onStatusChanged.isInitialized) {
                        onStatusChanged.invoke()
                    }
                }
            }
        }
    }
}

fun TypeProjectorProtocols.text(
    block: TextTypeProjectableProtocols.() -> Unit
): TextTypeProjectableProtocols = TextTypeProjectable().also {
    add(it)
    it.block()
}

inline fun <reified T : ProjectionProtocols<String>> TextTypeProjectableProtocols.projection(
    key: String,
    mutable: Boolean = false,
    contextual: Boolean = false
) = newProjection(klass = T::class, key = key, mutable = mutable, contextual = contextual)
