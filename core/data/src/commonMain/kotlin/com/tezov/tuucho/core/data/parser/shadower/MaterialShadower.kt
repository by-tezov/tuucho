package com.tezov.tuucho.core.data.parser.shadower

import com.tezov.tuucho.core.data.parser.shadower._system.JsonObjectConsumerProtocol
import com.tezov.tuucho.core.domain.business._system.toPath
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialShadower : KoinComponent {

    private val componentShadower: ComponentShadower by inject()

    suspend fun process(
        materialObject: JsonObject,
        jsonObjectConsumer: JsonObjectConsumerProtocol
    ) {
        componentShadower.process(
            path = "".toPath(),
            element = materialObject,
            jsonObjectConsumer = jsonObjectConsumer
        )
        jsonObjectConsumer.onDone()
    }

}