package com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema._element.form.FormSchema

object SubsetSchema {
    object Value {
        const val label = "label"
        const val field = "${FormSchema.Component.Value.subset}field"
        const val button = "button"
        const val layoutLinear = "layout-linear"
        const val spacer = "spacer"
    }
}
