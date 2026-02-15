package com.tezov.tuucho.core.data.repository.parser.rectifier.material.config

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.config.ConfigSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonObject

@OpenForTest
internal class ConfigRectifier(
    private val coroutineScopes: CoroutineScopesProtocol
) {
    private val configMaterialResourceRectifier by lazy { ConfigMaterialResourceRectifier() }

    suspend fun process(
        configObject: JsonObject
    ) = coroutineScopes.default.withContext {
        configObject
            .withScope(ConfigSchema::Scope)
            .apply {
                materialResource?.let { materialResource = configMaterialResourceRectifier.process(it) }
            }.collect()
    }
}
