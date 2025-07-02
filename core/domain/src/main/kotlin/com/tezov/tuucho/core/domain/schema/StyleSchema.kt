package com.tezov.tuucho.core.domain.schema

import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

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
        val JsonElement.height get() = this.jsonObject[Key.height].string
        val JsonElement.heightOrNull get() = (this as? JsonObject)?.get(Key.height).stringOrNull

        val JsonElement.width get() = this.jsonObject[Key.width].string
        val JsonElement.widthOrNull get() = (this as? JsonObject)?.get(Key.width).stringOrNull
    }
}



