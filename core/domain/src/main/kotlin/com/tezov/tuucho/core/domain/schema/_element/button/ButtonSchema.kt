package com.tezov.tuucho.core.domain.schema._element.button

import com.tezov.tuucho.core.domain.schema.ContentSchema


object ButtonSchema : ContentSchema {

    object Component {
        object Key

        object Value {
            const val subset = "button"
        }
    }

    object Content {
        object Key {
            const val value = "value"
            const val action = "action"
        }

        object Value
    }

    object Style {
        object Key {
            const val label = "label"
        }

        object Value
    }

}
