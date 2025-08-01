package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name

import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser._system.parentIsTypeOf
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.findOrNull
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema._system.withScope

import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema.requireIsRef
import com.tezov.tuucho.core.domain.model.schema.material.OptionSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.inject

class OptionRectifier : Rectifier() {

    override val matchers: List<MatcherRectifierProtocol> by inject(
        Name.Matcher.OPTION
    )

    override val childProcessors: List<Rectifier> by inject(
        Name.Processor.OPTION
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = (path.lastSegmentIs(TypeSchema.Value.option) && path.parentIsTypeOf(
        element, TypeSchema.Value.component
    )) || super.accept(path, element)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).withScope(OptionSchema::Scope).apply {
        type = TypeSchema.Value.option
        val value = this.element.string.requireIsRef()
        id = JsonPrimitive(value)
    }.collect()

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = element.find(path).withScope(OptionSchema::Scope).apply {
        type = TypeSchema.Value.option
        id ?: run { id = JsonNull }
        subset = retrieveSubsetOrMarkUnknown(path, element)
    }.collect()

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ) = with(element.find(path).jsonArray) {
        JsonArray(map { process("".toPath(), it) })
    }

    private fun retrieveSubsetOrMarkUnknown(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.findOrNull(path.parent())
        ?.withScope(ComponentSchema::Scope)?.subset
        ?: SubsetSchema.Value.unknown
}