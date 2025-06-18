package com.tezov.tuucho.core.data.parser._schema

import com.tezov.tuucho.core.data.parser._schema.header.HeaderIdSchema
import com.tezov.tuucho.core.data.parser._schema.header.HeaderSubsetSchema
import com.tezov.tuucho.core.data.parser._schema.header.HeaderTypeSchema

interface OptionSchema :
    HeaderTypeSchema,
    HeaderIdSchema,
    HeaderSubsetSchema {

    object Default {
        const val type = "option"
    }
}



