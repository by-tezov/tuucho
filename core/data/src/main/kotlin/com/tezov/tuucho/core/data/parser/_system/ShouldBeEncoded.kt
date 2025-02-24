package com.tezov.tuucho.core.data.parser._system

import com.tezov.tuucho.core.domain.model.material._common.RefModelDomain
import com.tezov.tuucho.core.domain.model.material._common.header.HeaderHasChildrenModelDomain

fun <T: Any> T.shouldBeEncoded(): Boolean {
    return (this as? HeaderHasChildrenModelDomain)?.hasChildren == true || this !is RefModelDomain
}
