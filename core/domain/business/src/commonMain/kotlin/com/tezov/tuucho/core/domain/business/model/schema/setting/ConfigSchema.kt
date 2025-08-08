package com.tezov.tuucho.core.domain.business.model.schema.setting

import com.tezov.tuucho.core.domain.business.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.model.schema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

object ConfigSchema {

    object Key {
        const val preload = "preload"
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        var preload by delegate<JsonObject?>(Key.preload)
    }

    object Preload {
        const val root = ConfigSchema.Key.preload

        object Key {
            const val subs = "subs"
            const val templates = "templates"
            const val pages = "pages"
        }

        class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
            override val root = Preload.root

            var subs by delegate<JsonArray?>(Key.subs)
            var templates by delegate<JsonArray?>(Key.templates)
            var pages by delegate<JsonArray?>(Key.pages)
        }
    }


    object MaterialItem {
        object Key {
            const val version = "version"
            const val url = "url"
        }

        class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
            var version by delegate<String?>(Key.version)
            var url by delegate<String?>(Key.url)
        }
    }

}

