package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.di.MaterialRepositoryModule.Name
import com.tezov.tuucho.core.data.parser.shadower.MaterialShadower
import com.tezov.tuucho.core.data.parser.shadower._system.JsonObjectConsumerProtocol
import com.tezov.tuucho.core.data.source.shadower.ShadowerMaterialSourceProtocol
import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import com.tezov.tuucho.core.domain.protocol.ShadowerMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ShadowerMaterialRepositoryProtocol.Event
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class ShadowerMaterialRepository(
    private val coroutineScopeProvider: CoroutineScopeProviderProtocol,
    private val materialShadower: MaterialShadower,
) : ShadowerMaterialRepositoryProtocol, KoinComponent {

    private val _events = MutableSharedFlow<Event>(replay = 0)
    override val events: SharedFlow<Event> = _events

    fun process(url: String, materialElement: JsonObject) {
        coroutineScopeProvider.parser.launch {
            val shadowerMaterialSources = getKoin()
                .get<List<ShadowerMaterialSourceProtocol>>(Name.SHADOWER_SOURCE)
            shadowerMaterialSources.forEach { it.onStart(url, materialElement) }
            materialShadower.process(
                material = materialElement,
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
                                    coroutineScopeProvider.event.launch {
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
