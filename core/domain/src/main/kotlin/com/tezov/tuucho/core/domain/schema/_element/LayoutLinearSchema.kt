package com.tezov.tuucho.core.domain.schema._element

import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.schema.ComponentSchema
import com.tezov.tuucho.core.domain.schema.ContentSchema
import com.tezov.tuucho.core.domain.schema.StyleSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

object LayoutLinearSchema : ContentSchema {

    object Component: ComponentSchema {
        object Key

        object Value {
            const val subset = "layout-linear"
        }
    }

    object Content: ContentSchema {
        object Key {
            const val items = "items"
        }

        object Value

        val Map<String, JsonElement>.itemsArray get() = this[Key.items]!!.jsonArray
        val Map<String, JsonElement>.itemsArrayOrNull get() = this[Key.items] as? JsonArray
    }

    object Style: StyleSchema {
        object Key {
            const val orientation = "orientation"
        }

        object Value {
            object Orientation {
                const val vertical = "vertical"
                const val horizontal = "horizontal"
            }
        }

        val Map<String, JsonElement>.orientation get() = this[Key.orientation].string
        val Map<String, JsonElement>.orientationOrNull get() = this[Key.orientation].stringOrNull
    }

}
