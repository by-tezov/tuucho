package com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material

object ImageSchema {
    object Component {
        object Value {
            const val subset = SubsetSchema.Value.image
        }
    }

    object Style {
        object Key {
            const val shape = "shape"
            const val height = "height"
            const val width = "width"
            const val padding = "padding"
            const val tintColor = "tint-color"
            const val backgroundColor = "backgound-color"
        }
    }

    object Content {
        object Key {
            const val value = "value"
        }
    }
}
