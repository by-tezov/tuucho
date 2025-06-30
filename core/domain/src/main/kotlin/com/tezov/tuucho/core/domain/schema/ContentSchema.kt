package com.tezov.tuucho.core.domain.schema

interface ContentSchema :
    TypeSchema,
    IdSchema,
    SubsetSchema {

    object Key

    object Value

}
