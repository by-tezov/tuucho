package com.tezov.tuucho.sample.shared.di

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.bindOrdered
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.sample.shared.interceptor.FailSafePageHttpInterceptor
import com.tezov.tuucho.sample.shared.interceptor.HeaderHttpAuthorizationInterceptor
import com.tezov.tuucho.sample.shared.interceptor.HeadersHttpInterceptor
import com.tezov.tuucho.sample.shared.interceptor.LoggerHttpInterceptor
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.plugin.module.dsl.factory

object InterceptorModule {

    interface Config {
        val headerPlatform: String
    }

    fun invoke() = module(ModuleContextData.Interceptor) {
        http()
    }

    private fun Module.http() {
        factory<FailSafePageHttpInterceptor>() bindOrdered HttpInterceptor::class
        factory<HeadersHttpInterceptor>() bindOrdered HttpInterceptor::class
        factory<HeaderHttpAuthorizationInterceptor>() bindOrdered HttpInterceptor::class
        factory<LoggerHttpInterceptor>() bindOrdered HttpInterceptor::class
    }
}
