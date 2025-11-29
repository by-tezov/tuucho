package com.tezov.tuucho.core.domain.business.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object StyleSchema {
    const val root = TypeSchema.Value.style

    object Key {
        const val id = IdSchema.root
        const val type = TypeSchema.root
        const val subset = SubsetSchema.root

        const val height = "height"
        const val width = "width"
    }

    object Value {
        object Group {
            const val common = "kotlin"
        }
    }

    class Scope(
        argument: SchemaScopeArgument
    ) : OpenScope<Scope>(argument)

    open class OpenScope<T : OpenScope<T>>(
        argument: SchemaScopeArgument
    ) : OpenSchemaScope<T>(argument) {
        final override val root = StyleSchema.root

        var id by delegate<JsonElement?>(Key.id)
        var type by delegate<String?>(Key.type)
        var subset by delegate<String?>(Key.subset)

        var height by delegate<JsonObject?>(Key.height)
        var width by delegate<JsonObject?>(Key.width)
    }
}
