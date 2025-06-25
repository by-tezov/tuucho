package com.tezov.tuucho.core.domain.schema._element.layout

import com.tezov.tuucho.core.domain.schema.ContentSchema

object LayoutLinearSchema : ContentSchema {

    object Key {
        const val items = "items"
    }

    object Default {
        const val subset = "layout-linear"
    }
}
