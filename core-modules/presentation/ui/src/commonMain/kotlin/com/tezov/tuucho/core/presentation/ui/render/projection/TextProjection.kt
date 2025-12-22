package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TextSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

interface TextProjectionProtocol : ProjectionProtocols<String>

class TextProjection(
    key: String,
    storage: ProjectionStorageProtocol<String>,
) : TextProjectionProtocol,
    ProjectionProtocols<String> by Projection(
        key = key,
        storage = storage,
        getValueOrNull = { jsonElement ->
            when (jsonElement) {
                is JsonObject -> {
                    jsonElement
                        .withScope(TextSchema::Scope)
                        .default
                }

                is JsonPrimitive -> {
                    jsonElement.stringOrNull
                }

                else -> {
                    null
                }
            }
        }
    )

private class ContextualTextProjection(
    private val delegate: TextProjectionProtocol
) : TextProjectionProtocol by delegate,
    UpdatableProtocol {
    override val type = TypeSchema.Value.text

    override lateinit var id: String
        private set

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (!this::id.isInitialized) {
            jsonElement?.idValue?.let { id = it }
        }
        delegate.process(jsonElement)
    }
}

fun createTextProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): TextProjectionProtocol {
    val projection = when (mutable) {
        true -> TextProjection(key, Projection.Mutable())
        false -> TextProjection(key, Projection.Static())
    }
    return when {
        contextual -> ContextualTextProjection(projection)
        else -> projection
    }
}
