package com.tezov.tuucho.core.domain.schema._element

import com.tezov.tuucho.core.domain.schema.ComponentSchema
import com.tezov.tuucho.core.domain.schema.ContentSchema
import com.tezov.tuucho.core.domain.schema.StyleSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

object LabelSchema : ContentSchema {

    object Component: ComponentSchema {
        object Key

        object Value {
            const val subset = "label"
        }
    }

    object Content: ContentSchema {
        object Key {
            const val value = "value"
        }

        object Value

        val Map<String, JsonElement>.valueObject get() = this[Key.value]!!.jsonObject
        val Map<String, JsonElement>.valueObjectOrNull get() = this[Key.value] as? JsonObject
    }

    object Style: StyleSchema {
        object Key {
            const val fontColor = "font-color"
            const val fontSize = "font-size"
        }

        object Value

        val Map<String, JsonElement>.fontColor get() = this[Key.fontColor]!!.jsonObject
        val Map<String, JsonElement>.fontColorOrNull get() = this[Key.fontColor] as? JsonObject


        val Map<String, JsonElement>.fontSize get() = this[Key.fontSize]!!.jsonObject
        val Map<String, JsonElement>.fontSizeOrNull get() = this[Key.fontSize] as? JsonObject
    }

}
