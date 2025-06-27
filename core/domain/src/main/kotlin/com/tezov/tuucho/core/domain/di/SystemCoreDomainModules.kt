package com.tezov.tuucho.core.domain.di

object SystemCoreDomainModules {

    operator fun invoke() = listOf(
        MiscModule(),
         UseCaseModule()
    )

}