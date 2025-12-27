package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.ActionProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.createActionProjection
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.TypeProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@TuuchoUiDsl
class ActionTypeProjectable : ProjectableProtocol {
    private val projections = mutableMapOf<String, ProjectionProtocols<() -> Unit>>()

    override val keys get() = projections.keys.toSet()

    override suspend fun process(
        jsonElement: JsonElement?,
        key: String
    ) {
        projections[key]?.process(jsonElement)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ProjectionProtocols<() -> Unit>> newProjection(
        klass: KClass<out T>,
        key: String,
        route: NavigationRoute,
        mutable: Boolean,
        contextual: Boolean
    ) = (when (klass) {
        ActionProjectionProtocol::class -> createActionProjection(key, route, mutable, contextual)
        else -> throw UiException.Default("not implemented")
    } as T).also { projections[it.key] = it }
}

fun TypeProjectorProtocol.action(
    block: ActionTypeProjectable.() -> Unit
): ActionTypeProjectable = ActionTypeProjectable().also {
    add(it)
    it.block()
}

inline fun <reified T : ProjectionProtocols<() -> Unit>> ActionTypeProjectable.projection(
    key: String,
    route: NavigationRoute,
    mutable: Boolean = false,
    contextual: Boolean = false
) = newProjection(klass = T::class, key = key, route = route, mutable = mutable, contextual = contextual)
