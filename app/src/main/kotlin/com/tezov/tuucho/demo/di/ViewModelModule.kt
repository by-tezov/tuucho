package com.tezov.tuucho.demo.di

import com.tezov.tuucho.core.data.cache.database.Database
import com.tezov.tuucho.core.domain.usecase.GetMaterialUseCase
import com.tezov.tuucho.core.domain.usecase.RefreshCacheMaterialUseCase
import com.tezov.tuucho.demo.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {

    internal operator fun invoke() = module {
        viewModel {
            HomeViewModel(
                database = get<Database>(),
                refreshCacheMaterials = get<RefreshCacheMaterialUseCase>(),
                getMaterials = get<GetMaterialUseCase>()
            )
        }
    }
}
