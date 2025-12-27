package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias StringProjectionProtocols = ProjectionProtocols<String>

interface StringProjectionProtocol : StringProjectionProtocols

class StringProjection(
    private val projection: StringProjectionProtocols,
) : StringProjectionProtocol,
    StringProjectionProtocols by projection {
    init {
        attach(this)
    }

    override suspend fun getValueOrNull(
        jsonElement: JsonElement?
    ) = when (jsonElement) {
        is JsonObject -> {
            jsonElement
                .withScope(DimensionSchema::Scope)
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

private class ContextualStringProjection(
    private val delegate: StringProjectionProtocol
) : StringProjectionProtocol by delegate,
    UpdatableProtocol {
    override val type = TypeSchema.Value.dimension

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

fun createStringProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): StringProjectionProtocol {
    val projection: StringProjectionProtocols = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val stringProjection = StringProjection(projection)
    return when {
        contextual -> ContextualStringProjection(stringProjection)
        else -> stringProjection
    }
}
