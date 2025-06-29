package com.tezov.tuucho.core.domain.schema._element.label

import com.tezov.tuucho.core.domain.schema.ContentSchema

object LabelSchema : ContentSchema {

    object Component {
        object Key

        object Value {
            const val subset = "label"
        }
    }

    object Content {
        object Key {
            const val value = "value"
        }

        object Value
    }

    object Style {
        object Key {
            const val fontColor = "font-color"
            const val fontSize = "font-size"
        }

        object Value
    }

}
