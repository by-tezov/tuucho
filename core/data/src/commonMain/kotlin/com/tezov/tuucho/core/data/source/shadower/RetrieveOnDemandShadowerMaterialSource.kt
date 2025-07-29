package com.tezov.tuucho.core.data.source.shadower

import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.SettingSchema
import kotlinx.serialization.json.JsonObject

class RetrieveOnDemandShadowerMaterialSource(
    private val materialNetworkSource: MaterialNetworkSource,
) : ShadowerMaterialSourceProtocol {

    override var isCancelled = false
        private set

    private lateinit var url: String
    private lateinit var map: MutableMap<String, MutableList<JsonObject>>

    override suspend fun onStart(url: String, materialElement: JsonObject) {
        if (materialElement.onScope(SettingSchema.Root::Scope).disableOnDemandShadower == true) {
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
            .apply {
                add(idScope.apply {
                    remove(IdSchema.Key.id_auto_generated)
                }.collect())
            }
    }

    override suspend fun onDone(): JsonObject? {
        map.forEach {(url, objects) ->
            val material = materialNetworkSource.retrieve(url)

            println(material) //TODO ici
        }
        return null
    }
}
