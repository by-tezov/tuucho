package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.usecase.ComponentRenderUseCase
import com.tezov.tuucho.core.domain.usecase.RefreshCacheMaterialUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterNavigationUrlEventUseCase
import org.koin.dsl.module

object UseCaseModule {

    internal operator fun invoke() = module {
        factory {
            ActionHandlerUseCase(
                listOf(
                    get<NavigationUrlActionHandler>()
                )
            )
        }

        factory {
            ComponentRenderUseCase(
                get<MaterialRepositoryProtocol>(),
                get<ScreenRendererProtocol>(),
            )
        }

        factory {
            RegisterNavigationUrlEventUseCase(
                get<NavigationUrlActionHandler>()
            )
        }

        factory { RefreshCacheMaterialUseCase(get<MaterialRepositoryProtocol>()) }
    }

}


