package com.tezov.tuucho.core.domain.schema._element.button

import com.tezov.tuucho.core.domain.schema.ContentSchema


object ButtonSchema : ContentSchema {

    object Key {
        const val value = "value"
        const val action = "action"
    }

    object Value {
        const val subset = "button"
    }
}
