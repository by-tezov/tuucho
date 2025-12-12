package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.ActionProjection
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.TypeProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@TuuchoUiDsl
class ActionTypeProjectable : ProjectableProtocol {

    private val projections = mutableMapOf<String, ProjectionProtocol>()

    override val keys get() = projections.keys.toSet()

    override suspend fun process(
        jsonElement: JsonElement?,
        key: String
    ) {
        projections[key]?.process(jsonElement)
    }

    fun <T : ProjectionProtocol> newProjection(
        klass: KClass<out T>,
        key: String,
        route: NavigationRoute
    ) =
        @Suppress("UNCHECKED_CAST")
        (when (klass) {
            ActionProjection.Static::class -> ActionProjection.Static(key, route)
            ActionProjection.Mutable::class -> ActionProjection.Mutable(key, route)
            else -> throw UiException.Default("not implemented")
        } as T).also { projections[it.key] = it }
}

fun TypeProjectorProtocol.action(
    block: ActionTypeProjectable.() -> Unit
): ActionTypeProjectable = ActionTypeProjectable().also {
    add(it)
    it.block()
}

inline fun <reified T : ProjectionProtocol> ActionTypeProjectable.projection(
    key: String,
    route: NavigationRoute
) = newProjection(klass = T::class, key = key, route = route)
