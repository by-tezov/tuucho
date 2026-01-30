package com.tezov.tuucho.core.domain.business._system.koin

import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.ScopeDSL

object KoinModuleExtension {

    @KoinDslMarker
    inline fun <reified T : Any> Module.factoryObject(
        value: T
    ) = factory(named<T>()) { value }

    @KoinDslMarker
    inline fun <reified T : Any> ScopeDSL.factoryObject(
        value: T
    ) = factory(named<T>()) { value }
}
