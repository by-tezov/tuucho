package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.parser.assembler._system.FindAllRefOrNullFetcherProtocol
import com.tezov.tuucho.core.domain._system.toPath
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialAssembler() : KoinComponent {

    private val componentAssembler: ComponentAssembler by inject()

    suspend fun process(
        material: JsonObject,
        findAllRefOrNullFetcher: FindAllRefOrNullFetcherProtocol
    ): JsonObject? {
        val jsonObjectAssembled = componentAssembler.process(
            path = "".toPath(),
            element = material,
            findAllRefOrNullFetcher = findAllRefOrNullFetcher
        )
        return jsonObjectAssembled.jsonObject
    }
}