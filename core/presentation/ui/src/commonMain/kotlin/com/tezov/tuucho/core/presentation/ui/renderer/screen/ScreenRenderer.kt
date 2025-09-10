package com.tezov.tuucho.core.presentation.ui.renderer.screen

import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenRendererProtocol
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.AbstractViewFactory
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScreenRenderer(
    private val coroutineScopes: CoroutineScopesProtocol,
) : ScreenRendererProtocol, KoinComponent {

    private val viewFactories: List<AbstractViewFactory> by inject()

    override suspend fun process(route: NavigationRoute, componentObject: JsonObject) = coroutineScopes.renderer.await {
        val type = componentObject.withScope(TypeSchema::Scope).self
        if (type != TypeSchema.Value.component) {
            throw UiException.Default("object is not a component $componentObject")
        }
        val id = componentObject.onScope(IdSchema::Scope).value
        val subset = componentObject.withScope(SubsetSchema::Scope).self

        val viewFactory = viewFactories
            .filter { it.accept(componentObject) }
            .singleOrThrow(id, subset)
            ?: throw UiException.Default("No renderer found for $componentObject")

        Screen(
            view = viewFactory.process(route, componentObject),
            route = route
        )
    }

    private fun <T> List<T>.singleOrThrow(id: String?, subset: String?): T? {
        if (size > 1) throw UiException.Default("Only one renderer can accept the object $id $subset")
        return firstOrNull()
    }

}


