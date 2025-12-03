package com.tezov.tuucho.core.data.repository.parser.assembler.response

import com.tezov.tuucho.core.data.repository.di.assembler.AssemblerModule
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.FindAllRefOrNullFetcherProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseRectifier
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinScopeComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
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
class ResponseAssembler : TuuchoKoinScopeComponent {
    override val scope: Scope by lazy {
        createScope().also {
            with(getKoin()) {
                val responseRectifierScope = get<ResponseRectifier>().scope
                it.linkTo(responseRectifierScope)
            }
        }
    }

    private val assemblers: List<AbstractAssembler> by inject(AssemblerModule.Name.ASSEMBLERS)

    suspend fun process(
        responseObject: JsonObject,
        findAllRefOrNullFetcher: FindAllRefOrNullFetcherProtocol,
    ): JsonObject {
        val subset = responseObject.withScope(SubsetSchema::Scope).self
            ?: throw DataException.Default("Missing subset in response $responseObject")
        return assemblers.firstOrNull { it.schemaType == subset }?.let {
            val jsonObjectAssembled = it.process(
                path = ROOT_PATH,
                element = responseObject,
                findAllRefOrNullFetcher = findAllRefOrNullFetcher
            )
            jsonObjectAssembled.jsonObject
        } ?: throw DataException.Default("Missing assembler for subset $subset")
    }
}
