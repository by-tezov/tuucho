package com.tezov.tuucho.core.domain.business.protocol.screen

import com.tezov.tuucho.core.domain.business.protocol.SourceIdentifierProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass

interface ScreenProtocol {

    interface IdentifierProtocol : SourceIdentifierProtocol

    val identifier: IdentifierProtocol

    suspend fun update(jsonObject: JsonObject)

    fun <V : ViewProtocol> views(klass: KClass<V>): List<V>
}