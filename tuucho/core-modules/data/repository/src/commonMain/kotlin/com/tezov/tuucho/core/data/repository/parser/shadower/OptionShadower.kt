package com.tezov.tuucho.core.data.repository.parser.shadower

import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser.shadower._system.ShadowerMatcherProtocol
import com.tezov.tuucho.core.data.repository.parser.shadower._system.ShadowerProtocol
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.getAllAssociated
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement

class OptionShadower : AbstractShadower() {
    sealed class Association {
        object Matcher : Association()

        object Processor : Association()
    }

    override val matchers: List<ShadowerMatcherProtocol> by lazy {
        getKoin().getAllAssociated(Association.Matcher::class)
    }

    override val childProcessors: List<ShadowerProtocol> by lazy {
        getKoin().getAllAssociated(Association.Processor::class)
    }

    override fun accept(
        path: JsonElementPath,
        element: JsonElement,
    ) = path.isTypeOf(element, TypeSchema.Value.option) || super.accept(path, element)
}
