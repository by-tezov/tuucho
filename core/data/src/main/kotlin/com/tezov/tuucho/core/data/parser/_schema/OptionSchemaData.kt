package com.tezov.tuucho.core.data.parser._schema

import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderSubsetSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData

interface OptionSchemaData :
    HeaderTypeSchemaData,
    HeaderIdSchemaData,
    HeaderSubsetSchemaData {

    object Default {
        const val type = "option"
    }
}



