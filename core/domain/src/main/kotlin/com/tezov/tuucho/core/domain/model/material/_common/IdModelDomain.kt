package com.tezov.tuucho.core.domain.model.material._common

import kotlinx.serialization.Serializable

@Serializable
data class IdModelDomain(
    val value: String,
    val source: String? = null
) {
//        fun rectifyIdsRef(id: String?, idFrom: String?) = when {
//            id?.startsWith(SymbolDomain.REF_INDICATOR) == true -> {
//                Uuid.random().toHexString() to id
//            }
//
//            idFrom != null -> {
//                (id ?: Uuid.random().toHexString()) to idFrom
//            }
//
//            else -> null
//        }
}