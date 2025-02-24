package com.tezov.tuucho.core.domain.model._system

import kotlin.uuid.Uuid

// TODO: UUID on null id should ne not setted here but on data/encoder
// for now, I can't be cause I need to refactor the encoder to work directly
// with JsonElement same I did with decoder because it is easy to alter the json on the fly
fun rectifyIdsRef(id: String?, idFrom: String?) = when {
    id?.startsWith(SymbolDomain.ID_REF_INDICATOR) == true -> {
        Uuid.random().toHexString() to id
    }

    idFrom != null -> {
        (id ?: Uuid.random().toHexString()) to idFrom
    }

    else -> null
}



