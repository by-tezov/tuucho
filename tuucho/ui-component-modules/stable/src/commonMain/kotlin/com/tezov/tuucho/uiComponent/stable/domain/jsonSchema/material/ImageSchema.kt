package com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema.material.ImageSchema as DomImageSchema

object ImageSchema {
    object Component {
        object Value {
            const val subset = SubsetSchema.Value.image
        }
    }

    object Style {
        object Key {
            const val height = "height"
            const val width = "width"
            const val tintColor = "tint-color"
            const val backgroundColor = "backgound-color"
            const val padding = "padding"
        }
    }

    object Content {
        object Key {
            const val image = DomImageSchema.root
        }
    }
}
