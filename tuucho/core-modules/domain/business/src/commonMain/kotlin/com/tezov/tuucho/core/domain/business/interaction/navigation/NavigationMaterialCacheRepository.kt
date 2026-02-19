package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.selector.PageBreadCrumbNavigationDefinitionSelectorMatcher
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema.Key
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSchema
import com.tezov.tuucho.core.domain.business.protocol.NavigationDefinitionSelectorMatcherProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol.Retrieve
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.MaterialCache
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackScreen
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationDefinitionSelectorMatcherFactoryUseCase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

internal class NavigationMaterialCacheRepository(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val navigationOptionSelectorFactory: NavigationDefinitionSelectorMatcherFactoryUseCase,
) : MaterialCache,
    TuuchoKoinComponent {
    private val retrieveMaterialRepository by inject<Retrieve>()
    private val navigationStackScreenRepository by inject<StackScreen>()

    private val mutex = Mutex()
    private val entriesCache = mutableMapOf<String, JsonElement>()
    private val routesCaches = mutableMapOf<String, List<NavigationRoute.Url>>()

    private fun String.key(postfix: String? = null) = postfix?.let { "$this+${postfix}" } ?: this

    private suspend fun JsonObject.acceptNavigationDefinition(): Boolean {
        val selectorObject =
            withScope(ComponentSettingNavigationSchema.Definition::Scope).selector ?: return true
        val selector = useCaseExecutor
            .await(
                useCase = navigationOptionSelectorFactory,
                input = NavigationDefinitionSelectorMatcherFactoryUseCase.Input(
                    prototypeObject = selectorObject
                )
            )?.selector
            ?.acceptNavigationDefinitionSelector() ?: throw DomainException.Default("Should not be possible")
        return selector
    }

    private suspend fun NavigationDefinitionSelectorMatcherProtocol.acceptNavigationDefinitionSelector() = when (this) {
        is PageBreadCrumbNavigationDefinitionSelectorMatcher -> {
            val route = navigationStackScreenRepository.routes()
            accept(route.map { it.value })
        }

        else -> {
            throw DomainException.Default("Unknown navigation option selector $this")
        }
    }

    override suspend fun prepareNavigationConsumable(
        url: String
    ) {
        mutex.withLock {
            var componentObject = entriesCache[url.key()]
            if (componentObject == null || !retrieveMaterialRepository.isValid(url)) {
                componentObject = retrieveMaterialRepository.process(url)
            }
            entriesCache[url.key()] = componentObject

            // Shadower
            val settingScope = componentObject
                .onScope(ComponentSettingSchema.Root::Scope)
            settingScope.shadower?.let {
                entriesCache[url.key(ComponentSettingSchema.Root.Key.shadower)] = it
            } ?: run { entriesCache.remove(url.key(ComponentSettingSchema.Root.Key.shadower)) }

            // Navigation Extra
            val settingNavigationScope = settingScope.navigation
                ?.withScope(ComponentSettingNavigationSchema::Scope)
            settingNavigationScope?.extra?.let {
                entriesCache[url.key(ComponentSettingNavigationSchema.Key.extra)] = it
            } ?: run { entriesCache.remove(url.key(ComponentSettingNavigationSchema.Key.extra)) }

            // Navigation Definition
            val settingNavigationDefinitionScope = settingNavigationScope
                ?.definitions
                ?.firstOrNull { it.jsonObject.acceptNavigationDefinition() }
                ?.let { it as? JsonObject }
                ?.withScope(ComponentSettingNavigationSchema.Definition::Scope)
            settingNavigationDefinitionScope?.option?.let {
                entriesCache[url.key(ComponentSettingNavigationSchema.Definition.Key.option)] = it
            } ?: run { entriesCache.remove(url.key(ComponentSettingNavigationSchema.Definition.Key.option)) }
            settingNavigationDefinitionScope?.transition?.let {
                entriesCache[url.key(ComponentSettingNavigationSchema.Definition.Key.transition)] = it
            } ?: run { entriesCache.remove(url.key(ComponentSettingNavigationSchema.Definition.Key.transition)) }
        }
    }

    override suspend fun bindComponentObjectCache(
        route: NavigationRoute.Url
    ) {
        mutex.withLock {
            routesCaches[route.value]?.let { routes ->
                routesCaches[route.value] = routes + route
            } ?: run {
                routesCaches[route.value] = listOf(route)
            }
        }
    }

    override suspend fun unbindComponentObjectCache(
        route: NavigationRoute.Url
    ) {
        mutex.withLock {
            routesCaches[route.value]?.let { routes ->
                (routes - route).also { result ->
                    if (result.isEmpty()) {
                        routesCaches.remove(route.value)
                        entriesCache.keys
                            .filter { it.startsWith(route.value) }
                            .forEach { entriesCache.remove(it) }
                    } else {
                        routesCaches[route.value] = result
                    }
                }
            }
        }
    }

    override suspend fun getComponentObject(
        url: String
    ) = mutex.withLock { entriesCache[url.key()] as JsonObject }

    override suspend fun consumeNavigationSettingExtraObject(
        url: String
    ) = mutex.withLock {
        entriesCache.remove(url.key(ComponentSettingNavigationSchema.Key.extra)) as? JsonObject
    }

    override suspend fun consumeNavigationDefinitionOptionObject(
        url: String
    ) = mutex.withLock {
        entriesCache.remove(url.key(ComponentSettingNavigationSchema.Definition.Key.option)) as? JsonObject
    }

    override suspend fun consumeNavigationDefinitionTransitionObject(
        url: String
    ) = mutex.withLock {
        entriesCache.remove(url.key(ComponentSettingNavigationSchema.Definition.Key.transition)) as? JsonObject
    }

    override suspend fun consumeShadowerSettingObject(
        url: String,
        direction: String
    ) = mutex.withLock {
        (entriesCache.remove(url.key(ComponentSettingSchema.Root.Key.shadower)) as? JsonObject)
            ?.withScope(SettingComponentShadowerSchema::Scope)
            ?.let {
                when (direction) {
                    Key.navigateForward -> it.navigateForward
                    Key.navigateBackward -> it.navigateBackward
                    else -> throw DomainException.Default("invalid direction $direction")
                }
            }
    }
}
