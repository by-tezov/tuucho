package com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.layout

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ContentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.StyleSchema
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.SubsetSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

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
    }

    object Style {
        object Key {
            const val orientation = "orientation"
            const val backgroundColor = "background-color"
            const val fillMaxSize = "fill-max-size"
            const val fillMaxWidth = "fill-max-width"
        }

        object Value {
            object Orientation {
                const val vertical = "vertical"
                const val horizontal = "horizontal"
            }
        }
    }
}
