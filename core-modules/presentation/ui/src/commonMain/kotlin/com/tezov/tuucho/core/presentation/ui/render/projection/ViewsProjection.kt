package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.presentation.ui.screen.Screen
import com.tezov.tuucho.core.presentation.ui.view._system.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view._system.ViewProtocol
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object ViewsProjection : TuuchoKoinComponent {
    private suspend fun getValue(
        jsonElement: JsonElement?,
        key: String,
        screen: Screen,
        parentPath: JsonElementPath,
    ): List<ViewProtocol>? = (jsonElement as? JsonArray)
        ?.let { componentsArray ->
            val factories = getKoin().getAll<ViewFactoryProtocol>()
            buildList {
                val childPath = parentPath.child(key)
                componentsArray.forEachIndexed { index, componentObject ->
                    factories
                        .firstOrNull {
                            it.accept(componentObject.jsonObject)
                        }?.process(
                            screen = screen,
                            path = childPath.atIndex(index)
                        )?.also { add(it) }
                }
            }
        }

    class Static(
        override val key: String,
        private val screen: Screen,
        private val parentPath: JsonElementPath,
    ) : Projection.AbstractStatic<List<ViewProtocol>>(key) {
        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = getValue(jsonElement, key, screen, parentPath)
    }

    class Mutable(
        override val key: String,
        private val screen: Screen,
        private val parentPath: JsonElementPath,
    ) : Projection.AbstractMutable<List<ViewProtocol>>(key) {
        override suspend fun getValue(
            jsonElement: JsonElement?
        ) = getValue(jsonElement, key, screen, parentPath)
    }
}
