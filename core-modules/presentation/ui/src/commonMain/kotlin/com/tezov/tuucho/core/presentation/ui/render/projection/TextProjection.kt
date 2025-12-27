package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TextSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui.render.protocol.StatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.render.updatable.Status
import com.tezov.tuucho.core.presentation.ui.render.updatable.Updatable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias TextProjectionTypeAlias = ProjectionProtocols<String>

interface TextProjectionProtocol : TextProjectionTypeAlias

private class TextProjection(
    private val projection: TextProjectionTypeAlias,
) : TextProjectionProtocol,
    TextProjectionTypeAlias by projection {
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

private class ContextualTextProjection(
    private val delegate: TextProjectionProtocol,
    private val updatable: UpdatableProtocol,
    private val status: StatusProtocol
) : TextProjectionProtocol by delegate,
    UpdatableProtocol by updatable,
    StatusProtocol by status {

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        delegate.process(jsonElement)
        updatable.process(jsonElement)
        status.update(jsonElement)
    }
}

fun createTextProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): TextProjectionProtocol {
    val projection: TextProjectionTypeAlias = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val textProjection = TextProjection(projection)
    return when {
        contextual -> ContextualTextProjection(
            delegate = textProjection,
            updatable = Updatable(TypeSchema.Value.text),
            status = Status()
        )
        else -> textProjection
    }
}
