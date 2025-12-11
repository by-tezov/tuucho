package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.tool.json.JsonElementPath.Companion.INDEX_SEPARATOR
import com.tezov.tuucho.core.presentation.ui.render.projector.TypeProjectorProtocols
import com.tezov.tuucho.core.presentation.ui.render.protocol.HasResolveStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProcessorProtocol
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

interface TextsProjectionProtocol : ProjectionProcessorProtocol, HasResolveStatusProtocol {
    val texts: List<TextProjectionProtocol>
}

private class TextsProjection(
    override val key: String,
) : TextsProjectionProtocol {

    override var hasBeenResolved: Boolean? = null
        private set

    override var texts: List<TextProjectionProtocol> = emptyList()
        private set

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        val jsonArray = jsonElement as? JsonArray ?: run {
            texts = emptyList()
            return
        }
        texts = buildList {
            jsonArray.forEachIndexed { index, element ->
                val textProjection = createTextProjection("${INDEX_SEPARATOR}$index")
                textProjection.process(element)
                add(textProjection)
            }
        }
        hasBeenResolved = true
    }
}

fun createTextsProjection(
    key: String
): TextsProjectionProtocol = TextsProjection(
    key = key
)

fun TypeProjectorProtocols.texts(
    key: String,
): TextsProjectionProtocol = createTextsProjection(key)
