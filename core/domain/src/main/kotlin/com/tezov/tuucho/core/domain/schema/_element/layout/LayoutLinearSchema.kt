package com.tezov.tuucho.core.domain.schema._element.layout

import com.tezov.tuucho.core.domain.schema.ContentSchema

object LayoutLinearSchema : ContentSchema {

    object Component {
        object Key

        object Value {
            const val subset = "layout-linear"
        }
    }

    object Content {
        object Key {
            const val items = "items"
        }

        object Value
    }

    object Style {
        object Key {
            const val orientation = "orientation"
        }

        object Value {
            object Orientation {
                const val vertical = "vertical"
                const val horizontal = "horizontal"
            }
        }
    }

}
