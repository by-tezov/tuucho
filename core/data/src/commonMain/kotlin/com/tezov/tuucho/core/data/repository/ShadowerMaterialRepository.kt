package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.parser.shadower.MaterialShadower
import com.tezov.tuucho.core.data.parser.shadower._system.JsonObjectConsumerProtocol
import com.tezov.tuucho.core.data.source.shadower.ShadowerMaterialSourceProtocol
import com.tezov.tuucho.core.domain.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.protocol.ShadowerMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ShadowerMaterialRepositoryProtocol.Event
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class ShadowerMaterialRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialShadower: MaterialShadower,
    private val shadowerMaterialSources: List<ShadowerMaterialSourceProtocol>,
) : ShadowerMaterialRepositoryProtocol, KoinComponent {

    private val _events = MutableSharedFlow<Event>(replay = 0)
    override val events: SharedFlow<Event> = _events

    fun process(url: String, materialObject: JsonObject) {
        coroutineScopes.launchOnParser {
            shadowerMaterialSources.forEach { it.onStart(url, materialObject) }
            materialShadower.process(
                materialObject = materialObject,
                jsonObjectConsumer = object : JsonObjectConsumerProtocol {

                    override suspend fun onNext(jsonObject: JsonObject) {
                        shadowerMaterialSources
                            .asSequence()
                            .filter { !it.isCancelled }
                            .forEach { it.onNext(jsonObject) }
                    }

                    override suspend fun onDone() {
                        shadowerMaterialSources
                            .asSequence()
                            .filter { !it.isCancelled }
                            .forEach { source ->
                                source.onDone().collect {
                                    coroutineScopes.launchOnEvent {
                                        _events.emit(
                                            Event(
                                                type = source.type,
                                                url = url,
                                                jsonObject = it
                                            )
                                        )
                                    }
                                }
                            }
                    }
                }
            )
        }
    }
}
