package com.tezov.tuucho.core.domain.business.model.schema.material._element

import com.tezov.tuucho.core.domain.business.model.schema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.model.schema.material.content.ContentSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.content.action.ActionSchema
import kotlinx.serialization.json.JsonObject

object ButtonSchema {

    object Component {
        object Value {
            const val subset = SubsetSchema.Value.button
        }
    }

    object Content {
        object Key {
            const val label = LabelSchema.Component.Value.subset
            const val action = ActionSchema.root
        }

        class Scope(argument: SchemaScopeArgument) : ContentSchema.OpenScope<Scope>(argument) {

            var label by delegate<JsonObject?>(Key.label)
            var action by delegate<JsonObject?>(Key.action)

        }

    }


}
