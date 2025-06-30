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

        val Map<String, JsonElement>.labelObject get() = this[Key.label]!!.jsonObject
        val Map<String, JsonElement>.labelObjectOrNull get() = this[Key.label] as? JsonObject

    }

    object Style {
        object Key

        object Value
    }

}
