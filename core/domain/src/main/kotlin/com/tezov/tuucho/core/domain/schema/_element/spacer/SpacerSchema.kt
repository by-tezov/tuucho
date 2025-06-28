package com.tezov.tuucho.core.domain.schema._element.spacer

import com.tezov.tuucho.core.domain.schema.ContentSchema
import com.tezov.tuucho.core.domain.schema.StyleSchema

object SpacerSchema : ContentSchema {

    object Component {
        object Key

        object Value {
            const val subset = "spacer"
        }
    }

    object Content {
        object Key

        object Value
    }

    object Style {
        object Key {
            const val height = StyleSchema.Key.height
            const val width = StyleSchema.Key.width
            const val weight = "weight"
        }

        object Value
    }
}
