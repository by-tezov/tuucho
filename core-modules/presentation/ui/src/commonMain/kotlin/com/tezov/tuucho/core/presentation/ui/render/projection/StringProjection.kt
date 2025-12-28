package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import com.tezov.tuucho.core.presentation.ui.render.misc.ReadyStatus
import com.tezov.tuucho.core.presentation.ui.render.misc.Updatable
import com.tezov.tuucho.core.presentation.ui.render.protocol.ReadyStatusProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

private typealias StringProjectionTypeAlias = ProjectionProtocols<String>

interface StringProjectionProtocol : StringProjectionTypeAlias

class StringProjection(
    private val projection: StringProjectionTypeAlias,
) : StringProjectionProtocol,
    StringProjectionTypeAlias by projection {
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
    private val delegate: StringProjectionProtocol,
    private val updatable: UpdatableProtocol,
    private val status: ReadyStatusProtocol
) : StringProjectionProtocol by delegate,
    UpdatableProtocol by updatable,
    ReadyStatusProtocol by status {

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        delegate.process(jsonElement)
        updatable.process(jsonElement)
        status.update(jsonElement)
    }
}

fun createStringProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): StringProjectionProtocol {
    val projection: StringProjectionTypeAlias = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val stringProjection = StringProjection(projection)
    return when {
        contextual -> ContextualStringProjection(
            delegate = stringProjection,
            updatable = Updatable(TypeSchema.Value.dimension),
            status = ReadyStatus()
        )
        else -> stringProjection
    }
}
