package com.tezov.tuucho.core.data.parser._schema

import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderSubsetSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData

interface ComponentSchemaData :
    HeaderTypeSchemaData,
    HeaderIdSchemaData,
    HeaderSubsetSchemaData {

    object Default {
        const val type = "component"
    }
}

object DefaultComponentSchemaData : ComponentSchemaData {

    object Name {
        const val option = "option"
        const val style = "style"
        const val content = "content"
    }
}



