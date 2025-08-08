package com.tezov.tuucho.core.domain.business.jsonSchema.material.content.action

object Action {

    object Navigate {
        const val command = "navigate"

        object Url {
            const val authority = "url"
        }
        object LocalDestination {
            const val authority = "local-destination"
            object Target {
                const val back = "back"
                const val finish = "finish"
            }
        }
    }

    object Form {
        const val command = "form"

        object Send {
            const val authority = "send-url"
        }

        object Update {
            const val authority = "update"
            object Target {
                const val error = "error"
            }
        }
    }

}