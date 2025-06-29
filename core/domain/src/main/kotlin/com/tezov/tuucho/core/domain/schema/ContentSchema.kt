package com.tezov.tuucho.core.domain.schema

import com.tezov.tuucho.core.domain.schema.common.IdSchema
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema

interface ContentSchema :
    TypeSchema,
    IdSchema,
    SubsetSchema {

    object Key

    object Value

}
