package com.tezov.tuucho.core.data.parser._schema._element.label

import com.tezov.tuucho.core.data.parser._schema.ContentSchema

object LabelSchema : ContentSchema {

    object Name {
        const val value = "value"
    }

    object Default {
        const val subset = "label"
    }
}
