package com.tezov.tuucho.core.domain.schema._element.button

import com.tezov.tuucho.core.domain.schema.ContentSchema
import com.tezov.tuucho.core.domain.schema._element.label.LabelSchema


object ButtonSchema : ContentSchema {

    object Component {
        object Key

        object Value {
            const val subset = "button"
        }
    }

    object Content {
        object Key {
            const val label = LabelSchema.Component.Value.subset
            const val action = "action"
        }

        object Value
    }

    object Style {
        object Key

        object Value
    }

}
