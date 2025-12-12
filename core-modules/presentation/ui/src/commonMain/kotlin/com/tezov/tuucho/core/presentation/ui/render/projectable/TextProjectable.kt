package com.tezov.tuucho.core.presentation.ui.render.projectable

import com.tezov.tuucho.core.presentation.ui.annotation.TuuchoUiDsl
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.TextProjection
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectableProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.TypeProjectorProtocol
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

@TuuchoUiDsl
class TextTypeProjectable : ProjectableProtocol {

    private val projections = mutableMapOf<String, ProjectionProtocol>()

    override val keys get() = projections.keys

    override suspend fun process(
        jsonElement: JsonElement?,
        key: String
    ) {
        projections[key]?.process(jsonElement)
    }

    fun <T : ProjectionProtocol> newProjection(
        klass: KClass<out T>,
        key: String
    ): T =
        @Suppress("UNCHECKED_CAST")
        (when (klass) {
            TextProjection.Static::class -> TextProjection.Static(key)
            TextProjection.Mutable::class -> TextProjection.Mutable(key)
            else -> throw UiException.Default("not implemented")
        } as T).also { projections[it.key] = it }
}

fun TypeProjectorProtocol.text(
    block: TextTypeProjectable.() -> Unit
): TextTypeProjectable = TextTypeProjectable().also {
    add(it)
    it.block()
}
inline fun <reified T : ProjectionProtocol> TextTypeProjectable.projection(
    key: String
) = newProjection(klass = T::class, key = key)
