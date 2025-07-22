package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object ContentSchema {

    const val root = TypeSchema.Value.content

    object Key {
        const val id = IdSchema.root
        const val type = TypeSchema.root
        const val subset = SubsetSchema.root
    }

    class Scope : OpenScope<Scope>()

    open class OpenScope<T : OpenScope<T>> : OpenSchemaScope<T>() {
        final override val root = ContentSchema.root
        var self by delegate<JsonObject?>(root)

        var id by delegate<JsonElement?>(Key.id)
        var type by delegate<String?>(Key.type)
        var subset by delegate<String?>(Key.subset)

    }

}
