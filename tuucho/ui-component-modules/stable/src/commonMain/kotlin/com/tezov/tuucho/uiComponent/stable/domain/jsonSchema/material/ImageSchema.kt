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
            const val alpha = "alpha"
            const val backgroundColor = "background-color"
        }

        object Value {
            object Shape {
                const val rounded = "rounded"
                const val roundedSquare = "rounded-square"
            }
        }
    }

    object Content {
        object Key {
            const val values = "values"
            const val description = "description"
        }
    }
}
