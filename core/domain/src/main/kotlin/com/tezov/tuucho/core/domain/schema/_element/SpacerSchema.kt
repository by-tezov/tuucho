package com.tezov.tuucho.core.domain.schema._element

import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.schema.ComponentSchema
import com.tezov.tuucho.core.domain.schema.ContentSchema
import com.tezov.tuucho.core.domain.schema.StyleSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

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

        val JsonElement.weight get() = this.jsonObject[Key.weight].string
        val JsonElement.weightOrNull get() = this.jsonObject[Key.weight].stringOrNull

    }
}
