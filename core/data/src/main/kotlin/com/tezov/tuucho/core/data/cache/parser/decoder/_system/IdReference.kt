package com.tezov.tuucho.core.data.cache.parser.decoder._system

import com.tezov.tuucho.core.domain.model._system.SymbolDomain

fun resolveIdRef(id: String?, idFrom: String?) = when {
    id?.startsWith(SymbolDomain.ID_REF_INDICATOR) == true -> {
        id.removePrefix(SymbolDomain.ID_REF_INDICATOR)
    }

    idFrom != null -> {
        idFrom.removePrefix(SymbolDomain.ID_REF_INDICATOR)
    }

    else -> null
}


