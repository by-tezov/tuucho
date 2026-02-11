package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.exceptionHandler.ShadowerExceptionHandler
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema.Key
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateShadowerUseCase.Input
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrue

@OpenForTest
class NavigateShadowerUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val materialCacheRepository: NavigationRepositoryProtocol.MaterialCache,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
    private val shadowerExceptionHandler: ShadowerExceptionHandler.Navigate?
) : UseCaseProtocol.Async<Input, Unit>,
    TuuchoKoinComponent {
    data class Input(
        val route: NavigationRoute.Url,
        val direction: String
    )

    override suspend fun invoke(
        input: Input
    ) {
        val screen = navigationStackScreenRepository
            .getScreenOrNull(input.route)
            ?: return
        val settingShadowerScope = materialCacheRepository
            .let {
                when (input.direction) {
                    Key.navigateForward -> it.getShadowerSettingNavigateForwardObject(input.route.value)
                    Key.navigateBackward -> it.getShadowerSettingNavigateBackwardObject(input.route.value)
                    else -> throw DomainException.Default("invalid direction $input.direction")
                }
            }?.withScope(SettingComponentShadowerSchema.Navigate::Scope)
        if (settingShadowerScope?.enable.isTrue) {
            if (settingShadowerScope?.waitDoneToRender.isTrue) {
                processShadowerAndUpdateScreen(screen)
            } else {
                coroutineScopes.default.async {
                    processShadowerAndUpdateScreen(screen)
                }
            }
        }
    }

    private suspend fun processShadowerAndUpdateScreen(
        screen: ScreenProtocol
    ) {
        suspend fun process() {
            val jsonObjects = shadowerMaterialRepository
                .process(
                    url = screen.route.value,
                    types = listOf(Shadower.Type.contextual)
                ).map { it.jsonObject }
            screen.update(jsonObjects)
        }
        runCatching { process() }
            .onFailure { failure ->
                shadowerExceptionHandler?.process(
                    context = ShadowerExceptionHandler.Navigate.Context(
                        screen = screen
                    ),
                    exception = failure,
                    replay = ::process
                ) ?: throw failure
            }
    }

}
