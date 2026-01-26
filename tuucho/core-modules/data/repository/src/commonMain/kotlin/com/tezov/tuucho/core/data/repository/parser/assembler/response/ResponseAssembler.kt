package com.tezov.tuucho.core.data.repository.parser.assembler.response

import com.tezov.tuucho.core.data.repository.di.ModuleContextData.Assembler.ScopeContext
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseRectifier
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.getAllAssociated
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinScopeComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.scope.Scope

@OpenForTest
class ResponseAssembler : TuuchoKoinScopeComponent {
    sealed class Association {
        object Processor : Association()
    }

    override val lazyScope: Lazy<Scope> = lazy {
        with(getKoin()) {
            createScope(ScopeContext.Response.value, ScopeContext.Response).also {
                val responseRectifier = get<ResponseRectifier>()
                it.linkTo(responseRectifier.lazyScope.value)
            }
        }
    }

    private val assemblers: List<AssemblerProtocol> by lazy {
        lazyScope.value.getAllAssociated(Association.Processor::class)
    }

    suspend fun process(
        context: AssemblerProtocol.Context,
        responseObject: JsonObject,
    ): JsonObject {
        val subset = responseObject.withScope(SubsetSchema::Scope).self
            ?: throw DataException.Default("Missing subset in response $responseObject")
        return assemblers.firstOrNull { it.schemaType == subset }?.let {
            val jsonObjectAssembled = it.process(
                context = context,
                path = ROOT_PATH,
                element = responseObject,
            )
            jsonObjectAssembled.jsonObject
        } ?: throw DataException.Default("Missing assembler for subset $subset")
    }
}
