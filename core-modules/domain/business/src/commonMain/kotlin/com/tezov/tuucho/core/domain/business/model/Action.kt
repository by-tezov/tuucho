package com.tezov.tuucho.core.domain.business.model

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

            object ActionLabel {
                const val validated = "validated"
                const val denied = "denied"
            }
        }

        object Update {
            const val authority = "update"
            object Target {
                const val error = "error"
            }
        }
    }

    object Store {
        const val command = "store"

        object KeyValue {
            const val authority = "key-value"
            object Target {
                const val save = "save"
                const val remove = "remove"
            }
        }
    }

}