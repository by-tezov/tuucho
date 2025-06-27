package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.domain._system.toPath
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialAssembler(
    private val database: Database,
    private val jsonConverter: Json
) : KoinComponent {

    private val componentAssembler: ComponentAssembler by inject()

    suspend fun process(
        extraData: ExtraDataAssembler
    ): JsonObject? {
        val versioning = database.versioning()
            .find(url = extraData.url) ?: return null
        versioning.rootPrimaryKey ?: return null
        val entity = database.jsonEntity().find(versioning.rootPrimaryKey) ?: return null
        val jsonElementAssembled = componentAssembler.process(
            path = "".toPath(),
            element = entity.jsonObject,
            extraData = extraData
        )
        return jsonElementAssembled as? JsonObject
    }
}