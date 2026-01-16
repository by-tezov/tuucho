package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.di.assembler.AssemblerModule
import com.tezov.tuucho.core.data.repository.di.assembler.MaterialAssemblerScope
import com.tezov.tuucho.core.data.repository.di.assembler.ResponseAssemblerScope
import com.tezov.tuucho.core.data.repository.di.rectifier.MaterialRectifierScope
import com.tezov.tuucho.core.data.repository.di.rectifier.RectifierModule
import com.tezov.tuucho.core.data.repository.di.rectifier.ResponseRectifierScope
import com.tezov.tuucho.core.data.repository.repository.SystemInformation
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

@OptIn(TuuchoInternalApi::class)
internal expect fun SystemCoreDataModules.platformInvoke(): List<KoinMass>

@TuuchoInternalApi
object SystemCoreDataModules {
    fun invoke(): List<KoinMass> = listOf(
        module(ModuleContextData.Main) {
            factoryOf(::SystemInformation) bind SystemInformationProtocol::class
        },
        MiscModule.invoke(),
        RectifierModule.invoke(),
        MaterialRectifierScope.invoke(),
        ResponseRectifierScope.invoke(),
        MaterialBreakerModule.invoke(),
        AssemblerModule.invoke(),
        MaterialAssemblerScope.invoke(),
        ResponseAssemblerScope.invoke(),
        ResponseAssemblerScope.Form.invoke(),
        MaterialShadowerModule.invoke(),
        MaterialRepositoryModule.invoke(),
        DatabaseRepositoryModule.invoke(),
        StoreRepositoryModule.invoke(),
        NetworkRepositoryModule.invoke(),
    ) +
        platformInvoke()
}
