package com.tezov.tuucho.core.domain.schema

import com.tezov.tuucho.core.domain.schema.common.IdSchema
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema

interface ComponentSchema :
    TypeSchema,
    IdSchema,
    SubsetSchema {

    object Key {
        const val content = "content"
        const val style = "style"
    }

}
