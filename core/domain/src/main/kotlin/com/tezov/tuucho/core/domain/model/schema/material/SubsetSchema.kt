package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope

object SubsetSchema {

    const val root = "subset"

    object Value {
        const val unknown = "unknown"
        const val label = "label"
        const val field = "field"
        const val button = "button"
        const val layoutLinear = "layout-linear"
        const val spacer = "spacer"

    }

    class Scope : OpenSchemaScope<Scope>() {
        override val root = SubsetSchema.root
        var self by delegate<String?>(root)
    }

}



