package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.di.ExtensionKoin.bindOrdered
import com.tezov.tuucho.shared.sample.interceptor.FailSafePageInterceptor
import com.tezov.tuucho.shared.sample.interceptor.HeaderAuthorizationInterceptor
import com.tezov.tuucho.shared.sample.interceptor.HeadersInterceptor
import org.koin.core.module.Module
import org.koin.dsl.bind

object RequestInterceptorModule {

    interface Config {
        val headerPlatform: String
    }

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupData.RequestInterceptor

        override fun Module.declaration() {

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
}
