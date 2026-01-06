package com.tezov.tuucho.core.data.repository.parser.rectifier.response

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData.Rectifier.ScopeContext
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.getAllAssociated
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinScopeComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import kotlinx.serialization.json.JsonObject
import org.koin.core.scope.Scope

sealed class ResponseAssociation {
    object Rectifier : ResponseAssociation()
}

@OpenForTest
internal class ResponseRectifier : TuuchoKoinScopeComponent {
    override val scope: Scope by lazy {
        with(getKoin()) {
            createScope(ScopeContext.Response.value, ScopeContext.Response)
        }
    }

    private val rectifiers: List<RectifierProtocol> by lazy {
        scope.getAllAssociated(ResponseAssociation.Rectifier::class)
    }

    suspend fun process(
        responseObject: JsonObject
    ) = responseObject
        .withScope(::SchemaScope)
        .apply {
            responseObject.forEach { (key, element) ->
                var _element = element
                rectifiers
                    .asSequence()
                    .filter { it.key == key }
                    .forEach {
                        _element = it.process(ROOT_PATH, element)
                    }
                this[key] = _element
            }
        }.collect()
}
