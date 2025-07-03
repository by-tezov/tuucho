package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object ActionSchema {

    const val root = "action"

    object Key {
        const val value = "value"
        const val params = "params"
    }

    class Scope : OpenSchemaScope<Scope>() {
        override val root = ActionSchema.root
        var self by delegate<JsonElement?>(root)

        var value by delegate<String?>(Key.value)
        var params by delegate<JsonObject?>(Key.params)

    }

}



