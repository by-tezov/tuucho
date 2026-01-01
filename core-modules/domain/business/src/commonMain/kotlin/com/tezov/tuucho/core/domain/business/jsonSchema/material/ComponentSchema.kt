package com.tezov.tuucho.core.domain.business.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
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
        const val state = StateSchema.root
        const val message = MessageSchema.root
    }

    object Value {
        object Group {
            const val common = "common"
        }
    }

    class Scope(
        argument: SchemaScopeArgument
    ) : OpenScope<Scope>(argument)

    open class OpenScope<T : OpenScope<T>>(
        argument: SchemaScopeArgument
    ) : OpenSchemaScope<T>(argument) {
        final override val root = ""

        var id by delegate<JsonElement?>(Key.id)
        var type by delegate<String?>(Key.type)
        var subset by delegate<String?>(Key.subset)
        var content by delegate<JsonObject?>(Key.content)
        var style by delegate<JsonObject?>(Key.style)
        var option by delegate<JsonObject?>(Key.option)
        var state by delegate<JsonObject?>(Key.state)
        var setting by delegate<JsonObject?>(Key.setting)
        var message by delegate<JsonObject?>(Key.message)
    }
}
