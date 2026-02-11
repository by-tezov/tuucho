package com.tezov.tuucho.sample.shared.di

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.bindOrdered
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.interaction.exceptionHandler.ShadowerExceptionHandler
import com.tezov.tuucho.sample.shared.exceptionHandler.ShadowerNavigateExceptionHandler
import org.koin.plugin.module.dsl.factory

object ExceptionHandlerModule {

    fun invoke() = module(ModuleContextData.Interceptor) {
        factory<ShadowerNavigateExceptionHandler>() bindOrdered ShadowerExceptionHandler.Navigate::class
    }


}
