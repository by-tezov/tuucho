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

        val JsonElement.titleObject get() = this.jsonObject[Key.title]!!.jsonObject
        val JsonElement.titleObjectOrNull get() = (this as? JsonObject)?.get(Key.title) as? JsonObject

        val JsonElement.placeholderObject get() = this.jsonObject[Key.placeholder]!!.jsonObject
        val JsonElement.placeholderObjectOrNull get() = (this as? JsonObject)?.get(Key.placeholder) as? JsonObject
    }

    object Style: StyleSchema {
        object Key

        object Value
    }

}
