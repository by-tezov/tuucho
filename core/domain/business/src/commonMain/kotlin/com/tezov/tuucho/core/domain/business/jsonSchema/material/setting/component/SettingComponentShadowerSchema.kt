package com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema.OpenScope
import kotlinx.serialization.json.JsonObject

object SettingComponentShadowerSchema {

    const val root = "shadower"

    object Key {
        const val navigateForward = "navigate-forward"
        const val navigateBackward = "navigate-backward"
        // implicit object retrieve with get Shadower.Type
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = ComponentSettingSchema.root

        var navigateForward by delegate<JsonObject?>(Key.navigateForward)
        var navigateBackward by delegate<JsonObject?>(Key.navigateBackward)
    }

    object Contextual {
        object Key {
            const val url = "url"
        }

        class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument) {
            // implicit object retrieve with get TypeSchema.Value (component, content, text)
            var url by delegate<JsonObject?>(Key.url)
        }
    }

    object Navigate {
        object Key {
            const val waitDoneToRender = "wait-done-to-render"
            const val enable = "enable"
        }

        class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument) {
            var waitDoneToRender by delegate<Boolean?>(Key.waitDoneToRender)
            var enable by delegate<Boolean?>(Key.enable)
        }
    }

}