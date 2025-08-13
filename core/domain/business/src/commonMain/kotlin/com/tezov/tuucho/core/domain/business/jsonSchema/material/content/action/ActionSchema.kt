package com.tezov.tuucho.core.domain.business.jsonSchema.material.content.action

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument

object ActionSchema {

    const val root = "action"

    object Key {
        const val value = "value"
    }

    class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument)

    open class OpenScope<T : OpenScope<T>>(argument: SchemaScopeArgument) :
        OpenSchemaScope<T>(argument) {
        override val root = ActionSchema.root

        var value by delegate<String?>(Key.value)
    }
}