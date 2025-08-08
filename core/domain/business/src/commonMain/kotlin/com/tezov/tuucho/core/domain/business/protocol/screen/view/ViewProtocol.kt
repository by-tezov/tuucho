package com.tezov.tuucho.core.domain.business.protocol.screen.view

import com.tezov.tuucho.core.domain.business.protocol.SourceIdentifierProtocol
import kotlinx.serialization.json.JsonObject

interface ViewProtocol {

    interface IdentifierProtocol : SourceIdentifierProtocol

    val identifier: IdentifierProtocol

    suspend fun update(jsonObject: JsonObject)
}