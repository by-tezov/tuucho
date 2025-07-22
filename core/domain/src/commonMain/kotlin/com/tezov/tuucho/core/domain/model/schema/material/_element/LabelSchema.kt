package com.tezov.tuucho.core.domain.model.schema.material._element

import com.tezov.tuucho.core.domain.model.schema.material.ContentSchema
import com.tezov.tuucho.core.domain.model.schema.material.StyleSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import kotlinx.serialization.json.JsonObject

object LabelSchema {

    object Component {
        object Value {
            const val subset = SubsetSchema.Value.label
        }

    }

    object Content {
        object Key {
            const val value = "value"
        }

        class Scope : ContentSchema.OpenScope<Scope>() {

            var value by delegate<JsonObject?>(Key.value)

        }
    }

    object Style {
        object Key {
            const val fontColor = "font-color"
            const val fontSize = "font-size"
        }

        class Scope : StyleSchema.OpenScope<Scope>() {

            var fontColor by delegate<JsonObject?>(Key.fontColor)
            var fontSize by delegate<JsonObject?>(Key.fontSize)

        }

    }

}
