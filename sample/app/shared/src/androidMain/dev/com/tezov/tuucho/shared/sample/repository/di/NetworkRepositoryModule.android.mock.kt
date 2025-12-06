package com.tezov.tuucho.shared.sample.repository.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import com.tezov.tuucho.core.barrel.di.ModuleGroupCore
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.shared.sample.di.NetworkModuleAndroid

object NetworkRepositoryModuleAndroidFlavor {

    fun invoke() = NetworkModuleAndroid.FlavorDefault.invoke()
}
