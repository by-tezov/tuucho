package com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.page

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import kotlinx.serialization.json.JsonObject

object PageSettingSchema {

    const val root = TypeSchema.Value.Setting.prefix

    object Key {
        const val type = TypeSchema.root
        const val ttl = Ttl.root
    }

    class Scope(argument: SchemaScopeArgument) :  OpenSchemaScope<Scope>(argument) {
        override val root = PageSettingSchema.root

        var type by delegate<String?>(Key.type)
        var ttl by delegate<JsonObject?>(Key.ttl)
    }

    object Ttl {
        const val root = "ttl"

        object Key {
            const val strategy = "strategy"
            const val transientValue = "transient-value"
        }

        object Value {
            object Strategy {
                const val transient = "transient"
                const val singleUse = "single-use"
            }

        }


        class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
            override val root = Ttl.root

            var strategy by delegate<String?>(Key.strategy)
            var transientValue by delegate<String?>(Key.transientValue)
        }
    }

}