@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.jsonSchema.material._element

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ContentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.StyleSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
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

        class Scope(
            argument: SchemaScopeArgument
        ) : ContentSchema.OpenScope<Scope>(argument) {
            var value by delegate<JsonObject?>(Key.value)
        }
    }

    object Style {
        object Key {
            const val fontColor = "font-color"
            const val fontSize = "font-size"
        }

        class Scope(
            argument: SchemaScopeArgument
        ) : StyleSchema.OpenScope<Scope>(argument) {
            var fontColor by delegate<JsonObject?>(Key.fontColor)
            var fontSize by delegate<JsonObject?>(Key.fontSize)
        }
    }
}
