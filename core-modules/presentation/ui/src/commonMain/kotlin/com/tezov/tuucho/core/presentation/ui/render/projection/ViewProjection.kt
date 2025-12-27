package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.presentation.ui.screen.Screen
import com.tezov.tuucho.core.presentation.ui.view._system.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view._system.ViewProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

private typealias ViewProjectionProtocols = ProjectionProtocols<ViewProtocol>

interface ViewProjectionProtocol : ViewProjectionProtocols

class ViewProjection(
    private val screen: Screen,
    private val parentPath: JsonElementPath,
    private val projection: ViewProjectionProtocols
) : ViewProjectionProtocol,
    ViewProjectionProtocols by projection,
    TuuchoKoinComponent {
    init {
        attach(this)
    }

    override suspend fun getValueOrNull(
        jsonElement: JsonElement?
    ) = (jsonElement as? JsonObject)
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
}

fun createViewProjection(
    key: String,
    screen: Screen,
    parentPath: JsonElementPath,
): ViewProjectionProtocol = ViewProjection(
    screen = screen,
    parentPath = parentPath,
    projection = Projection(
        key = key,
        storage = Projection.Static()
    )
)
