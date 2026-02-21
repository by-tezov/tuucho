package com.tezov.tuucho.core.data.repository.parser.shadower

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class MaterialShadower(
    private val coroutineScopes: CoroutineScopesProtocol
) : TuuchoKoinComponent {
    private val componentShadower: ComponentShadower by inject()

    fun process(
        componentObject: JsonObject
    ) = flow {
        componentShadower.run { process(path = ROOT_PATH, element = componentObject) }
    }.flowOn(coroutineScopes.default.dispatcher)
}
