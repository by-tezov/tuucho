package com.tezov.tuucho.core.domain.business.di

object SystemCoreDomainModules {

    operator fun invoke() = listOf(
        MiscModule(),
        NavigationModule(),
        ActionProcessorModule(),
        UseCaseModule()
    )

}