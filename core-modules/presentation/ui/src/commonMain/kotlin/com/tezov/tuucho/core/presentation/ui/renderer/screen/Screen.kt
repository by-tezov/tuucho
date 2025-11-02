package com.tezov.tuucho.core.presentation.ui.renderer.screen

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.presentation.ui.renderer.screen._system.ScreenProtocol
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewProtocol
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol as DomainViewProtocol

class Screen(
    override val view: ViewProtocol,
    override val route: NavigationRoute.Url,
) : ScreenProtocol {
    @Composable
    override fun display(
        scope: Any?
    ) {
        view.display(scope)
    }

    override suspend fun update(
        jsonObject: JsonObject
    ) {
        view.update(jsonObject)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V : DomainViewProtocol> views(
        klass: KClass<V>
    ) = view
        .flatten()
        .asSequence()
        .filter { klass.isInstance(it) }
        .toList() as List<V>
}

fun ViewProtocol.flatten(): List<ViewProtocol> = buildList {
    val stack = ArrayDeque<ViewProtocol>()
    stack.add(this@flatten)
    while (stack.isNotEmpty()) {
        val current = stack.removeLast()
        add(current)
        current.children?.let {
            stack.addAll(it)
        }
    }
}
