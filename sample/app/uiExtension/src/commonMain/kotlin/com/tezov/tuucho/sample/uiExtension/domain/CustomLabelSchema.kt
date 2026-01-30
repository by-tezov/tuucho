package com.tezov.tuucho.sample.uiExtension.domain

import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MessageSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.action.ActionSchema

object CustomLabelSchema {
    object Component {
        object Value {
            const val subset = "label-custom"
        }
    }

    object Content {
        object Key {
            const val value = "value"
            const val action = "action"
        }
    }

    object Style {
        object Key {
            const val fontColorLight = "font-color-light"
            const val fontColorDark = "font-color-dark"
            const val fontSize = "font-size"
        }
    }

    object Message {
        object Key {
            const val upstream = "upstream"
            const val downstream = "downstream"
        }

        object Value {
            object Subset {
                const val customLabelMessage = "custom-label-message"
            }
        }

        class Scope(
            argument: SchemaScopeArgument
        ) : MessageSchema.OpenScope<Scope>(argument) {
            var upstream by delegate<Int?>(Key.upstream)
            var downstream by delegate<Int?>(Key.downstream)
        }
    }

}
