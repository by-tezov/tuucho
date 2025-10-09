package com.tezov.tuucho.shared.sample.di

import org.koin.dsl.ModuleDeclaration

internal expect fun SystemSharedModules.platformInvoke(): List<ModuleDeclaration>

object SystemSharedModules {

    fun invoke(): List<ModuleDeclaration> =
        listOf(
            NetworkRepositoryModule.invoke(),
        ) + platformInvoke()

}
