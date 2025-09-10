package com.tezov.tuucho.core.domain.business.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.content.ContentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object ComponentSchema {

    object Key {
        const val id = IdSchema.root
        const val type = TypeSchema.root
        const val subset = SubsetSchema.root
        const val content = ContentSchema.root
        const val style = StyleSchema.root
        const val option = OptionSchema.root
        const val setting = ComponentSettingSchema.root
        const val state = OptionSchema.root
        const val message = MessageSchema.root
    }

    class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument)

    open class OpenScope<T : OpenScope<T>>(argument: SchemaScopeArgument) :
        OpenSchemaScope<T>(argument) {
        final override val root = ""

        var id by delegate<JsonElement?>(Key.id)
        var type by delegate<String?>(Key.type)
        var subset by delegate<String?>(Key.subset)
        var content by delegate<JsonObject?>(Key.content)
        var style by delegate<JsonObject?>(Key.style)
        var option by delegate<JsonObject?>(Key.option)
        var state by delegate<JsonObject?>(Key.option)
        var message by delegate<JsonObject?>(Key.message)

    }

    val JsonObject.contentOrNull
        get() = withScope(::Scope).content

    val JsonObject.styleOrNull
        get() = withScope(::Scope).style

    val JsonObject.optionOrNull
        get() = withScope(::Scope).option

    val JsonObject.stateOrNull
        get() = withScope(::Scope).state

    val JsonObject.messageOrNull
        get() = withScope(::Scope).message
}
