package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser._system.parentIsTypeOf
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ComponentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.requireIsRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.StyleSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.findOrNull
import com.tezov.tuucho.core.domain.tool.json.string
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.inject

class StyleRectifier : Rectifier() {

    override val matchers: List<MatcherRectifierProtocol> by inject(
        Name.Matcher.STYLE
    )

    override val childProcessors: List<Rectifier> by inject(
        Name.Processor.STYLE
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement,
    ) = (path.lastSegmentIs(TypeSchema.Value.style) && path.parentIsTypeOf(
        element, TypeSchema.Value.component
    )) || super.accept(path, element)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).withScope(StyleSchema::Scope).apply {
        type = TypeSchema.Value.style
        val value = this.element.string.requireIsRef()
        id = JsonPrimitive(value)
    }.collect()

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).withScope(StyleSchema::Scope).apply {
        type = TypeSchema.Value.style
        id ?: run { id = JsonNull }
        subset = retrieveSubsetOrMarkUnknown(path, element)
    }.collect()

    override fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement,
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