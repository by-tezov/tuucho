package com.tezov.tuucho.core.data.repository.parser.rectifier.response

import com.tezov.tuucho.core.data.repository.di.ModuleContextData.Rectifier.ScopeContext
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.getAllAssociated
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinScopeComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import kotlinx.serialization.json.JsonObject
import org.koin.core.scope.Scope

@OpenForTest
internal class ResponseRectifier : TuuchoKoinScopeComponent {
    sealed class Association {
        object Processor : Association()
    }

    override val lazyScope: Lazy<Scope> = lazy {
        getKoin().createScope(ScopeContext.Response.value, ScopeContext.Response)
    }

    private val rectifiers: List<RectifierProtocol> by lazy {
        lazyScope.value.getAllAssociated(Association.Processor::class)
    }

    @Suppress("RedundantSuspendModifier")
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
