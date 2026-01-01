package com.tezov.tuucho.core.presentation.ui.render.projection.view

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.tool.json.toIndexPath
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasResolveStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.screen.ScreenContextProtocol
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

interface ViewsProjectionProtocol :
    ProjectionProcessorProtocol,
    HasResolveStatusProtocol {
    val views: List<ViewProjectionProtocol>
}

private class ViewsProjection(
    override val key: String,
    private val screenContext: ScreenContextProtocol
) : ViewsProjectionProtocol,
    TuuchoKoinComponent {
    override var hasBeenResolved: Boolean? = null
        private set

    override var views: List<ViewProjectionProtocol> = emptyList()
        private set

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        val jsonArray = jsonElement as? JsonArray ?: run {
            views = emptyList()
            return
        }
        views = buildList {
            jsonArray.forEachIndexed { index, element ->
                val textProjection = createViewProjection(index.toIndexPath(), screenContext)
                textProjection.process(element)
                add(textProjection)
            }
        }
        hasBeenResolved = true
    }
}

fun createViewsProjection(
    key: String,
    screenContext: ScreenContextProtocol
): ViewsProjectionProtocol = ViewsProjection(
    key = key,
    screenContext = screenContext
)

fun TypeProjectorProtocols.views(
    key: String,
    screenContext: ScreenContextProtocol
): ViewsProjectionProtocol = createViewsProjection(key, screenContext)
