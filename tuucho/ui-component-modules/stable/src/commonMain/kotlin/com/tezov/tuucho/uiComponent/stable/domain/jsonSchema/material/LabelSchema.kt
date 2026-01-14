package com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ContentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.StyleSchema
import kotlinx.serialization.json.JsonObject

object LabelSchema {
    object Component {
        object Value {
            const val subset = SubsetSchema.Value.label
        }
    }

    object Content {
        object Key {
            const val value = "value"
        }
    }

    object Style {
        object Key {
            const val fontColor = "font-color"
            const val fontSize = "font-size"
        }
    }
}
