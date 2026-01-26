package com.tezov.tuucho.core.data.repository.parser.rectifier.material.image

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierHelper.rectifyIds
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierMatcherProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.getAllAssociated
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.isRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ImageSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import org.koin.core.scope.Scope

class ImageRectifier(
    scope: Scope
) : AbstractRectifier(scope) {
    sealed class Association {
        object Matcher : Association()

        object Processor : Association()
    }

    override val key = ImageSchema.root
    override val matchers: List<RectifierMatcherProtocol> by lazy {
        scope.getAllAssociated(Association.Matcher::class)
    }
    override val childProcessors: List<RectifierProtocol> by lazy {
        scope.getAllAssociated(Association.Processor::class)
    }

    override fun beforeAlterPrimitive(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement,
    ) = element
        .find(path)
        .withScope(ImageSchema::Scope)
        .apply {
            type = TypeSchema.Value.image
            val stringValue = this.element.string
            if (stringValue.isRef) {
                id = this.element
            } else {
                id = JsonNull
                source = stringValue
            }
        }.collect()

    override fun beforeAlterObject(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement,
    ) = element
        .find(path)
        .withScope(ImageSchema::Scope)
        .apply {
            type = TypeSchema.Value.image
            id ?: run { id = JsonNull }
        }.collect()

    override fun afterAlterObject(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement {
        return element
            .find(path)
            .withScope(ImageSchema::Scope)
            .apply {
                rectifyIds(ImageSchema.Value.Group.common)
                    .let { (valueRectified, sourceRectified) ->
                        if (valueRectified != null || sourceRectified != null) {
                            id = onScope(IdSchema::Scope)
                                .apply {
                                    valueRectified?.let { value = it }
                                    sourceRectified?.let { source = it }
                                }.collect()
                        }
                    }
                if(source != null) {
                    cacheKey = ImageSchema.cacheKey(
                        url = context.url,
                        id = onScope(IdSchema::Scope).value ?: throw DataException.Default("id can't be null, rectifier id not applied.")
                    )
                }
            }.collect()
    }
}
