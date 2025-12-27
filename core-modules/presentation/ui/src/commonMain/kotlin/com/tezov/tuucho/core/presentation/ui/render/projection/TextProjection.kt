package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TextSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui._system.idSourceOrNull
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias TextProjectionProtocols = ProjectionProtocols<String>

interface TextProjectionProtocol : TextProjectionProtocols

class TextProjection(
    private val projection: TextProjectionProtocols,
) : TextProjectionProtocol,
    TextProjectionProtocols by projection {
    init {
        attach(this)
    }

    override suspend fun getValueOrNull(
        jsonElement: JsonElement?
    ) = when (jsonElement) {
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

class Updatable(
    override val type: String,
) : UpdatableProtocol {

    override var isReady = false
        private set

    override var id: String? = null
        private set

    fun updateIsReady(
        jsonElement: JsonElement?
    ) {
        isReady = jsonElement != null && jsonElement.idSourceOrNull == null
    }

    override suspend fun process(jsonElement: JsonElement?) {
        if (id == null) {
            jsonElement?.idValue?.let { id = it }
        }
        updateIsReady(jsonElement)
    }
}

private class ContextualTextProjection(
    private val delegate: TextProjectionProtocol,
    private val updatable:Updatable = Updatable(TypeSchema.Value.text)
) : TextProjectionProtocol by delegate,
    UpdatableProtocol by updatable {

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        delegate.process(jsonElement)
        updatable.process(jsonElement)
    }
}

fun createTextProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): TextProjectionProtocol {
    val projection: TextProjectionProtocols = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val textProjection = TextProjection(projection)
    return when {
        contextual -> ContextualTextProjection(textProjection)
        else -> textProjection
    }
}
