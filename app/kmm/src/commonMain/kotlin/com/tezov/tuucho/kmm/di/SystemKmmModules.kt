package com.tezov.tuucho.kmm.di

object SystemKmmModules {

    internal operator fun invoke() = listOf(
        ViewModelModule.invoke()
    )
}