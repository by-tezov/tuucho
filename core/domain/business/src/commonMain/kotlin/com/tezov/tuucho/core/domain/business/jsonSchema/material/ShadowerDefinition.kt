package com.tezov.tuucho.core.domain.business.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema.config.ConfigSchema

object Shadower {

    object Type {
        const val contextual = "contextual"
    }

    object Contextual {

        fun String.replaceUrlOriginToken(value: String) = replace("\${${ConfigSchema.MaterialItem.Key.urlOrigin}}", value)

        fun defaultUrl(base: String) = "$base-${Type.contextual}"
    }
}