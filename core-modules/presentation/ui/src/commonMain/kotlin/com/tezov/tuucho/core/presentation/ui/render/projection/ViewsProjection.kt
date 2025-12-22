package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.presentation.ui.screen.Screen
import com.tezov.tuucho.core.presentation.ui.view._system.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view._system.ViewProtocol
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

interface ViewsProjectionProtocol : ProjectionProtocols<List<ViewProtocol>>

class ViewsProjection(
    key: String,
    private val screen: Screen,
    private val parentPath: JsonElementPath,
) : ViewsProjectionProtocol,
    ProjectionProtocols<List<ViewProtocol>> by Projection(
        key = key,
        storage = Projection.Static()
    ),
    TuuchoKoinComponent {

    override suspend fun getValueOrNull(
        jsonElement: JsonElement?
    ) = (jsonElement as? JsonArray)
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
}

fun createViewsProjection(
    key: String,
    screen: Screen,
    parentPath: JsonElementPath,
): ViewsProjectionProtocol = ViewsProjection(key, screen, parentPath)
