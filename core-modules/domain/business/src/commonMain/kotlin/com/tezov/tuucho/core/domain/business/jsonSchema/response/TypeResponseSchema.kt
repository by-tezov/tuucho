package com.tezov.tuucho.core.domain.business.jsonSchema.response

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import kotlinx.serialization.json.JsonObject

object TypeResponseSchema {

    const val root = "type"

    object Value {
        const val form = "form"
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = TypeResponseSchema.root
        var self by delegate<String?>(root)
    }

    val JsonObject.type
        get() = withScope(::Scope).self
            ?: throw DomainException.Default("type value is null for $this")
}




