package com.tezov.tuucho.kmm.di

import com.tezov.tuucho.kmm.AppScreenViewModel
import org.koin.dsl.module

object ViewModelModule {

    internal operator fun invoke() = module {

        factory<AppScreenViewModel> {
            AppScreenViewModel(
                useCaseExecutor = get(),
                registerUpdateViewEvent = get(),
                registerToNavigationUrlActionEvent = get(),
                registerToScreenStackRepositoryEvent = get(),
                navigateToUrl = get()
            )
        }

    }
}