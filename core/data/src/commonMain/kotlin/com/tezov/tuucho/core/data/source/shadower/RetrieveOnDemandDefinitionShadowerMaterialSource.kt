package com.tezov.tuucho.core.data.source.shadower

import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.data.source.RefreshMaterialCacheLocalSource
import com.tezov.tuucho.core.domain.model.Shadower
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.SettingSchema
import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonObject

class RetrieveOnDemandDefinitionShadowerMaterialSource(
    private val coroutineContextProvider: CoroutineContextProviderProtocol,
    private val materialNetworkSource: MaterialNetworkSource,
    private val materialRectifier: MaterialRectifier,
    private val refreshMaterialCacheLocalSource: RefreshMaterialCacheLocalSource,
    private val materialAssembler: MaterialAssembler,
    private val materialDatabaseSource: MaterialDatabaseSource,
) : ShadowerMaterialSourceProtocol {

    override val type = Shadower.Type.onDemandDefinition

    override var isCancelled = false
        private set

    private lateinit var url: String
    private lateinit var map: MutableMap<String, MutableList<JsonObject>>

    override suspend fun onStart(url: String, materialElement: JsonObject) {
        if (materialElement.onScope(SettingSchema.Root::Scope).disableOnDemandDefinitionShadower == true) {
            isCancelled = true
        }
        this.url = url
        map = mutableMapOf()
    }

    override suspend fun onNext(jsonObject: JsonObject) {
        val idScope = jsonObject.onScope(IdSchema::Scope)
        idScope.source ?: return
        val url = jsonObject.onScope(SettingSchema::Scope)
            .onDemandDefinitionUrl?.replace("\${current}", url)
            ?: "$url-${SettingSchema.Value.OnDemandDefinitionUrl.default}"
        map[url] = (map[url] ?: mutableListOf())
            .apply { add(jsonObject) }
    }

    override suspend fun onDone() = flow {
        coroutineScope {
            map.map { (url, jsonObjects) ->
                async {
                    refreshTransientDatabaseCache(url)
                    jsonObjects.assembleAll()
                }
            }.awaitAll().flatten().forEach { emit(it) }
        }
    }

    private suspend fun refreshTransientDatabaseCache(
        url: String
    ) {
        val material = materialNetworkSource
            .retrieve(url)
            .let { materialRectifier.process(it) }

        //TODO should not be inserted inside the static database, do another one for transient data
        refreshMaterialCacheLocalSource.process(
            material = material,
            url = url,
            isShared = true
        )
    }

    private suspend fun List<JsonObject>.assembleAll() = mapNotNull { jsonObject ->
        materialAssembler.process(
            material = jsonObject,
            findAllRefOrNullFetcher = { from, type ->
                materialDatabaseSource.findAllRefOrNull(
                    from = from,
                    url = this@RetrieveOnDemandDefinitionShadowerMaterialSource.url,
                    type = type
                )
            }
        )
    }
}
