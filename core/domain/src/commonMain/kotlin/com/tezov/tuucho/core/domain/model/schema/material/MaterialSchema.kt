package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.model.schema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object MaterialSchema {

    object Key {
        const val version = "version"
        const val rootComponent = "root"
        const val components = "components"
        const val contents = "contents"
        const val styles = "styles"
        const val options = "options"

        const val texts = "texts"
        const val colors = "colors"
        const val dimensions = "dimensions"
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        var version by delegate<String?>(Key.version)

        var rootComponent by delegate<JsonObject?>(Key.rootComponent)

        var components by delegate<JsonArray?>(Key.components)
        var contents by delegate<JsonArray?>(Key.contents)
        var styles by delegate<JsonArray?>(Key.styles)
        var options by delegate<JsonArray?>(Key.options)

        var texts by delegate<JsonElement?>(Key.texts)
        var colors by delegate<JsonElement?>(Key.colors)
        var dimensions by delegate<JsonElement?>(Key.dimensions)
    }
}



