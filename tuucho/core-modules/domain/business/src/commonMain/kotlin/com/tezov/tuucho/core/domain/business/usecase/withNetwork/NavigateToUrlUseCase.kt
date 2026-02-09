package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRouteIdGenerator
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase.Input
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class NavigateToUrlUseCase(
    private val navigationRouteIdGenerator: NavigationRouteIdGenerator,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackTransitionRepository: NavigationRepositoryProtocol.StackTransition,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val navigationMiddlewares: List<NavigationMiddleware.ToUrl>
) : UseCaseProtocol.Async<Input, Unit>,
    TuuchoKoinComponent {
    data class Input(
        val url: String
    )

    override suspend fun invoke(
        input: Input
    ) {
        middlewareExecutor
            .process(
                middlewares = navigationMiddlewares + terminalMiddleware(),
                context = NavigationMiddleware.ToUrl.Context(
                    currentUrl = navigationStackRouteRepository.currentRoute()?.value,
                    input = input,
                    onShadowerException = null
                )
            )
    }

    private fun terminalMiddleware() = NavigationMiddleware.ToUrl { context, _ ->
        with(context.input) {
            val inputRoute = NavigationRoute.Url(
                id = navigationRouteIdGenerator.generate(),
                value = url
            )
            val outputRoute = navigationStackRouteRepository
                .forward(route = inputRoute)
            if (outputRoute.id == inputRoute.id) {
                navigationStackScreenRepository
                    .forward(route = outputRoute)
//                runShadower(newRoute, componentObject, context)
            }
            navigationStackTransitionRepository
                .forward(route = outputRoute)
        }
    }

//    private suspend fun runShadower(
//        route: NavigationRoute.Url,
//        componentObject: JsonObject,
//        context: NavigationMiddleware.ToUrl.Context
//    ) {
//        val screen = navigationStackScreenRepository
//            .getScreenOrNull(route)
//            ?: return
//        val componentSettingScope = componentObject
//            .onScope(ComponentSettingSchema.Root::Scope)
//        val settingShadowerScope = componentSettingScope
//            .shadower
//            ?.withScope(SettingComponentShadowerSchema::Scope)
//            ?.navigateForward
//            ?.withScope(SettingComponentShadowerSchema.Navigate::Scope)
//        if (settingShadowerScope?.enable.isTrue) {
//            val job = coroutineScopes.default.async {
//                suspend fun process() {
//                    val jsonObjects = shadowerMaterialRepository
//                        .process(
//                            url = route.value,
//                            componentObject = componentObject,
//                            types = listOf(Shadower.Type.contextual)
//                        ).map { it.jsonObject }
//                    screen.update(jsonObjects)
//                }
//                runCatching { process() }.onFailure { failure ->
//                    context.onShadowerException?.process(
//                        exception = failure,
//                        context = context,
//                        replay = ::process
//                    ) ?: throw failure
//                }
//            }
//            if (settingShadowerScope?.waitDoneToRender.isTrue) {
//                job.await()
//            } else {
//                @OptIn(TuuchoInternalApi::class)
//                coroutineScopes.default.throwOnFailure(job)
//            }
//        }
//    }
}
