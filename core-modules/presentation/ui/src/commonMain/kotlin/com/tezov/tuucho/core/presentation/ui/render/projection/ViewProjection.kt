package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.presentation.ui.screen.Screen
import com.tezov.tuucho.core.presentation.ui.view._system.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view._system.ViewProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object ViewProjection : TuuchoKoinComponent {
    private suspend fun getValue(
        jsonElement: JsonElement?,
        key: String,
        screen: Screen,
        parentPath: JsonElementPath,
    ): ViewProtocol? = (jsonElement as? JsonObject)
        ?.let { componentObject ->
            val factories = getKoin().getAll<ViewFactoryProtocol>()
            factories
                .firstOrNull {
                    it.accept(componentObject)
                }?.process(
                    screen = screen,
                    path = parentPath.child(key)
                )
        }

    class Static(
        override val key: String,
        private val screen: Screen,
        private val parentPath: JsonElementPath,
    ) : Projection.AbstractStatic<ViewProtocol>(key) {
        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = getValue(jsonElement, key, screen, parentPath)
    }

    class Mutable(
        override val key: String,
        private val screen: Screen,
        private val parentPath: JsonElementPath,
    ) : Projection.AbstractMutable<ViewProtocol>(key) {
        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = getValue(jsonElement, key, screen, parentPath)
    }
}
