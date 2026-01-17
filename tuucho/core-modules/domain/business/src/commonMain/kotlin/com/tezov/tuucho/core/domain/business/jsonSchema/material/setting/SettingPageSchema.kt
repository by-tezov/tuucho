package com.tezov.tuucho.core.domain.business.jsonSchema.material.setting

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TimeToLive
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import kotlinx.serialization.json.JsonObject

object PageSettingSchema {
    const val root = TypeSchema.Value.Setting.prefix

    object Key {
        const val type = TypeSchema.root
        const val timeToLive = TimeToLive.root
    }

    class Scope(
        argument: SchemaScopeArgument
    ) : OpenSchemaScope<Scope>(argument) {
        override val root = PageSettingSchema.root

        var type by delegate<String?>(Key.type)
        var timeToLive by delegate<JsonObject?>(Key.timeToLive)
    }

}
