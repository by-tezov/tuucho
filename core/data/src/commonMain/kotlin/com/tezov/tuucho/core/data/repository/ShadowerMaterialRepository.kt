package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.parser.shadower.MaterialShadower
import com.tezov.tuucho.core.data.parser.shadower._system.JsonObjectConsumerProtocol
import com.tezov.tuucho.core.data.source.shadower.ShadowerMaterialSourceProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol.Shadower
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class ShadowerMaterialRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialShadower: MaterialShadower,
    private val shadowerMaterialSources: List<ShadowerMaterialSourceProtocol>,
) : Shadower, KoinComponent {

    private val _events = Notifier.Emitter<Shadower.Event>(
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val events get() = _events.createCollector

    override fun process(url: String, materialObject: JsonObject) {
        coroutineScopes.parser.async {
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
                                    coroutineScopes.event.async {
                                        _events.emit(
                                            Shadower.Event(
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
