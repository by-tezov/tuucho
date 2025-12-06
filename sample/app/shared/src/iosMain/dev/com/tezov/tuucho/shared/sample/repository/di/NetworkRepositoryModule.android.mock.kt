package com.tezov.tuucho.shared.sample.repository.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import com.tezov.tuucho.core.barrel.di.ModuleGroupCore
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module

object NetworkRepositoryModuleAndroidFlavor {

    fun invoke() = NetworkRepositoryModuleIos.FlavorDefault.invoke()
}
