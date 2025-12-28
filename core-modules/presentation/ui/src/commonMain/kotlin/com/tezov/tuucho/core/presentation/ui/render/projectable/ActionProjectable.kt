package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.ActionProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.createActionProjection
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasReadyStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasUpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.defaultStatus
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

interface ActionProjectableProtocols : ProjectableProtocol, HasUpdatableProtocol, HasReadyStatusProtocol {
    fun <T : ProjectionProtocols<() -> Unit>> newProjection(
        klass: KClass<out T>,
        key: String,
        route: NavigationRoute,
        mutable: Boolean,
        contextual: Boolean
    ): T
}

@TuuchoUiDsl
class ActionProjectable : ActionProjectableProtocols {
    private val projections = mutableMapOf<String, ProjectionProtocols<() -> Unit>>()

    override val keys get() = projections.keys.toSet()

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
    override fun <T : ProjectionProtocols<() -> Unit>> newProjection(
        klass: KClass<out T>,
        key: String,
        route: NavigationRoute,
        mutable: Boolean,
        contextual: Boolean
    ) = (when (klass) {
        ActionProjectionProtocol::class -> createActionProjection(key, route, mutable, contextual)
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

fun TypeProjectorProtocols.action(
    block: ActionProjectableProtocols.() -> Unit
): ActionProjectableProtocols = ActionProjectable().also {
    add(it)
    it.block()
}

inline fun <reified T : ProjectionProtocols<() -> Unit>> ActionProjectableProtocols.projection(
    key: String,
    route: NavigationRoute,
    mutable: Boolean = false,
    contextual: Boolean = false
) = newProjection(klass = T::class, key = key, route = route, mutable = mutable, contextual = contextual)
