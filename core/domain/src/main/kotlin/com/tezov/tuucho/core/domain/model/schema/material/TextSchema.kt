package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import kotlinx.serialization.json.JsonElement

object TextSchema {

    const val root = TypeSchema.Value.text

    object Key {
        const val id = IdSchema.root
        const val type = TypeSchema.root
        const val default = "default"
    }

    object Value {
        object Group {
            const val common = "common"
        }
    }

    class Scope : OpenSchemaScope<Scope>() {
        override val root = TextSchema.root
        var self by delegate<JsonElement?>(root)

        var id by delegate<JsonElement?>(Key.id)
        var type by delegate<String?>(Key.type)
        var default by delegate<String?>(Key.default)
    }

}



