package com.tezov.tuucho.core.domain.business.protocol

import org.koin.core.module.KoinDslMarker
import org.koin.dsl.ModuleDeclaration

data class ModuleProtocol(
    val group: Group,
    val declaration: ModuleDeclaration
) {
    interface Group

    companion object {
        @KoinDslMarker
        fun module(
            group: Group,
            declaration: ModuleDeclaration
        ) = ModuleProtocol(group, declaration)
    }
}
