package com.tezov.tuucho.core.domain.business.navigation.protocol

import com.tezov.tuucho.core.domain.business.navigation.NavigationDestination
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.ViewContext
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.StateViewProtocol
import com.tezov.tuucho.core.domain.business.usecase.RenderViewContextUseCase
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface ViewContextStackRepositoryProtocol {

    val currentViewContext: ViewContext

    suspend fun swallow(stack: List<NavigationDestination>, componentObject: JsonObject)

    fun getViewState(url: String): StateViewProtocol

}

class ViewContextStackRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
) : ViewContextStackRepositoryProtocol, KoinComponent {

    private val renderViewContext: RenderViewContextUseCase by inject()

    override val currentViewContext: ViewContext
        get() = stack.last()

    private val stack = mutableListOf<ViewContext>()

    override suspend fun swallow(
        stack: List<NavigationDestination>,
        componentObject: JsonObject,
    ) {
        val updatedStack = mutableListOf<ViewContext>()
        val existingStack = this.stack.toMutableList()

        for ((index, destination) in stack.withIndex()) {
            when (val route = destination.route) {
                is NavigationRoute.Url -> {
                    val url = route.value
                    val existingAtSameIndex = this.stack.getOrNull(index)?.takeIf { it.url == url }
                    if (existingAtSameIndex != null) {
                        updatedStack.add(existingAtSameIndex)
                        existingStack.remove(existingAtSameIndex)
                    } else {
                        val existingElsewhere = existingStack.firstOrNull { it.url == url }
                        if (existingElsewhere != null) {
                            updatedStack.add(existingElsewhere)
                            existingStack.remove(existingElsewhere)
                        } else {
                            val newViewContext = renderViewContext.invoke(url, componentObject)
                            updatedStack.add(newViewContext)
                        }
                    }
                }

                else -> {
                    throw IllegalArgumentException("Unsupported NavigationRoute: $route")
                }
            }
        }
        this.stack.clear()
        this.stack.addAll(updatedStack)
    }

    override fun getViewState(url: String) = stack.first { it.url == url }.state
}
