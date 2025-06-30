package com.tezov.tuucho.core.domain.schema._element

import com.tezov.tuucho.core.domain.schema.ComponentSchema
import com.tezov.tuucho.core.domain.schema.ContentSchema
import com.tezov.tuucho.core.domain.schema.StyleSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

object FieldSchema : ContentSchema {

    object Component: ComponentSchema {
        object Key

        object Value {
            const val subset = "field"
        }
    }

    object Content: ContentSchema {
        object Key {
            const val title = "title"
            const val placeholder = "placeholder"
            const val messageError = "message-error"
        }

        object Value

        val Map<String, JsonElement>.titleObject get() = this[Key.title]!!.jsonObject
        val Map<String, JsonElement>.titleObjectOrNull get() = this[Key.title] as? JsonObject

        val Map<String, JsonElement>.placeholderObject get() = this[Key.placeholder]!!.jsonObject
        val Map<String, JsonElement>.placeholderObjectOrNull get() = this[Key.placeholder] as? JsonObject
    }

    object Style: StyleSchema {
        object Key

        object Value
    }

}
