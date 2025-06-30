package com.tezov.tuucho.core.domain.schema._element

import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.schema.ComponentSchema
import com.tezov.tuucho.core.domain.schema.ContentSchema
import com.tezov.tuucho.core.domain.schema.StyleSchema
import kotlinx.serialization.json.JsonElement

object SpacerSchema : ContentSchema {

    object Component: ComponentSchema {
        object Key

        object Value {
            const val subset = "spacer"
        }
    }

    object Content: ContentSchema {
        object Key

        object Value
    }

    object Style: StyleSchema {
        object Key {
            const val weight = "weight"
        }

        object Value

        val Map<String, JsonElement>.weight get() = this[Key.weight].string
        val Map<String, JsonElement>.weightOrNull get() = this[Key.weight].stringOrNull

    }
}
