package com.tezov.tuucho.core.data.parser._schema

import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderSubsetSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchema

interface OptionSchema :
    HeaderTypeSchema,
    HeaderIdSchema,
    HeaderSubsetSchema {

    object Default {
        const val type = "option"
    }
}



