package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.selector.PageBreadCrumbNavigationDefinitionSelectorMatcher
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.NavigationDefinitionSelectorMatcherProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol.Retrieve
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.MaterialCache
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol.StackScreen
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationDefinitionSelectorMatcherFactoryUseCase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class NavigationMaterialCacheRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val navigationOptionSelectorFactory: NavigationDefinitionSelectorMatcherFactoryUseCase,
) : MaterialCache, TuuchoKoinComponent {

    private val retrieveMaterialRepository by inject<Retrieve>()
    private val navigationStackScreenRepository by inject<StackScreen>()

    private val mutex = Mutex()
    private val cache = mutableMapOf<String, CacheEntry>()

    private inner class CacheEntry(
        val url: String,
    ) {
        val component = suspendLazy {
            retrieveMaterialRepository.process(url)
        }

        val setting = suspendLazy {
            component.await()
                .onScope(ComponentSettingSchema.Root::Scope)
                .navigation
        }

        val settingExtra = suspendLazy {
            setting.await()
                ?.withScope(ComponentSettingNavigationSchema::Scope)
                ?.extra
        }

        val definition = suspendLazy {
            setting.await()
                ?.withScope(ComponentSettingNavigationSchema::Scope)
                ?.definitions
                ?.firstOrNull { it.jsonObject.accept() }
                ?.let { it as? JsonObject }
        }

        val definitionOption = suspendLazy {
            definition.await()
                ?.withScope(ComponentSettingNavigationSchema.Definition::Scope)
                ?.option
        }

        val definitionTransition = suspendLazy {
            definition.await()
                ?.withScope(ComponentSettingNavigationSchema.Definition::Scope)
                ?.transition
        }


        private suspend fun JsonObject.accept(): Boolean {
            val selectorObject =
                withScope(ComponentSettingNavigationSchema.Definition::Scope).selector ?: return true
            val selector = useCaseExecutor
                .await(
                    useCase = navigationOptionSelectorFactory,
                    input = NavigationDefinitionSelectorMatcherFactoryUseCase.Input(
                        prototypeObject = selectorObject
                    )
                )?.selector
                ?.accept() ?: throw DomainException.Default("Should not be possible")
            return selector
        }

        private suspend fun NavigationDefinitionSelectorMatcherProtocol.accept() = when (this) {
            is PageBreadCrumbNavigationDefinitionSelectorMatcher -> {
                val route = navigationStackScreenRepository.routes()
                accept(route.map { it.value })
            }

            else -> {
                throw DomainException.Default("Unknown navigation option selector $this")
            }
        }
    }

    private fun <T> suspendLazy(block: suspend () -> T) = coroutineScopes.default.async(start = CoroutineStart.LAZY) { block() }

    private fun getEntry(url: String) = cache.getOrPut(url) { CacheEntry(url) }

    override suspend fun releaseAll(urls: List<String>) {
        mutex.withLock { urls.forEach { cache.remove(it) } }
    }

    override suspend fun release(url: String) {
        mutex.withLock { cache.remove(url) }
    }

    override suspend fun getComponentObject(url: String) =
        mutex.withLock { getEntry(url).component.await() }

    override suspend fun getNavigationSettingObject(url: String) =
        mutex.withLock { getEntry(url).setting.await() }

    override suspend fun getNavigationSettingExtraObject(url: String) =
        mutex.withLock { getEntry(url).settingExtra.await() }

    override suspend fun getNavigationDefinitionObject(url: String) =
        mutex.withLock { getEntry(url).definition.await() }

    override suspend fun getNavigationDefinitionOptionObject(url: String) =
        mutex.withLock { getEntry(url).definitionOption.await() }

    override suspend fun getNavigationDefinitionTransitionObject(url: String) =
        mutex.withLock { getEntry(url).definitionTransition.await() }

}
