package com.tezov.tuucho.core.data.di

import android.content.Context
import com.tezov.tuucho.core.data.repository.di.DatabaseRepositoryModuleAndroid
import org.koin.core.qualifier.named
import org.koin.dsl.module

object ApplicationModules {

    object Name {
        val APPLICATION_CONTEXT = named("ApplicationModules.Name.APPLICATION_CONTEXT")
    }

    internal fun invoke() = module {
        factory<Context>(DatabaseRepositoryModuleAndroid.Name.APPLICATION_CONTEXT) {
            get(Name.APPLICATION_CONTEXT)
        }
    }
}