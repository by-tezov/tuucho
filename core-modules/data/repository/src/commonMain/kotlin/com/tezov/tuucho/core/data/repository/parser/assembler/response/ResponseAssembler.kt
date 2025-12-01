package com.tezov.tuucho.core.data.repository.parser.assembler.response

import com.tezov.tuucho.core.data.repository.di.AssemblerModule.Response.Name
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.FindAllRefOrNullFetcherProtocol
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinScopeComponent
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope

@OpenForTest
@OptIn(TuuchoExperimentalAPI::class)
class ResponseAssembler : TuuchoKoinScopeComponent {
    override var scopeNullable: Scope? = null
    override val scope: Scope by lazy {
        createScope().also { scopeNullable = it }
    }

    private val assemblers: List<AbstractAssembler> by inject(Name.ASSEMBLERS)

    suspend fun process(
        responseObject: JsonObject,
        findAllRefOrNullFetcher: FindAllRefOrNullFetcherProtocol,
    ): JsonObject {
        TODO()
    }
}
