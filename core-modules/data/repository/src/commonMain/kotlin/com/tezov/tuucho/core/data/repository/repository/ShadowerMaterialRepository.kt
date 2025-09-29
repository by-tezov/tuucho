package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.parser.shadower.MaterialShadower
import com.tezov.tuucho.core.data.repository.parser.shadower._system.JsonObjectConsumerProtocol
import com.tezov.tuucho.core.data.repository.source.shadower.ShadowerMaterialSourceProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol.Shadower
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent

class ShadowerMaterialRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialShadower: MaterialShadower,
    private val shadowerMaterialSources: List<ShadowerMaterialSourceProtocol>,
) : Shadower, KoinComponent {

    override suspend fun process(url: String, componentObject: JsonObject, types: List<String>) =
        buildList {
            coroutineScopes.parser.await {
                shadowerMaterialSources
                    .asSequence()
                    .filter { types.contains(it.type) }
                    .forEach { it.onStart(url, componentObject) }
                materialShadower.process(
                    componentObject = componentObject,
                    jsonObjectConsumer = object : JsonObjectConsumerProtocol {

                        override suspend fun onNext(
                            jsonObject: JsonObject,
                            shadowerSettingObject: JsonObject?,
                        ) {
                            shadowerMaterialSources
                                .asSequence()
                                .filter { !it.isCancelled && types.contains(it.type) }
                                .forEach {
                                    it.onNext(
                                        jsonObject = jsonObject,
                                        settingObject = shadowerSettingObject?.withScope(
                                            SettingComponentShadowerSchema::Scope
                                        )?.get(it.type)?.jsonObject
                                    )
                                }
                        }

                        override suspend fun onDone() {
                            shadowerMaterialSources
                                .asSequence()
                                .filter { !it.isCancelled && types.contains(it.type) }
                                .forEach {
                                    it.onDone().map { jsonObject ->
                                        Shadower.Output(
                                            type = it.type,
                                            url = url,
                                            jsonObject = jsonObject
                                        )
                                    }.let(::addAll)
                                }
                        }
                    }
                )
            }
        }
}
