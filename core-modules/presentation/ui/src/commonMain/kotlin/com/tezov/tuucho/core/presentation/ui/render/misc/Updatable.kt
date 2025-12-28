package com.tezov.tuucho.core.presentation.ui.render.misc

import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import kotlinx.serialization.json.JsonElement

class Updatable(
    override val type: String,
) : UpdatableProtocol {

    override var id: String? = null
        private set

    override suspend fun process(jsonElement: JsonElement?) {
        if (id == null) {
            jsonElement?.idValue?.let { id = it }
        }
    }
}
