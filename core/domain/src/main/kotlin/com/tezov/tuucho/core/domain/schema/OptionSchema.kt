package com.tezov.tuucho.core.domain.schema

interface OptionSchema :
    TypeSchema,
    IdSchema,
    SubsetSchema {

    object Key

    object Value

}



