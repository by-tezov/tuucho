package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.database.dao.VersioningQueries
import com.tezov.tuucho.core.data.parser.assembler._system.ArgumentAssembler
import com.tezov.tuucho.core.domain._system.toPath
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialAssembler(
    private val jsonObjectQueries: JsonObjectQueries,
    private val versioningQueries: VersioningQueries,
) : KoinComponent {

    private val componentAssembler: ComponentAssembler by inject()

    suspend fun process(
        argument: ArgumentAssembler
    ): JsonElement? {
        val versioning = versioningQueries
            .find(url = argument.url) ?: return null
        versioning.rootPrimaryKey ?: return null
        val entity = jsonObjectQueries.find(versioning.rootPrimaryKey) ?: return null
        val jsonElementAssembled = componentAssembler.process(
            path = "".toPath(),
            element = entity.jsonObject,
            argument = argument
        )
        return jsonElementAssembled
    }
}