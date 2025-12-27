package com.tezov.tuucho.core.presentation.ui.screen

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui._system.type
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import com.tezov.tuucho.core.presentation.ui.view._system.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view._system.ViewProtocol
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol as DomainViewProtocol

class Screen(
    override val route: NavigationRoute.Url,
    override val componentObject: JsonObject,
) : ScreenProtocol,
    TuuchoKoinComponent {
    private val viewFactories: List<ViewFactoryProtocol> by lazy {
        getKoin().getAll()
    }

    private lateinit var rootView: ViewProtocol
    private val views = mutableListOf<ViewProtocol>()
    private val updatables = mutableMapOf<String, UpdatableProtocol>()

    private fun keyTypeId(
        type: String,
        id: String
    ) = "$type+$id"

    suspend fun prepare() {
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
        rootView = factory.process(
            screen = this,
            path = ROOT_PATH
        )
    }

    fun addView(
        view: ViewProtocol
    ) {
        views.add(view)
        view.updatables.forEach { updatable ->
            updatable.id?.let { id ->
                val keyTypeId = keyTypeId(updatable.type, id)
                if (this@Screen.updatables.contains(keyTypeId)) {
                    throw UiException.Default("Warning, typeId $keyTypeId already exist")
                }
                this@Screen.updatables[keyTypeId] = updatable
            }
        }
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
        val id = jsonObject.idValue
        val type = jsonObject.type
        println("|> update ${keyTypeId(type, id)} $jsonObject")

        updatables[keyTypeId(type, id)]?.process(jsonObject)
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
