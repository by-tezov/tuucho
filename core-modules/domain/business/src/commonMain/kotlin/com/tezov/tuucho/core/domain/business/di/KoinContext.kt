package com.tezov.tuucho.core.domain.business.di

import org.koin.core.Koin
import org.koin.core.KoinApplication

object KoinContext {

    lateinit var koinApplication:KoinApplication

    val koin get() = koinApplication.koin

}

interface TuuchoKoinComponent : org.koin.core.component.KoinComponent {
    override fun getKoin(): Koin = KoinContext.koin
}