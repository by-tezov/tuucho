package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.model.schema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonElement

object SettingSchema {

    const val root = "setting"

    object Key {
        const val onDemandDefinitionUrl = "on-demand-definition-url"
        const val missingDefinition = "missing-definition"
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = SettingSchema.root
        var self by delegate<JsonElement?>(root)

        var onDemandDefinitionUrl by delegate<String?>(Key.onDemandDefinitionUrl)
        var missingDefinition by delegate<Boolean?>(Key.missingDefinition)
    }
}

