package com.tezov.tuucho.core.domain.schema

import com.tezov.tuucho.core.domain.schema.common.IdSchema
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema

interface StyleSchema :
    TypeSchema,
    IdSchema,
    SubsetSchema {

    object Key {
        const val height = "height"
        const val width = "width"
    }

    object Value
}



