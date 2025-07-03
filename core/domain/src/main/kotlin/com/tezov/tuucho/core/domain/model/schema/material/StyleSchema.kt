package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
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

    class Scope : OpenScope<Scope>()

    open class OpenScope<T : OpenScope<T>> : OpenSchemaScope<T>() {
        final override val root = StyleSchema.root
        var self by delegate<JsonObject?>(root)

        var id by delegate<JsonElement?>(Key.id)
        var type by delegate<String?>(Key.type)
        var subset by delegate<String?>(Key.subset)

        var height by delegate<JsonObject?>(Key.height)
        var width by delegate<JsonObject?>(Key.width)

    }
}



