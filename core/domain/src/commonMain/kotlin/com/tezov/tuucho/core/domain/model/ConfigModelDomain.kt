package com.tezov.tuucho.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ConfigModelDomain(
    val preload: Preload
) {
    @Serializable
    data class Preload(
        val subs: List<Item> = emptyList(),
        val templates: List<Item> = emptyList(),
        val pages: List<Item> = emptyList()
    ) {

        @Serializable
        data class Item(
            val version: String,
            val url: String
        )
    }
}
