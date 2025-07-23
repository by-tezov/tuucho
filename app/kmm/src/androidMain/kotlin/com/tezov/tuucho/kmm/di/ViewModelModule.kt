package com.tezov.tuucho.kmm.di

import com.tezov.tuucho.core.domain.usecase.RegisterNavigationUrlEventUseCase
import com.tezov.tuucho.kmm.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {

    internal operator fun invoke() = module {
        viewModel {
            MainViewModel(
                registerNavigationUrlEvent = get<RegisterNavigationUrlEventUseCase>()
            )
        }
    }
}
