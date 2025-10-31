package com.tezov.tuucho.core.data.repository.parser.assembler

import com.tezov.tuucho.core.data.repository.di.MaterialAssemblerModule.Name
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.parser.assembler._system.FindAllRefOrNullFetcherProtocol
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OpenForTest
class MaterialAssembler() : TuuchoKoinComponent {

    private val assemblers: List<AbstractAssembler> by inject(Name.ASSEMBLERS)

    suspend fun process(
        materialObject: JsonObject,
        findAllRefOrNullFetcher: FindAllRefOrNullFetcherProtocol,
    ): JsonObject? {
        val type = materialObject.withScope(TypeSchema::Scope).self
            ?: throw DataException.Default("Missing type in material $materialObject")
        return assemblers.firstOrNull { it.schemaType == type }?.let {
            val jsonObjectAssembled = it.process(
                path = "".toPath(),
                element = materialObject,
                findAllRefOrNullFetcher = findAllRefOrNullFetcher
            )
            jsonObjectAssembled.jsonObject

        } ?: throw DataException.Default("Missing assembler for type $type")
    }
}