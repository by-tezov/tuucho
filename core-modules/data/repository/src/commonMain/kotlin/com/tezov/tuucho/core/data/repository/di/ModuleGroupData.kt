package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol

sealed class ModuleGroupData : ModuleProtocol.Group {
    data object Main : ModuleGroupData()

    data object Assembler : ModuleGroupData()

    data object Breaker : ModuleGroupData()

    data object Rectifier : ModuleGroupData()

    data object Shadower : ModuleGroupData()

    data object RequestInterceptor : ModuleGroupData()
}
