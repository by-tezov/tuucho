package com.tezov.tuucho.core.data.repository.parser.rectifier.content

import com.tezov.tuucho.core.data.repository.di.MaterialRectifierModule
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser._system.parentIsTypeOf
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.RectifierHelper.rectifyIds
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.RectifierHelper.retrieveSubsetOrMarkUnknown
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ContentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.requireIsRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.component.inject

@OptIn(TuuchoExperimentalAPI::class)
class ContentRectifier : AbstractRectifier() {
    override val key = ContentSchema.root

    override val childProcessors: List<AbstractRectifier> by inject(
        MaterialRectifierModule.Name.Processor.CONTENT
    )

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = (path.lastSegmentIs(TypeSchema.Value.content) &&
        path.parentIsTypeOf(
            element,
            TypeSchema.Value.component
        )) ||
        super.accept(path, element)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = element
        .find(path)
        .withScope(ContentSchema::Scope)
        .apply {
            type = TypeSchema.Value.content
            val value = this.element.string.requireIsRef()
            id = JsonPrimitive(value)
        }.collect()

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = element
        .find(path)
        .withScope(ContentSchema::Scope)
        .apply {
            type = TypeSchema.Value.content
            id ?: run { id = JsonNull }
            subset = retrieveSubsetOrMarkUnknown(path, element)
        }.collect()

    override fun afterAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? {
        var valueRectified: String?
        var sourceRectified: String?
        return element
            .find(path)
            .withScope(ContentSchema::Scope)
            .takeIf {
                it
                    .rectifyIds(ContentSchema.Value.Group.common)
                    .also { (value, source) ->
                        valueRectified = value
                        sourceRectified = source
                    }
                valueRectified != null || sourceRectified != null
            }?.apply {
                id = onScope(IdSchema::Scope)
                    .apply {
                        valueRectified?.let { value = it }
                        sourceRectified?.let { source = it }
                    }.collect()
            }?.collect()
    }
}
