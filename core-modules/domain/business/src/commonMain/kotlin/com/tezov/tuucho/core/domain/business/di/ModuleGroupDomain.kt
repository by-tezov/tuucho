package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol

sealed class ModuleGroupDomain: ModuleProtocol.Group {
    data object Main : ModuleGroupDomain()
    data object UseCase : ModuleGroupDomain()
    data object ActionProcessor : ModuleGroupDomain()
    data object Middleware : ModuleGroupDomain()
    data object RequestInterceptor : ModuleGroupDomain()
}