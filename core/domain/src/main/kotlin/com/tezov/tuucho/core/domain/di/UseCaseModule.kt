package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.repository.MaterialRepository
import com.tezov.tuucho.core.domain.usecase.RefreshCacheMaterialUseCase
import org.koin.dsl.module

object UseCaseModule {

    internal operator fun invoke() = module {
        factory { RefreshCacheMaterialUseCase(get<MaterialRepository>()) }
    }

}


