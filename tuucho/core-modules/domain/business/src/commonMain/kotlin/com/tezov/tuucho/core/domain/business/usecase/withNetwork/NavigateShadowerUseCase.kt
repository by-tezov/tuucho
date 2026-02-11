package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema.Key
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateShadowerUseCase.Input
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrue

@OpenForTest
class NavigateShadowerUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val materialCacheRepository: NavigationRepositoryProtocol.MaterialCache,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val shadowerMiddlewares: List<NavigationMiddleware.Shadower>
) : UseCaseProtocol.Async<Input, Unit>,
    TuuchoKoinComponent {
    data class Input(
        val route: NavigationRoute.Url,
        val direction: String
    )

    override suspend fun invoke(
        input: Input
    ) {
        middlewareExecutor
            .process(
                middlewares = shadowerMiddlewares + terminalMiddleware(),
                context = NavigationMiddleware.Shadower.Context(
                    input = input
                )
            )
    }

    private fun terminalMiddleware() = NavigationMiddleware.Shadower { context, _ ->
        with(context.input) {
            val screen = navigationStackScreenRepository
                .getScreenOrNull(route)
                ?: return@with
            val settingShadowerScope = materialCacheRepository
                .let {
                    when (direction) {
                        Key.navigateForward -> it.getShadowerSettingNavigateForwardObject(route.value)
                        Key.navigateBackward -> it.getShadowerSettingNavigateBackwardObject(route.value)
                        else -> throw DomainException.Default("invalid direction $direction")
                    }
                }?.withScope(SettingComponentShadowerSchema.Navigate::Scope)
            if (settingShadowerScope?.enable.isTrue) {
                if (settingShadowerScope?.waitDoneToRender.isTrue) {
                    processShadowerAndUpdateScreen(screen)
                } else {
                    @OptIn(TuuchoInternalApi::class)
                    coroutineScopes.default.asyncOnCompletionThrowing {
                        // TODO: How can I catch that ?
                        processShadowerAndUpdateScreen(screen)
                    }
                }
            }
        }
    }

    private suspend fun processShadowerAndUpdateScreen(
        screen: ScreenProtocol
    ) {
        val jsonObjects = shadowerMaterialRepository
            .process(
                url = screen.route.value,
                types = listOf(Shadower.Type.contextual)
            ).map { it.jsonObject }
        screen.update(jsonObjects)
    }
}
