package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository._system.SystemInformation
import com.tezov.tuucho.core.data.repository.di.assembler.AssemblerModule
import com.tezov.tuucho.core.data.repository.di.assembler.MaterialAssemblerScope
import com.tezov.tuucho.core.data.repository.di.assembler.ResponseAssemblerScope
import com.tezov.tuucho.core.data.repository.di.rectifier.MaterialRectifierScope
import com.tezov.tuucho.core.data.repository.di.rectifier.RectifierModule
import com.tezov.tuucho.core.data.repository.di.rectifier.ResponseRectifierScope
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory
import kotlin.collections.plus

@OptIn(TuuchoInternalApi::class)
internal expect fun SystemCoreDataModules.platformInvoke(): List<KoinMass>

@TuuchoInternalApi
object SystemCoreDataModules {
    fun invoke(): List<KoinMass> = listOf(
        module(ModuleContextData.Main) {
            factory<SystemInformation>() bind SystemInformationProtocol::class
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
        RepositoryModule.invoke(),
        NetworkModule.invoke(),
        DatabaseModule.invoke(),
        AssetModule.invoke(),
        ImageModule.invoke(),
        StoreRepositoryModule.invoke(),
    ) +
        platformInvoke()
}
