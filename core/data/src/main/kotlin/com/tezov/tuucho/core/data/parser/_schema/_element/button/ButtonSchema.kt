package com.tezov.tuucho.core.data.parser._schema._element.button

import com.tezov.tuucho.core.data.parser._schema.ContentSchema

object ButtonSchema : ContentSchema {

    object Name {
        const val value = "value"
    }

    object Default {
        const val subset = "button"
    }
}
