package com.tezov.tuucho.core.domain.business.jsonSchema.material.setting

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object SettingSchema {

    const val root = TypeSchema.Value.setting

    object Key {
        const val id = IdSchema.root
        const val type = TypeSchema.root
        const val onDemandDefinitionUrl = "on-demand-definition-url"
    }

    object Value {
        object OnDemandDefinitionUrl {
            const val default = "on-demand-definition"
        }
    }

    class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument)

    open class OpenScope<T : OpenScope<T>>(argument: SchemaScopeArgument) :
        OpenSchemaScope<T>(argument) {
        final override val root = SettingSchema.root

        var id by delegate<JsonElement?>(Key.id)
        var type by delegate<String?>(Key.type)
        var onDemandDefinitionUrl by delegate<String?>(Key.onDemandDefinitionUrl)
    }

    object Root {
        object Key {
            const val disableOnDemandDefinitionShadower = "disable-on-demand-definition-shadower"
            const val navigation = SettingNavigationSchema.root
        }

        class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument) {
            var disableOnDemandDefinitionShadower by delegate<Boolean?>(Key.disableOnDemandDefinitionShadower)
            var navigation by delegate<JsonObject?>(Key.navigation)
        }
    }

}