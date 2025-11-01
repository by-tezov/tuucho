package com.tezov.tuucho.core.domain.business.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.page.PageSettingSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object MaterialSchema {
    object Key {
        const val pageSetting = PageSettingSchema.root
        const val rootComponent = "root"

        const val components = "components"
        const val contents = "contents"
        const val styles = "styles"
        const val options = "options"
        const val states = "states"

        const val texts = "texts"
        const val colors = "colors"
        const val dimensions = "dimensions"
        const val actions = "actions"
    }

    class Scope(
        argument: SchemaScopeArgument
    ) : OpenSchemaScope<Scope>(argument) {
        var pageSetting by delegate<JsonObject?>(Key.pageSetting)
        var rootComponent by delegate<JsonObject?>(Key.rootComponent)

        var components by delegate<JsonElement?>(Key.components)
        var contents by delegate<JsonElement?>(Key.contents)
        var styles by delegate<JsonElement?>(Key.styles)
        var options by delegate<JsonElement?>(Key.options)
        var states by delegate<JsonElement?>(Key.states)

        var texts by delegate<JsonElement?>(Key.texts)
        var colors by delegate<JsonElement?>(Key.colors)
        var dimensions by delegate<JsonElement?>(Key.dimensions)
        var actions by delegate<JsonElement?>(Key.actions)
    }
}
