package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.parser.shadower.MaterialShadowerProtocol
import com.tezov.tuucho.core.data.parser.shadower._system.JsonObjectConsumerProtocol
import com.tezov.tuucho.core.data.source.shadower.ShadowerMaterialSourceProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol.Shadower
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class ShadowerMaterialRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialShadower: MaterialShadowerProtocol,
    private val shadowerMaterialSources: List<ShadowerMaterialSourceProtocol>,
) : Shadower, KoinComponent {

    override suspend fun process(url: String, materialObject: JsonObject, types: List<String>) =
        buildList {
            coroutineScopes.parser.await {
                shadowerMaterialSources
                    .asSequence()
                    .filter { types.contains(it.type) }
                    .forEach { it.onStart(url, materialObject) }
                materialShadower.process(
                    materialObject = materialObject,
                    jsonObjectConsumer = object : JsonObjectConsumerProtocol {

                        override suspend fun onNext(jsonObject: JsonObject) {
                            shadowerMaterialSources
                                .asSequence()
                                .filter { !it.isCancelled && types.contains(it.type) }
                                .forEach { it.onNext(jsonObject) }
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
