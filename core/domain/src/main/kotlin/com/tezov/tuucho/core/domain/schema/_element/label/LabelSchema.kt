package com.tezov.tuucho.core.domain.schema._element.label

import com.tezov.tuucho.core.domain.schema.ContentSchema

object LabelSchema : ContentSchema {

    object Name {
        const val Key = "value"
    }

    object Value {
        const val subset = "label"
    }
}
