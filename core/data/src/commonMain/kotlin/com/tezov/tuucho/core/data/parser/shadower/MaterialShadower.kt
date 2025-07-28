package com.tezov.tuucho.core.data.parser.shadower

import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialShadower : KoinComponent {

    private val componentShadower: ComponentShadower by inject()

    @Suppress("RedundantSuspendModifier")
    suspend fun process(
        material: JsonObject,
    ) {
        componentShadower.process(
            path = "".toPath(),
            element = material,
            jsonObjectConsumer = { jsonObject ->


                if (jsonObject.onScope(IdSchema::Scope).source != null) {
                    println(jsonObject)
                }

            }
        )
    }

}