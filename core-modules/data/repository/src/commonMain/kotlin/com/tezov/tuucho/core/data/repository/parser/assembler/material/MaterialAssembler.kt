package com.tezov.tuucho.core.data.repository.parser.assembler.material

import com.tezov.tuucho.core.data.repository.di.assembler.AssemblerModule
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.FindAllRefOrNullFetcherProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.MaterialRectifier
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinScopeComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope

@OpenForTest
@OptIn(TuuchoExperimentalAPI::class)
class MaterialAssembler : TuuchoKoinScopeComponent {
    override val scope: Scope by lazy {
        createScope(this).also {
            with(getKoin()) {
                val materialRectifierScope = get<MaterialRectifier>().scope
                it.linkTo(materialRectifierScope)
            }
        }
    }

    private val assemblers: List<AbstractAssembler> by inject(AssemblerModule.Name.ASSEMBLERS)

    suspend fun process(
        materialObject: JsonObject,
        findAllRefOrNullFetcher: FindAllRefOrNullFetcherProtocol,
    ): JsonObject {
        val type = materialObject.withScope(TypeSchema::Scope).self
            ?: throw DataException.Default("Missing type in material $materialObject")
        return assemblers.firstOrNull { it.schemaType == type }?.let {
            val jsonObjectAssembled = it.process(
                path = ROOT_PATH,
                element = materialObject,
                findAllRefOrNullFetcher = findAllRefOrNullFetcher
            )
            jsonObjectAssembled.jsonObject
        } ?: throw DataException.Default("Missing assembler for type $type")
    }
}
