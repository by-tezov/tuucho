package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema._system.Schema.Companion.schema
import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.inject

class ComponentRectifier : Rectifier() {

    override val matchers: List<MatcherProtocol> by inject(
        Name.Matcher.COMPONENT
    )
    override val childProcessors: List<Rectifier> by inject(
        Name.Processor.COMPONENT
    )

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = element.find(path).schema().withScope(ComponentSchema::Scope).apply {
        type = TypeSchema.Value.component
        id ?: run { id = JsonNull }
    }.collect()

    override fun beforeAlterArray(path: JsonElementPath, element: JsonElement) =
        with(element.find(path).jsonArray) {
            JsonArray(map { process("".toPath(), it) })
        }

}