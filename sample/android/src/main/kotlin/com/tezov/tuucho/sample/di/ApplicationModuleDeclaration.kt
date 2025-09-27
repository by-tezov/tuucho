package com.tezov.tuucho.sample.di

import android.content.Context
import com.tezov.tuucho.core.data.repository.di.DatabaseRepositoryModuleAndroid
import org.koin.dsl.ModuleDeclaration

object ApplicationModuleDeclaration {

    operator fun invoke(
        applicationContext: Context,
    ): ModuleDeclaration = {
        single<Context>(DatabaseRepositoryModuleAndroid.Name.APPLICATION_CONTEXT) {
            applicationContext
        }
    }

}