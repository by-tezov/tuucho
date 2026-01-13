package com.tezov.tuucho.core.presentation.ui.render.misc

import com.tezov.tuucho.core.presentation.ui._system.idValueOrNull
import com.tezov.tuucho.core.presentation.ui.render.protocol.IdProcessorProtocol
import kotlinx.serialization.json.JsonElement

class IdProcessor : IdProcessorProtocol {
    override var id: String? = null
        private set

    override suspend fun process(
        jsonElement: JsonElement?
    ) {
        if (id == null) {
            jsonElement?.idValueOrNull?.let { id = it }
        }
    }
}
