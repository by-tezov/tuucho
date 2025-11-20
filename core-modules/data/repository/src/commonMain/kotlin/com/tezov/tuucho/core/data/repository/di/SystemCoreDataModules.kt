package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.repository.SystemInformation
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol

@OptIn(TuuchoInternalApi::class)
internal expect fun SystemCoreDataModules.platformInvoke(): List<ModuleProtocol>

@TuuchoInternalApi
object SystemCoreDataModules {
    fun invoke(): List<ModuleProtocol> = listOf(
        module(ModuleGroupData.Main) {
            factory<SystemInformationProtocol> {
                SystemInformation(
                    platformRepository = get()
                )
            }
        },
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
