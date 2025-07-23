package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries.Companion.jsonObject
import com.tezov.tuucho.core.data.database.dao.VersioningQueries.Companion.versioning
import com.tezov.tuucho.core.domain._system.toPath
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialAssembler(
    private val database: Database
) : KoinComponent {

    private val componentAssembler: ComponentAssembler by inject()

    suspend fun process(
        extraData: ExtraDataAssembler
    ): JsonElement? {
        val versioning = database.versioning()
            .find(url = extraData.url) ?: return null
        versioning.rootPrimaryKey ?: return null
        val entity = database.jsonObject().find(versioning.rootPrimaryKey) ?: return null
        val jsonElementAssembled = componentAssembler.process(
            path = "".toPath(),
            element = entity.jsonObject,
            extraData = extraData
        )
        return jsonElementAssembled
    }
}