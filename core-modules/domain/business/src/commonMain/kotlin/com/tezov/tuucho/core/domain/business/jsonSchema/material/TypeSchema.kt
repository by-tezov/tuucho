package com.tezov.tuucho.core.domain.business.jsonSchema.material

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import kotlinx.serialization.json.JsonObject

object TypeSchema {
    const val root = "type"

    object Value {
        const val component = "component"

        object Setting {
            const val prefix = "setting"
            const val component = "$prefix-component"
            const val page = "$prefix-page"
        }

        const val content = "content"
        const val style = "style"
        const val option = "option"

        const val text = "text"
        const val dimension = "dimension"
        const val color = "color"

        const val action = "action"

        const val state = "state"
        const val message = "message"
    }

    class Scope(
        argument: SchemaScopeArgument
    ) : OpenSchemaScope<Scope>(argument) {
        override val root = TypeSchema.root
        var self by delegate<String?>(root)
    }

    val JsonObject.type
        get() = withScope(::Scope).self
            ?: throw DomainException.Default("type value is null for $this")
}
