package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import kotlin.collections.plus

@OptIn(TuuchoInternalApi::class)
internal expect fun SystemCoreDataModules.platformInvoke(): List<ModuleProtocol>

@TuuchoInternalApi
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
    ) +
        platformInvoke()
}
