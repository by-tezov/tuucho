package com.tezov.tuucho.core.data.repository

import com.tezov.tuucho.core.data.di.MaterialRepositoryModule.Name
import com.tezov.tuucho.core.data.parser.shadower.MaterialShadower
import com.tezov.tuucho.core.data.parser.shadower._system.JsonObjectConsumerProtocol
import com.tezov.tuucho.core.data.source.shadower.ShadowerMaterialSourceProtocol
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

class ShadowerMaterialRepository(
    private val materialShadower: MaterialShadower,
) : KoinComponent {

    private val _events = MutableSharedFlow<JsonObject>(replay = 0)
    val events: SharedFlow<JsonObject> = _events

    suspend fun process(url: String, materialElement: JsonObject) {
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
                        .forEach { jsonObject ->
                            jsonObject.onDone()?.let { _events.emit(it) }
                        }
                }
            }
        )
    }
}
