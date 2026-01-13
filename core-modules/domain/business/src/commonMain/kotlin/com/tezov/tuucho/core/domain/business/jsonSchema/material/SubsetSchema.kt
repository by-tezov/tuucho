package com.tezov.tuucho.core.domain.business.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument

object SubsetSchema {
    const val root = "subset"

    object Value {
        const val unknown = "unknown"
    }

    class Scope(
        argument: SchemaScopeArgument
    ) : OpenSchemaScope<Scope>(argument) {
        override val root = SubsetSchema.root
        var self by delegate<String?>(root)
    }
}
