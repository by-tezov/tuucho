package com.tezov.tuucho.core.data.parser.rectifier.setting

import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingNavigationSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingNavigationSchema.Selector
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class SettingNavigationRectifier : Rectifier() {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(SettingSchema.Root.Key.navigation)) return false
        val parent = element.find(path.parent())
        return parent.isTypeOf(TypeSchema.Value.setting)
    }

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = mutableListOf<JsonElement>().apply {
        add(element.find(path).jsonObject)
    }.let(::JsonArray)

    override fun afterAlterArray(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).jsonArray.map {
        afterAlterObject("".toPath(), it) ?: it
    }.let(::JsonArray)

    override fun afterAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? {
        var selectorRectified: JsonObject? = null
        return element.find(path).withScope(SettingNavigationSchema::Scope)
            .takeIf { scope ->
                scope.selector?.withScope(Selector::Scope)?.apply {
                    rectifySelector()?.let { selectorRectified = it }
                }
                selectorRectified != null
            }
            ?.apply { selector = selectorRectified }
            ?.collect()
    }

    private fun Selector.Scope.rectifySelector(): JsonObject? {
        val value = (element as? JsonObject)?.get(Selector.Key.value)
        if(value is JsonPrimitive) {
            this.values = JsonArray(listOf(value))
            return collect()
        }
        val values = (element as? JsonObject)?.get(Selector.Key.values)
        if(values is JsonPrimitive) {
            this.values = JsonArray(listOf(values))
            return collect()
        }
        return null
    }

}