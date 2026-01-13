package com.tezov.tuucho.core.presentation.ui.render.projection.view

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.presentation.ui.render.projection.ExtractorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.Projection
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

private typealias ViewTypeAlias = ViewProtocol

private typealias ViewProjectionTypeAlias = ProjectionProtocols<ViewProtocol>

interface ViewProjectionProtocol : ViewProjectionTypeAlias

private class ViewProjection(
    private val screenContext: ScreenContextProtocol,
    private val projection: ViewProjectionTypeAlias
) : ViewProjectionProtocol,
    ViewProjectionTypeAlias by projection,
    TuuchoKoinComponent {
    init {
        attach(this as ExtractorProjectionProtocol<ViewTypeAlias>)
    }

    override suspend fun extract(
        jsonElement: JsonElement?
    ) = (jsonElement as? JsonObject)
        ?.let { componentObject ->
            val factories = getKoin().getAll<ViewFactoryProtocol>()
            factories
                .firstOrNull {
                    it.accept(componentObject)
                }?.process(screenContext = screenContext)
        }

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        projection.process(jsonElement)
        (jsonElement as? JsonObject)?.let { componentObject ->
            projection.value?.apply {
                initialize(componentObject)
                screenContext.addView(this)
            }
        }
    }
}

fun createViewProjection(
    key: String,
    screenContext: ScreenContextProtocol,
): ViewProjectionProtocol = ViewProjection(
    screenContext = screenContext,
    projection = Projection(key = key)
)

fun TypeProjectorProtocols.view(
    key: String,
    screenContext: ScreenContextProtocol
): ViewProjectionProtocol = createViewProjection(key, screenContext)
