package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.ProjectionProtocols
import com.tezov.tuucho.core.presentation.ui.render.projection.ViewProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.ViewProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ViewsProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.ViewsProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.createViewProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.createViewsProjection
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.TypeProjectorProtocol
import com.tezov.tuucho.core.presentation.ui.screen.Screen
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@TuuchoUiDsl
class ViewProjectable : ProjectableProtocol {
    private val projections = mutableMapOf<String, ProjectionProtocols<*>>()

    override val keys get() = projections.keys

    override suspend fun process(
        jsonElement: JsonElement?,
        key: String
    ) {
        projections[key]?.process(jsonElement)
    }

    @Suppress("UNCHECKED_CAST")
    fun <I, T : ProjectionProtocols<I>> newProjection(
        klass: KClass<out T>,
        key: String,
        screen: Screen,
        path: JsonElementPath
    ) = (when (klass) {
        ViewsProjection::class, ViewsProjectionProtocol::class -> createViewsProjection(key, screen, path)
        ViewProjection::class, ViewProjectionProtocol::class -> createViewProjection(key, screen, path)
        else -> throw UiException.Default("not implemented")
    } as T).also { projections[it.key] = it }
}

fun TypeProjectorProtocol.view(
    block: ViewProjectable.() -> Unit
): ViewProjectable = ViewProjectable().also {
    add(it)
    it.block()
}

inline fun <I, reified T : ProjectionProtocols<I>> ViewProjectable.projection(
    key: String,
    screen: Screen,
    path: JsonElementPath,
) = newProjection(klass = T::class, key = key, screen = screen, path = path)
