package com.tezov.tuucho.core.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui._system.type
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.protocol.ContextualUpdaterProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.protocol.ScreenProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject
import kotlin.reflect.KClass
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol as DomainViewProtocol

internal class Screen(
    override val route: NavigationRoute.Url
) : ScreenProtocol,
    TuuchoKoinComponent {
    private data class Updatable(
        val viewIndex: Int,
        val processors: MutableList<ContextualUpdaterProcessorProtocol>
    )

    private val coroutineScopes by inject<CoroutineScopesProtocol>()
    private val materialCacheRepository by inject<NavigationRepositoryProtocol.MaterialCache>()
    private val viewFactories: List<ViewFactoryProtocol> by lazy { getKoin().getAll() }
    private val redrawCounterTrigger = mutableIntStateOf(0)
    private var rootView: ViewProtocol? = null
    private val views = mutableListOf<ViewProtocol>()
    private val updatables = mutableMapOf<String, Updatable>()
//    private val mutex = ReentrantMutex()

    private fun keyTypeId(
        type: String,
        id: String
    ) = "$type+$id"

    suspend fun createViews() {
        coroutineScopes.default.withContext {
//            mutex.withReentrantLock {
            createViewsNoSync()
//            }
        }
    }

    private suspend fun createViewsNoSync() {
        if (this@Screen.rootView != null) {
            throw UiException.Default("View root is not null")
        }
        val componentObject = materialCacheRepository.getComponentObject(route.value)
        val type = componentObject.withScope(TypeSchema::Scope).self
        if (type != TypeSchema.Value.component) {
            throw UiException.Default("object is not a component $componentObject")
        }
        val id = componentObject.onScope(IdSchema::Scope).value
        val subset = componentObject.withScope(SubsetSchema::Scope).self
        val factory = viewFactories
            .filter { it.accept(componentObject) }
            .singleOrThrow(id, subset)
            ?: throw UiException.Default("No renderer found for $componentObject")
        val screenContext = ScreenContext(
            route = route,
            addViewBlock = ::addView
        )
        val rootView = factory
            .process(screenContext = screenContext)
            .also { rootView = it }
        rootView.initialize(componentObject)
        addViewNoSync(rootView)
        redrawCounterTrigger.intValue += 1
    }

    private suspend fun addView(
        view: ViewProtocol
    ) {
        coroutineScopes.default.withContext {
//            mutex.withReentrantLock {
            addViewNoSync(view)
//            }
        }
    }

    private fun addViewNoSync(
        view: ViewProtocol
    ) {
        views.add(view)
        val viewIndex = views.lastIndex
        view.contextualUpdater.forEach { updater ->
            updater.id?.let { id ->
                val keyTypeId = keyTypeId(updater.type, id)
                updatables
                    .getOrPut(keyTypeId) {
                        Updatable(
                            viewIndex = viewIndex,
                            processors = mutableListOf()
                        )
                    }.processors
                    .add(updater)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <V : DomainViewProtocol> views(
        klass: KClass<V>
    ) = coroutineScopes.default.withContext {
//        mutex.withLock {
        views.filter { klass.isInstance(it) } as List<V>
//        }
    }

    @Composable
    override fun display(
        scope: Any?
    ) {
        if (redrawCounterTrigger.intValue > 0) {
            rootView?.display(scope)
        }
    }

    override suspend fun recreateViews() {
        coroutineScopes.default.withContext {
//            mutex.withLock {
            updatables.clear()
            views.clear()
            rootView = null
            createViewsNoSync()
//            }
        }
    }

    override suspend fun update(
        jsonObject: JsonObject
    ) {
        coroutineScopes.default.withContext {
//            mutex.withLock {
            val updatedIndexView = updateAndReturnViewIndex(jsonObject)
            updatedIndexView?.let { views[it].updateIfNeeded() }
//            }
        }
    }

    override suspend fun update(
        jsonObjects: Flow<JsonObject>
    ) {
        coroutineScopes.default.withContext {
//            mutex.withLock {
            val updatedIndexViews = buildList {
                jsonObjects.collect {
                    updateAndReturnViewIndex(it)?.let(::add)
                }
            }
            updatedIndexViews.forEach {
                views[it].updateIfNeeded()
            }
//            }
        }
    }

    private suspend fun updateAndReturnViewIndex(
        jsonObject: JsonObject
    ): Int? {
        val id = jsonObject.idValue
        val type = jsonObject.type
        return updatables[keyTypeId(type, id)]?.let { updatable ->
            updatable.processors.forEach { it.process(jsonObject) }
            updatable.viewIndex
        }
    }

    private fun <T> List<T>.singleOrThrow(
        id: String?,
        subset: String?
    ): T? {
        if (size > 1) throw UiException.Default("Only one view factory can accept the component object $id $subset")
        return firstOrNull()
    }
}
