package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.model.schema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object DimensionSchema {

    const val root = TypeSchema.Value.dimension

    object Key {
        const val id = IdSchema.root
        const val type = TypeSchema.root
        const val default = "default"
    }

    object Value {
        object Group {
            const val common = "common"
            const val font = "font"
            const val padding = "padding"
        }
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = DimensionSchema.root
        var self by delegate<JsonObject?>(root)

        var id by delegate<JsonElement?>(Key.id)
        var type by delegate<String?>(Key.type)
        var default by delegate<String?>(Key.default)

    }

}



