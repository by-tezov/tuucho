package com.tezov.tuucho.core.presentation.ui.render.updatable

import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.render.protocol.ComponentProjectorProtocol
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement

class ComponentUpdatable(
    private val componentProjector: ComponentProjectorProtocol
) : UpdatableProtocol {
    override val type = TypeSchema.Value.component

    override lateinit var id: String

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (!this::id.isInitialized) {
            jsonElement?.idValue?.let { id = it }
        } else {
            componentProjector.process(jsonElement)
        }
    }
}
