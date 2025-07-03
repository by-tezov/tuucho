package com.tezov.tuucho.core.domain.model.schema.material._element

import com.tezov.tuucho.core.domain.model.schema.material.ContentSchema
import com.tezov.tuucho.core.domain.model.schema.material.StyleSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import kotlinx.serialization.json.JsonArray

object LayoutLinearSchema {

    object Component {
        object Value {
            const val subset = SubsetSchema.Value.layoutLinear
        }
    }

    object Content {
        object Key {
            const val items = "items"
        }

        class Scope : ContentSchema.OpenScope<Scope>() {

            var items by delegate<JsonArray?>(Key.items)

        }
    }

    object Style {
        object Key {
            const val orientation = "orientation"
        }

        object Value {
            object Orientation {
                const val vertical = "vertical"
                const val horizontal = "horizontal"
            }
        }

        class Scope : StyleSchema.OpenScope<Scope>() {

            var orientation by delegate<String?>(Key.orientation)

        }
    }

}
