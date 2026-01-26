package com.tezov.tuucho.core.data.repository.parser.rectifier.material.content

import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ContentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.addGroup
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.requireIsRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema.Key
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class ContentsRectifier(
    scope: Scope
) : AbstractRectifier(scope) {
    override val key = Key.contents
    private val contentRectifier: ContentRectifier by inject()

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList {
        element.find(path).jsonObject.forEach { (group, contents) ->
            contents.jsonObject.forEach { (key, content) ->
                when (content) {
                    is JsonPrimitive -> alterPrimitive(key, group, content)
                    is JsonObject -> alterObject(key, group, content)
                    else -> error("type not managed")
                }.let(::add)
            }
        }
    }.let(::JsonArray)

    private fun alterPrimitive(
        key: String,
        group: String,
        jsonPrimitive: JsonPrimitive,
    ) = jsonPrimitive
        .withScope(ContentSchema::Scope)
        .apply {
            val stringValue = element.string.requireIsRef()
            id = onScope(IdSchema::Scope)
                .apply {
                    value = key.addGroup(group)
                    source = stringValue
                }.collect()
        }.collect()

    private fun alterObject(
        key: String,
        group: String,
        jsonObject: JsonObject,
    ) = jsonObject
        .withScope(ContentSchema::Scope)
        .apply {
            id = onScope(IdSchema::Scope)
                .apply {
                    when (val id = id) {
                        is JsonNull, null -> {
                            value = key.addGroup(group)
                        }

                        is JsonPrimitive -> {
                            source = id.stringOrNull?.requireIsRef()
                            value = key.addGroup(group)
                        }

                        is JsonObject -> {
                            source ?: run { source = value?.requireIsRef() }
                            value = key.addGroup(group)
                        }

                        else -> {
                            error("type not managed")
                        }
                    }
                }.collect()
        }.collect()

    override fun afterAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ) = element
        .find(path)
        .jsonArray
        .map {
            contentRectifier.process(ROOT_PATH, it)
        }.let(::JsonArray)
}
