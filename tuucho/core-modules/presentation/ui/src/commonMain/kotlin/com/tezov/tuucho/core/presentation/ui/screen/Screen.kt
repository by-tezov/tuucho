package com.tezov.tuucho.core.presentation.ui.screen

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui._system.type
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.protocol.ContextualUpdaterProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol as DomainViewProtocol

internal class Screen(
    private val coroutineScopes: CoroutineScopesProtocol,
    override val route: NavigationRoute.Url
) : ScreenProtocol,
    TuuchoKoinComponent {
    private data class Updatable(
        val viewIndex: Int,
        val updaterProcessor: ContextualUpdaterProcessorProtocol
    )

    private val viewFactories: List<ViewFactoryProtocol> by lazy {
        getKoin().getAll()
    }

    private lateinit var rootView: ViewProtocol
    private val views = mutableListOf<ViewProtocol>()
    private val updatables = mutableMapOf<String, Updatable>()
    private val updateMutex = Mutex()

    private fun keyTypeId(
        type: String,
        id: String
    ) = "$type+$id"

    suspend fun initialize(
        componentObject: JsonObject
    ) {
        coroutineScopes.default.withContext {
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
            rootView = factory
                .process(screenContext = screenContext)
                .apply { initialize(componentObject) }
            screenContext.addView(rootView)
        }
    }

    private fun addView(
        view: ViewProtocol
    ) {
        views.add(view)
        val viewIndex = views.lastIndex
        view.contextualUpdater.forEach { updatable ->
            updatable.id?.let { id ->
                val keyTypeId = keyTypeId(updatable.type, id)
                if (this.updatables.contains(keyTypeId)) {
                    throw UiException.Default("Error, typeId $keyTypeId already exist")
                }
                this@Screen.updatables[keyTypeId] = Updatable(
                    viewIndex = viewIndex,
                    updaterProcessor = updatable
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <V : DomainViewProtocol> views(
        klass: KClass<V>
    ) = coroutineScopes.default.withContext {
        views.filter { klass.isInstance(it) } as List<V>
    }

    @Composable
    override fun display(
        scope: Any?
    ) {
        rootView.display(scope)
    }

    override suspend fun update(
        jsonObject: JsonObject
    ) {
        coroutineScopes.default.withContext {
            updateMutex.withLock {
                val updatedIndexView = updateAndReturnViewIndex(jsonObject)
                updatedIndexView?.let { views[it].updateIfNeeded() }
            }
        }
    }

    override suspend fun update(
        jsonObjects: List<JsonObject>
    ) {
        coroutineScopes.default.withContext {
            updateMutex.withLock {
                val updatedIndexViews = buildList {
                    jsonObjects.forEach {
                        updateAndReturnViewIndex(it)?.let(::add)
                    }
                }
                updatedIndexViews.forEach {
                    views[it].updateIfNeeded()
                }
            }
        }
    }

    private suspend fun updateAndReturnViewIndex(
        jsonObject: JsonObject
    ): Int? {
        val id = jsonObject.idValue
        val type = jsonObject.type
        return updatables[keyTypeId(type, id)]?.let {
            it.updaterProcessor.process(jsonObject)
            it.viewIndex
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
