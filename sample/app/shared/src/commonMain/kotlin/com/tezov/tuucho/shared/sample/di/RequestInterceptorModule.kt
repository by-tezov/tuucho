package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.shared.sample.interceptor.HeadersInterceptor
import com.tezov.tuucho.shared.sample.interceptor.HeaderAuthorizationInterceptor
import org.koin.core.module.Module
import org.koin.dsl.bind

object RequestInterceptorModule {

    interface Config {
        val headerPlatform: String
    }

    fun invoke() = object : ModuleProtocol {

        override val group = ModuleGroupData.RequestInterceptor

        override fun Module.declaration() {

            factory<HeadersInterceptor> {
                HeadersInterceptor(
                    config = get()
                )
            } bind NetworkRepositoryModule.RequestInterceptor::class

            factory<HeaderAuthorizationInterceptor> {
                HeaderAuthorizationInterceptor(
                    useCaseExecutor = get(),
                    config = get(),
                    getValueOrNullFromStore = get()
                )
            } bind NetworkRepositoryModule.RequestInterceptor::class
        }
    }
}