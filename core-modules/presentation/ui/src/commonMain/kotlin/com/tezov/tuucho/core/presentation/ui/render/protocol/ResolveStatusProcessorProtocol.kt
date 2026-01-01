package com.tezov.tuucho.core.presentation.ui.render.protocol

import kotlinx.serialization.json.JsonElement

interface HasResolveStatusProtocol {
    val hasBeenResolved: Boolean?
}

interface ResolveStatusProcessorProtocol :
    HasResolveStatusProtocol,
    HasRequestViewUpdateProtocol,
    RequestViewUpdateSetterProtocol {
    fun update(
        jsonElement: JsonElement?
    )
}
