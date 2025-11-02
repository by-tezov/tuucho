package com.tezov.tuucho.core.domain.business.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormSchema

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

    class Scope(
        argument: SchemaScopeArgument
    ) : OpenSchemaScope<Scope>(argument) {
        override val root = SubsetSchema.root
        var self by delegate<String?>(root)
    }
}
