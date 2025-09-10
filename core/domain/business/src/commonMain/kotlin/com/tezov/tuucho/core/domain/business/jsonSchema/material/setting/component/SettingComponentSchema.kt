package com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object ComponentSettingSchema {

    const val root = TypeSchema.Value.Setting.prefix

    object Key {
        const val id = IdSchema.root
        const val type = TypeSchema.root
        const val urlContextual = "url-contextual"
    }

    object Value {
        object UrlContextual {
            const val suffix = "-contextual"
        }
    }

    class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument)

    open class OpenScope<T : OpenScope<T>>(argument: SchemaScopeArgument) :
        OpenSchemaScope<T>(argument) {
        final override val root = ComponentSettingSchema.root

        var id by delegate<JsonElement?>(Key.id)
        var type by delegate<String?>(Key.type)
        var urlContextual by delegate<String?>(Key.urlContextual)
    }

    object Root {
        object Key {
            const val shadower = SettingComponentShadowerSchema.root
            const val navigation = ComponentSettingNavigationSchema.root
        }

        class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument) {
            var contextualShadower by delegate<JsonObject?>(Key.shadower)
            var navigation by delegate<JsonObject?>(Key.navigation)
        }
    }

}