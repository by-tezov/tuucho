package com.tezov.tuucho.core.domain.schema._element

import com.tezov.tuucho.core.domain.schema.ActionSchema
import com.tezov.tuucho.core.domain.schema.ContentSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject


object ButtonSchema : ContentSchema {

    object Component {
        object Key

        object Value {
            const val subset = "button"
        }
    }

    object Content: ActionSchema {
        object Key {
            const val label = LabelSchema.Component.Value.subset
            const val action = ActionSchema.Key.action
        }

        object Value

        val JsonElement.labelObject get() = this.jsonObject[Key.label]!!.jsonObject
        val JsonElement.labelObjectOrNull get() = this.jsonObject[Key.label] as? JsonObject

    }

    object Style {
        object Key

        object Value
    }

}
