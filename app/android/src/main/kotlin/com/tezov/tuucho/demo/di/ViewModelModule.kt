//package com.tezov.tuucho.demo.di
//
//import com.tezov.tuucho.core.domain.usecase.RegisterNavigationUrlEventUseCase
//import com.tezov.tuucho.demo.MainViewModel
//import org.koin.androidx.viewmodel.dsl.viewModel
//import org.koin.dsl.module
//
//object ViewModelModule {
//
//    internal operator fun invoke() = module {
//        viewModel {
//            MainViewModel(
//                registerNavigationUrlEvent = get<RegisterNavigationUrlEventUseCase>()
//            )
//        }
//    }
//}
