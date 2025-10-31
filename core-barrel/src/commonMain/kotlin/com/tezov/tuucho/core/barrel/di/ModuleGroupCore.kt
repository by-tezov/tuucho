package com.tezov.tuucho.core.barrel.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol

sealed class ModuleGroupCore: ModuleProtocol.Group {
    data object Main : ModuleGroupCore()
}