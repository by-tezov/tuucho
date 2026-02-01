package com.tezov.tuucho.core.data.repository.parser.shadower

import com.tezov.tuucho.core.data.repository.parser.shadower._system.JsonObjectConsumerProtocol
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ComponentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class MaterialShadower(
    private val coroutineScopes: CoroutineScopesProtocol
) : TuuchoKoinComponent {
    private val componentShadower: ComponentShadower by inject()

    suspend fun process(
        componentObject: JsonObject,
        jsonObjectConsumer: JsonObjectConsumerProtocol,
    ) {
        coroutineScopes.default.withContext {
            componentShadower.process(
                path = ROOT_PATH,
                element = componentObject,
                jsonObjectConsumer = object : JsonObjectConsumerProtocol {
                    private val shadowerSettingObject = componentObject
                        .withScope(ComponentSchema::Scope)
                        .setting
                        ?.withScope(ComponentSettingSchema.Root::Scope)
                        ?.shadower

                    override suspend fun onNext(
                        jsonObject: JsonObject,
                        shadowerSettingObject: JsonObject?,
                    ) {
                        jsonObjectConsumer.onNext(jsonObject, this.shadowerSettingObject)
                    }

                    override suspend fun onDone() {
                        jsonObjectConsumer.onDone()
                    }
                }
            )
            jsonObjectConsumer.onDone()
        }
    }
}
