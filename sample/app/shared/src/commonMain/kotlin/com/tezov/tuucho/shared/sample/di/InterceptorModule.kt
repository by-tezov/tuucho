package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.tool.extension.ExtensionKoin.bindOrdered
import com.tezov.tuucho.shared.sample.interceptor.FailSafePageHttpInterceptor
import com.tezov.tuucho.shared.sample.interceptor.HeaderHttpAuthorizationInterceptor
import com.tezov.tuucho.shared.sample.interceptor.HeadersHttpInterceptor
import com.tezov.tuucho.shared.sample.interceptor.LoggerHttpInterceptor
import org.koin.core.module.Module

object InterceptorModule {

    interface Config {
        val headerPlatform: String
    }

    fun invoke() = module(ModuleGroupData.RequestInterceptor) {
        http()
    }

    private fun Module.http() {
        factory<FailSafePageHttpInterceptor> {
            FailSafePageHttpInterceptor(
                config = get(),
            )
        } bindOrdered HttpInterceptor::class

        factory<HeadersHttpInterceptor> {
            HeadersHttpInterceptor(
                config = get()
            )
        } bindOrdered HttpInterceptor::class

        factory<HeaderHttpAuthorizationInterceptor> {
            HeaderHttpAuthorizationInterceptor(
                useCaseExecutor = get(),
                config = get(),
                getValueOrNullFromStore = get()
            )
        } bindOrdered HttpInterceptor::class

        factory<LoggerHttpInterceptor> {
            LoggerHttpInterceptor(
                logger = get()
            )
        } bindOrdered HttpInterceptor::class
    }
}
