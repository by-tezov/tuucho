package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.parser.assembler._system.FindAllRefOrNullFetcherProtocol
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MaterialAssembler() : KoinComponent {

    private val assemblers: List<Assembler> by inject(Name.ASSEMBLERS)

    suspend fun process(
        material: JsonObject,
        findAllRefOrNullFetcher: FindAllRefOrNullFetcherProtocol
    ): JsonObject? {
        val type = material.withScope(TypeSchema::Scope).self
            ?: throw DataException.Default("Missing type in material $material")
        return assemblers.firstOrNull { it.schemaType == type }?.let {
            val jsonObjectAssembled = it.process(
                path = "".toPath(),
                element = material,
                findAllRefOrNullFetcher = findAllRefOrNullFetcher
            )
            jsonObjectAssembled.jsonObject

        } ?: throw DataException.Default("Missing assembler for type $type")
    }
}