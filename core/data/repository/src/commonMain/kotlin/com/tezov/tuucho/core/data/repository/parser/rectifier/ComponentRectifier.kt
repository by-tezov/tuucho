package com.tezov.tuucho.core.data.repository.parser.rectifier

import com.tezov.tuucho.core.data.repository.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ComponentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.inject

class ComponentRectifier : AbstractRectifier() {

    override val matchers: List<MatcherRectifierProtocol> by inject(
        Name.Matcher.COMPONENT
    )
    override val childProcessors: List<AbstractRectifier> by inject(
        Name.Processor.COMPONENT
    )

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = element.find(path).withScope(ComponentSchema::Scope).apply {
        type = TypeSchema.Value.component
        id ?: run { id = JsonNull }
    }.collect()

    override fun beforeAlterArray(path: JsonElementPath, element: JsonElement) =
        with(element.find(path).jsonArray) {
            JsonArray(map { process("".toPath(), it) })
        }

}