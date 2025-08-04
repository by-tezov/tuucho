package com.tezov.tuucho.core.domain.business.model

object Action {

    object Navigate {
        const val command = "navigate"

        object Authority {
            const val url = "url"
        }
    }

    object Form {
        private const val command = "form-"

        object Send {
            const val command = "${Form.command}send"

            object Authority {
                const val url = "url"
            }
        }

        object Update {
            const val command = "${Form.command}update"

            object Authority {
                const val error = "error"
            }

        }
    }

}