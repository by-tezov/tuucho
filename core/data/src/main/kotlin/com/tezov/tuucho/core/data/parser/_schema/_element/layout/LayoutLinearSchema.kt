package com.tezov.tuucho.core.data.parser._schema._element.layout

import com.tezov.tuucho.core.data.parser._schema.ContentSchema

object LayoutLinearSchema : ContentSchema {

    object Name {
        const val items = "items"
    }

    object Default {
        const val subset = "layout-linear"
    }
}
