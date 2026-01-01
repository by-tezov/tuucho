package com.tezov.tuucho.core.presentation.ui.screen

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui._system.type
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProcessorProtocol
import com.tezov.tuucho.core.presentation.ui.view._system.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view._system.ViewProtocol
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol as DomainViewProtocol

class ScreenContext(
    override val route: NavigationRoute,
    private val addViewBlock: (view: ViewProtocol) -> Unit
) : ScreenContextProtocol {
    override fun addView(
        view: ViewProtocol
    ) = addViewBlock(view)
}

private data class Updatable(
    val viewIndex: Int,
    val updatableProcessor: UpdatableProcessorProtocol
)

class Screen(
    override val route: NavigationRoute.Url
) : ScreenProtocol,
    TuuchoKoinComponent {
    private val viewFactories: List<ViewFactoryProtocol> by lazy {
        getKoin().getAll()
    }

    private lateinit var rootView: ViewProtocol
    private val views = mutableListOf<ViewProtocol>()
    private val updatables = mutableMapOf<String, Updatable>()

    private fun keyTypeId(
        type: String,
        id: String
    ) = "$type+$id"

    suspend fun initialize(
        componentObject: JsonObject
    ) {
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

    private fun addView(
        view: ViewProtocol
    ) {
        views.add(view)
        val viewIndex = views.lastIndex
        view.updatables.forEach { updatable ->
            updatable.id?.let { id ->
                val keyTypeId = keyTypeId(updatable.type, id)
                if (this@Screen.updatables.contains(keyTypeId)) {
                    throw UiException.Default("Error, typeId $keyTypeId already exist")
                }
                this@Screen.updatables[keyTypeId] = Updatable(
                    viewIndex = viewIndex,
                    updatableProcessor = updatable
                )
            }
        }
    }

    @Composable
    override fun display(
        scope: Any?
    ) {
        rootView.display(scope)
    }

    private suspend fun updateAndReturnViewIndex(
        jsonObject: JsonObject
    ): Int? {
        val id = jsonObject.idValue
        val type = jsonObject.type
        return updatables[keyTypeId(type, id)]?.let {
            it.updatableProcessor.process(jsonObject)
            it.viewIndex
        }
    }

    override suspend fun update(
        jsonObject: JsonObject
    ) {
        val updatedIndexView = updateAndReturnViewIndex(jsonObject)
        updatedIndexView?.let { views[it].updateIfNeeded() }
    }

    override suspend fun update(
        jsonObjects: List<JsonObject>
    ) {
        val updatedIndexViews = buildList {
            jsonObjects.forEach {
                updateAndReturnViewIndex(it)?.let(::add)
            }
        }
        updatedIndexViews.forEach {
            views[it].updateIfNeeded()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V : DomainViewProtocol> views(
        klass: KClass<V>
    ) = views.filter { klass.isInstance(it) } as List<V>

    private fun <T> List<T>.singleOrThrow(
        id: String?,
        subset: String?
    ): T? {
        if (size > 1) throw UiException.Default("Only one view factory can accept the component object $id $subset")
        return firstOrNull()
    }
}
