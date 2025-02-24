package com.tezov.tuucho.core.data.cache.parser.decoder

import com.tezov.tuucho.core.data.cache.database.Database
import com.tezov.tuucho.core.domain.model.material.ComponentModelDomain
import com.tezov.tuucho.core.domain.model.material.MaterialModelDomain
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialModelDomainDecoder(
    private val database: Database,
    private val json: Json
) : KoinComponent {

    private val componentEntityDecoder: ComponentModelDomainDecoder by inject()

    suspend fun decode(
        config: DecoderConfig
    ): MaterialModelDomain? {
        val versioning = database.versioning()
            .find(url = config.url) ?: return null
        versioning.rootPrimaryKey ?: return null
        val entity = database.jsonEntity().find(versioning.rootPrimaryKey) ?: return null

        val jsonElementDecoded = componentEntityDecoder.decode(element = entity.jsonElement, config = config)

        val componentDecoded = json.decodeFromJsonElement(
            ComponentModelDomain.PolymorphicSerializer,
            jsonElementDecoded
        )

        //println(json.encodeToJsonElement(MaterialModelDomain.serializer(), MaterialModelDomain(root = componentDecoded)))

        return MaterialModelDomain(root = componentDecoded)
    }
}