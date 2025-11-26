package com.tezov.tuucho.core.presentation.ui.renderer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ColorSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ComponentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.layout.LayoutLinearSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.layout.LayoutLinearSchema.Style.Value.Orientation
import com.tezov.tuucho.core.presentation.tool.modifier.then
import com.tezov.tuucho.core.presentation.tool.modifier.thenOnNotNull
import com.tezov.tuucho.core.presentation.ui._system.toColorOrNull
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.AbstractViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewProtocol
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class LayoutLinearViewFactory : AbstractViewFactory() {
    private val viewFactories: List<AbstractViewFactory> by inject()

    override fun accept(
        componentElement: JsonObject
    ) = componentElement.let {
        it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
            it.withScope(SubsetSchema::Scope).self == LayoutLinearSchema.Component.Value.subset
    }

    override suspend fun process(
        route: NavigationRoute.Url,
        componentObject: JsonObject,
    ) = LayoutLinearView(
        route = route,
        componentObject = componentObject,
        viewFactories = viewFactories,
    ).also { it.init() }
}

class LayoutLinearView(
    private val route: NavigationRoute.Url,
    componentObject: JsonObject,
    private val viewFactories: List<AbstractViewFactory>,
) : AbstractView(componentObject) {
    override val children: List<ViewProtocol>?
        get() = _itemViews.value

    private var _orientation: String? = null
    private var _fillMaxSize: Boolean? = null
    private var _fillMaxWidth: Boolean? = null
    private var _backgroundColor: JsonObject? = null
    private val _itemViews = mutableStateOf<List<ViewProtocol>?>(null)

    override suspend fun JsonObject.processComponent() {
        withScope(ComponentSchema::Scope).run {
            content?.processContent()
            style?.processStyle()
        }
    }

    override suspend fun JsonObject.processContent() {
        withScope(LayoutLinearSchema.Content::Scope).run {
            items?.let { itemsArray ->
                _itemViews.value?.let { itemsView ->
                    itemsView.forEachIndexed { index, itemView -> itemView.update(itemsArray[index].jsonObject) }
                } ?: run {
                    _itemViews.value = itemsArray.mapNotNull { item ->
                        viewFactories
                            .first { it.accept(item.jsonObject) }
                            .process(route, item.jsonObject)
                    }
                }

                // TODO
//                componentObject = componentObject.withScope(ComponentSchema::Scope).apply {
//                    content = content?.withScope(LayoutLinearSchema.Content::Scope).apply {
//                        remove(LayoutLinearSchema.Content.Key.items)
//                    }?.collect()
//                }.collect()
            }
        }
    }

    override suspend fun JsonObject.processStyle() {
        withScope(LayoutLinearSchema.Style::Scope).run {
            backgroundColor?.processColor(LayoutLinearSchema.Style.Key.backgroundColor)
            orientation?.let { _orientation = it }
            fillMaxSize?.let { _fillMaxSize = it }
            fillMaxWidth?.let { _fillMaxWidth = it }
        }
    }

    override suspend fun JsonObject.processColor(
        key: String
    ) {
        when (key) {
            LayoutLinearSchema.Style.Key.backgroundColor -> _backgroundColor = this
        }
    }

    private val orientation
        get() = _orientation ?: Orientation.vertical

    private val fillMaxSize
        get() = _fillMaxSize

    private val fillMaxWidth
        get() = _fillMaxWidth

    private val backgroundColor
        get(): Color? {
            return _backgroundColor
                ?.withScope(ColorSchema::Scope)
                ?.default
                ?.toColorOrNull()
        }

    private val items get(): List<ViewProtocol>? = _itemViews.value

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        val modifier = Modifier
            .then {
                ifTrue(fillMaxSize) {
                    fillMaxSize()
                } or ifTrue(fillMaxWidth) {
                    fillMaxWidth()
                }
            }
            .thenOnNotNull(backgroundColor) { background(it) }

        when (orientation) {
            Orientation.horizontal -> {
                Row(modifier = modifier) {
                    items?.forEach { it.display(this@Row) }
                }
            }

            else -> {
                Column(modifier = modifier) {
                    items?.forEach { it.display(this@Column) }
                }
            }
        }
    }
}
