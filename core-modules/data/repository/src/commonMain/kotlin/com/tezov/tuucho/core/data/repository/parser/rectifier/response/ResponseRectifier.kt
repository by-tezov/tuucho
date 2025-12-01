package com.tezov.tuucho.core.data.repository.parser.rectifier.response

import com.tezov.tuucho.core.data.repository.di.RectifierModule.Response.Name
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinScopeComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope

@OpenForTest
@OptIn(TuuchoExperimentalAPI::class)
internal class ResponseRectifier : TuuchoKoinScopeComponent {
    override var scopeNullable: Scope? = null
    override val scope: Scope by lazy { createScope().also { scopeNullable = it } }

    private val rectifiers: List<AbstractRectifier> by inject(Name.RECTIFIERS)

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
