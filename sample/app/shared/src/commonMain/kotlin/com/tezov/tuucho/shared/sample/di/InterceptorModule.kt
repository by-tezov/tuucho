package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.domain.business.di.Koin
import com.tezov.tuucho.core.domain.tool.extension.ExtensionKoin.bindOrdered
import com.tezov.tuucho.shared.sample.interceptor.FailSafePageHttpInterceptor
import com.tezov.tuucho.shared.sample.interceptor.HeaderHttpAuthorizationInterceptor
import com.tezov.tuucho.shared.sample.interceptor.HeadersHttpInterceptor
import com.tezov.tuucho.shared.sample.interceptor.LoggerHttpInterceptor
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

object InterceptorModule {

    interface Config {
        val headerPlatform: String
    }

    fun invoke() = module(ModuleGroupData.Interceptor) {
        http()
    }

    private fun Module.http() {
        factoryOf(::FailSafePageHttpInterceptor) bindOrdered HttpInterceptor::class

        factoryOf(::HeadersHttpInterceptor) bindOrdered HttpInterceptor::class

        factoryOf(::HeaderHttpAuthorizationInterceptor) bindOrdered HttpInterceptor::class

        factoryOf(::LoggerHttpInterceptor) bindOrdered HttpInterceptor::class
    }
}
