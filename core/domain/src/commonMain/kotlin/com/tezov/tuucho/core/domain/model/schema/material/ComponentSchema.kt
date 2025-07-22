package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object ComponentSchema {

    object Key {
        const val id = IdSchema.root
        const val type = TypeSchema.root
        const val subset = SubsetSchema.root
        const val content = ContentSchema.root
        const val style = StyleSchema.root
        const val option = OptionSchema.root
    }

    class Scope : OpenScope<Scope>()

    open class OpenScope<T : OpenScope<T>> : OpenSchemaScope<T>() {
        final override val root = ""
        var self by delegate<JsonObject?>(root)

        var id by delegate<JsonElement?>(Key.id)
        var type by delegate<String?>(Key.type)
        var subset by delegate<String?>(Key.subset)
        var content by delegate<JsonObject?>(Key.content)
        var style by delegate<JsonObject?>(Key.style)
        var option by delegate<JsonObject?>(Key.option)

    }
}
