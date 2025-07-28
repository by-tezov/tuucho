package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.model.schema._system.SchemaScopeArgument

object TypeSchema {

    const val root = "type"

    object Value {
        const val component = "component"
        const val content = "content"
        const val style = "style"
        const val option = "option"
        const val text = "text"
        const val dimension = "dimension"
        const val color = "color"
    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = TypeSchema.root
        var self by delegate<String?>(root)
    }
}




