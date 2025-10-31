package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import kotlin.collections.List
import kotlin.collections.listOf
import kotlin.collections.plus

internal expect fun SystemCoreDataModules.platformInvoke(): List<ModuleProtocol>

object SystemCoreDataModules {

    fun invoke(): List<ModuleProtocol> = listOf(
        MiscModule.invoke(),
        MaterialRectifierModule.invoke(),
        MaterialBreakerModule.invoke(),
        MaterialAssemblerModule.invoke(),
        MaterialShadowerModule.invoke(),
        MaterialRepositoryModule.invoke(),
        DatabaseRepositoryModule.invoke(),
        NetworkRepositoryModule.invoke(),
    ) + platformInvoke()

}
