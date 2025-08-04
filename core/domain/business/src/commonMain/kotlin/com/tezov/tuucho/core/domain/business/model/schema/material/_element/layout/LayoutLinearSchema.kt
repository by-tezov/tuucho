package com.tezov.tuucho.core.domain.business.model.schema.material._element.layout

import com.tezov.tuucho.core.domain.business.model.schema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.model.schema.material.ContentSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.StyleSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.SubsetSchema
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

        class Scope(argument: SchemaScopeArgument) : ContentSchema.OpenScope<Scope>(argument) {

            var items by delegate<JsonArray?>(Key.items)

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

        class Scope(argument: SchemaScopeArgument) : StyleSchema.OpenScope<Scope>(argument) {

            var orientation by delegate<String?>(Key.orientation)
            var backgroundColor by delegate<JsonObject?>(Key.backgroundColor)
            var fillMaxSize by delegate<Boolean?>(Key.fillMaxSize)
            var fillMaxWidth by delegate<Boolean?>(Key.fillMaxWidth)

        }
    }

}