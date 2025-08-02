package com.tezov.tuucho.core.domain.model.schema.material

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.model.schema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.model.schema.material._element.form.FormSchema

object SubsetSchema {

    const val root = "subset"

    object Value {
        const val unknown = "unknown"
        const val label = "label"
        const val field = "${FormSchema.Component.Value.subset}field"
        const val button = "button"
        const val layoutLinear = "layout-linear"
        const val spacer = "spacer"

    }

    class Scope(argument: SchemaScopeArgument) : OpenSchemaScope<Scope>(argument) {
        override val root = SubsetSchema.root
        var self by delegate<String?>(root)
    }

}



