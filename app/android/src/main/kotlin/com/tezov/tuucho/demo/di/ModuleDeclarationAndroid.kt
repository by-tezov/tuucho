package com.tezov.tuucho.demo.di

import android.content.Context
import com.tezov.tuucho.core.data.repository.di.DatabaseRepositoryModuleAndroid
import org.koin.dsl.ModuleDeclaration

object ModuleDeclarationAndroid {

    operator fun invoke(
        applicationContext: Context,
    ): ModuleDeclaration = {
        single<Context>(DatabaseRepositoryModuleAndroid.Name.APPLICATION_CONTEXT) {
            applicationContext
        }
    }

}