package com.tezov.tuucho.core.ui.di

import com.tezov.tuucho.core.domain.repository.MaterialRepository
import com.tezov.tuucho.core.ui.renderer.MaterialRenderer
import com.tezov.tuucho.core.ui.userCase.ComponentRenderUseCase
import org.koin.dsl.module

object UseCaseModule {

    internal operator fun invoke() = module {
        factory {
            ComponentRenderUseCase(
                get<MaterialRepository>(),
                get<MaterialRenderer>(),
            )
        }
    }

}


