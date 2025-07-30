package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.exception.DomainException
import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.model.schema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import kotlinx.serialization.json.JsonObject

object TypeSchema {

    const val root = "type"

    object Value {
        const val component = "component"
        const val content = "content"
        const val style = "style"
        const val option = "option"
        const val state = "state"
        const val message = "message"
        const val text = "text"
        const val dimension = "dimension"
        const val color = "color"
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = TypeSchema.root
        var self by delegate<String?>(root)
    }

    val JsonObject.type
        get() = withScope(::Scope).self
            ?: throw DomainException.Default("type value is null for $this")
}




