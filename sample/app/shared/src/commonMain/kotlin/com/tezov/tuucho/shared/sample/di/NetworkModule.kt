package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule
import com.tezov.tuucho.shared.sample.interceptor.HeadersInterceptor
import com.tezov.tuucho.shared.sample.interceptor.HeaderAuthorizationInterceptor
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.bind

object NetworkModule {

    interface Config {
        val headerPlatform: String
    }

    fun invoke(): ModuleDeclaration = {

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