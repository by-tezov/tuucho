package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.model.schema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonObject

object SettingSchema {

    const val root = "setting"

    object Key {
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
        var self by delegate<JsonObject?>(root)

        var onDemandDefinitionUrl by delegate<String?>(Key.onDemandDefinitionUrl)
    }

    object Root {
        object Key {
            const val disableOnDemandDefinitionShadower = "disable-on-demand-definition-shadower"
        }

        class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument) {
            var disableOnDemandDefinitionShadower by delegate<Boolean?>(Key.disableOnDemandDefinitionShadower)
        }
    }

}

