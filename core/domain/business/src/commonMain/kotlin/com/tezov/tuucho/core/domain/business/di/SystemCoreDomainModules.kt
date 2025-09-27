package com.tezov.tuucho.core.domain.business.di

object SystemCoreDomainModules {

    fun invoke() = listOf(
        MiscModule.invoke(),
        NavigationModule.invoke(),
        ActionProcessorModule.invoke(),
        UseCaseModule.invoke()
    )

}