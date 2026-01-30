package com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material

object ButtonSchema {
    object Component {
        object Value {
            const val subset = SubsetSchema.Value.button
        }
    }

    object Content {
        object Key {
            const val label = LabelSchema.Component.Value.subset
            const val action = "action"
        }
    }
}
