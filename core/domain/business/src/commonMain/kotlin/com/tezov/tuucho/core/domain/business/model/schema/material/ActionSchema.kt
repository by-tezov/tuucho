package com.tezov.tuucho.core.domain.business.model.schema.material

import com.tezov.tuucho.core.domain.business.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.model.schema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object ActionSchema {

    const val root = "action"

    object Key {
        const val value = "value"
        const val params = "params"
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = ActionSchema.root
        var self by delegate<JsonElement?>(root)

        var value by delegate<String?>(Key.value)
        var params by delegate<JsonObject?>(Key.params)

    }

}



