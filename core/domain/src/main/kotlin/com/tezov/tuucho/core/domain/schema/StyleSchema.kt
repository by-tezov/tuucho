package com.tezov.tuucho.core.domain.schema

import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import kotlinx.serialization.json.JsonElement

interface StyleSchema :
    TypeSchema,
    IdSchema,
    SubsetSchema {

    object Key {
        const val height = "height"
        const val width = "width"
    }

    object Value
    
    companion object {
        val Map<String, JsonElement>.height get() = this[Key.height].string
        val Map<String, JsonElement>.heightOrNull get() = this[Key.height].stringOrNull

        val Map<String, JsonElement>.width get() = this[Key.width].string
        val Map<String, JsonElement>.widthOrNull get() = this[Key.width].stringOrNull
    }
}



