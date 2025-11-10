package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.tool.extension.ExtensionKoin.bindOrdered
import com.tezov.tuucho.shared.sample.interceptor.FailSafePageInterceptor
import com.tezov.tuucho.shared.sample.interceptor.HeaderAuthorizationInterceptor
import com.tezov.tuucho.shared.sample.interceptor.HeadersInterceptor

object RequestInterceptorModule {

    interface Config {
        val headerPlatform: String
    }

    fun invoke() = module(ModuleGroupData.RequestInterceptor) {

        factory<FailSafePageInterceptor> {
            FailSafePageInterceptor(
                config = get(),
            )
        } bindOrdered HttpInterceptor.Node::class

        factory<HeadersInterceptor> {
            HeadersInterceptor(
                config = get()
            )
        } bindOrdered HttpInterceptor.Node::class

        factory<HeaderAuthorizationInterceptor> {
            HeaderAuthorizationInterceptor(
                useCaseExecutor = get(),
                config = get(),
                getValueOrNullFromStore = get()
            )
        } bindOrdered HttpInterceptor.Node::class

    }
}
