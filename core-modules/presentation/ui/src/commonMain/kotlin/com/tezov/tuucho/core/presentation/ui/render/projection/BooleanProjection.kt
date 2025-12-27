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

private typealias BooleanProjectionTypeAlias = ProjectionProtocols<Boolean>

interface BooleanProjectionProtocol : BooleanProjectionTypeAlias

class BooleanProjection(
    private val projection: BooleanProjectionTypeAlias,
) : BooleanProjectionProtocol,
    BooleanProjectionTypeAlias by projection {
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
                ?.toBooleanStrictOrNull()
        }

        is JsonPrimitive -> {
            jsonElement.stringOrNull
                ?.toBooleanStrictOrNull()
        }

        else -> {
            null
        }
    }
}

private class ContextualBooleanProjection(
    private val delegate: BooleanProjectionProtocol
) : BooleanProjectionProtocol by delegate,
    UpdatableProtocol {
    override val type = TypeSchema.Value.dimension

    override var id: String? = null
        private set

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (id == null) {
            jsonElement?.idValue?.let { id = it }
        }
        delegate.process(jsonElement)
    }
}

fun createBooleanProjection(
    key: String,
    mutable: Boolean,
    contextual: Boolean
): BooleanProjectionProtocol {
    val projection: BooleanProjectionTypeAlias = Projection(
        key = key,
        storage = when (mutable) {
            true -> Projection.Mutable()
            false -> Projection.Static()
        }
    )
    val booleanProjection = BooleanProjection(projection)
    return when {
        contextual -> ContextualBooleanProjection(booleanProjection)
        else -> booleanProjection
    }
}
