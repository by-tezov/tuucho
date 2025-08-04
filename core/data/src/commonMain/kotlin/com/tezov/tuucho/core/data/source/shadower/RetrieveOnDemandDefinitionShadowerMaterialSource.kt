package com.tezov.tuucho.core.data.source.shadower

import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.data.database.type.Visibility
import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.data.source.RefreshMaterialCacheLocalSource
import com.tezov.tuucho.core.domain.business.model.schema._system.onScope
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.SettingSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.Shadower
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonObject

class RetrieveOnDemandDefinitionShadowerMaterialSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialNetworkSource: MaterialNetworkSource,
    private val materialRectifier: MaterialRectifier,
    private val refreshMaterialCacheLocalSource: RefreshMaterialCacheLocalSource,
    private val materialAssembler: MaterialAssembler,
    private val materialDatabaseSource: MaterialDatabaseSource,
) : ShadowerMaterialSourceProtocol {

    override val type = Shadower.Type.onDemandDefinition

    override var isCancelled = false
        private set

    private lateinit var urlOrigin: String
    private lateinit var map: MutableMap<String, MutableList<JsonObject>>

    override suspend fun onStart(url: String, materialElement: JsonObject) {
        if (materialElement.onScope(SettingSchema.Root::Scope).disableOnDemandDefinitionShadower == true) {
            isCancelled = true
            return
        }
        this.urlOrigin = url
        map = mutableMapOf()
    }

    override suspend fun onNext(jsonObject: JsonObject) {
        val idScope = jsonObject.onScope(IdSchema::Scope)
        idScope.source ?: return
        val url = jsonObject.onScope(SettingSchema::Scope)
            .onDemandDefinitionUrl?.replace("\${current}", urlOrigin)
            ?: "$urlOrigin-${SettingSchema.Value.OnDemandDefinitionUrl.default}"
        map[url] = (map[url] ?: mutableListOf())
            .apply { add(jsonObject) }
    }

    override suspend fun onDone() = flow {
        coroutineScope {
            map.map { (url, jsonObjects) ->
                async {
                    refreshTransientDatabaseCache(url)
                    jsonObjects.assembleAll(url)
                }
            }
        }.awaitAll()
            .flatten()
            .forEach { emit(it) }
    }

    private suspend fun refreshTransientDatabaseCache(
        url: String
    ) {
        val material = coroutineScopes.onNetwork {
            materialNetworkSource.retrieve(url)
        }.let { materialRectifier.process(it) }
        refreshMaterialCacheLocalSource.process(
            materialObject = material,
            url = url,
            visibility = Visibility.Local,
            lifetime = Lifetime.Transient(
                urlOrigin = urlOrigin
            )
        )
    }

    private suspend fun List<JsonObject>.assembleAll(url: String) = mapNotNull { jsonObject ->
        materialAssembler.process(
            materialObject = jsonObject,
            findAllRefOrNullFetcher = { from, type ->
                coroutineScopes.onDatabase {
                    materialDatabaseSource.findAllRefOrNull(
                        from = from,
                        url = url,
                        urlOrigin = urlOrigin,
                        type = type
                    )
                }
            }
        )
    }
}
