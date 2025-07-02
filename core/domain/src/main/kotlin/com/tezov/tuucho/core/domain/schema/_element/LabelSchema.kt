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

        val JsonElement.valueObject get() = this.jsonObject[Key.value]!!.jsonObject
        val JsonElement.valueObjectOrNull get() = this.jsonObject[Key.value] as? JsonObject
    }

    object Style: StyleSchema {
        object Key {
            const val fontColor = "font-color"
            const val fontSize = "font-size"
        }

        object Value

        val JsonElement.fontColor get() = this.jsonObject[Key.fontColor]!!.jsonObject
        val JsonElement.fontColorOrNull get() = this.jsonObject[Key.fontColor] as? JsonObject


        val JsonElement.fontSize get() = this.jsonObject[Key.fontSize]!!.jsonObject
        val JsonElement.fontSizeOrNull get() = this.jsonObject[Key.fontSize] as? JsonObject
    }

}
