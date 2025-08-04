package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.parser.shadower.MaterialShadower
import com.tezov.tuucho.core.data.parser.shadower._system.JsonObjectConsumerProtocol
import com.tezov.tuucho.core.data.source.shadower.ShadowerMaterialSourceProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.ShadowerMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.ShadowerMaterialRepositoryProtocol.Event
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class ShadowerMaterialRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialShadower: MaterialShadower,
    private val shadowerMaterialSources: List<ShadowerMaterialSourceProtocol>,
) : ShadowerMaterialRepositoryProtocol, KoinComponent {

    private val _events = Notifier.Emitter<Event>()
    override val events get() = _events.createCollector

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
