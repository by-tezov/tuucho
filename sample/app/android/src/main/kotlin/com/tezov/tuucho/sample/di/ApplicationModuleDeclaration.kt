package com.tezov.tuucho.sample.di

import android.content.Context
import com.tezov.tuucho.core.data.di.ApplicationModules
import org.koin.dsl.ModuleDeclaration

object ApplicationModuleDeclaration {

    operator fun invoke(
        applicationContext: Context,
    ): ModuleDeclaration = {
        single<Context>(ApplicationModules.Name.APPLICATION_CONTEXT) {
            applicationContext
        }
    }

}