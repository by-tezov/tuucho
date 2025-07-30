package com.tezov.tuucho.core.ui.uiComponentFactory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.ColorSchema
import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LayoutLinearSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LayoutLinearSchema.Style.Value.Orientation
import com.tezov.tuucho.core.ui._system.toColorOrNull
import com.tezov.tuucho.core.ui.uiComponentFactory._system.Screen
import com.tezov.tuucho.core.ui.uiComponentFactory._system.UiComponentFactory
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class LayoutLinearUiComponentFactory : UiComponentFactory() {

    private val materialUiComponentFactory: MaterialUiComponentFactory by inject()

    override fun accept(componentElement: JsonObject) = componentElement.let {
        it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                it.withScope(SubsetSchema::Scope).self == LayoutLinearSchema.Component.Value.subset
    }

    override fun process(url: String, componentElement: JsonObject) = LayoutLinearScreen(
        url = url,
        componentElement = componentElement,
        materialUiComponentFactory = materialUiComponentFactory,
    ).also { it.init() }
}

class LayoutLinearScreen(
    url: String,
    componentElement: JsonObject,
    private val materialUiComponentFactory: MaterialUiComponentFactory,
) : Screen(url, componentElement) {

    private var _orientation: String? = null
    private var _fillMaxSize: Boolean? = null
    private var _fillMaxWidth: Boolean? = null
    private var _backgroundColor: JsonObject? = null
    private val _items = mutableStateOf<List<Screen>?>(null)

    override fun JsonObject.processComponent() {
        withScope(ComponentSchema::Scope).run {
            content?.processContent()
            style?.processStyle()
        }
    }

    override fun JsonObject.processContent() {
        withScope(LayoutLinearSchema.Content::Scope).run {
            items?.let { items ->
                _items.value = items.mapNotNull {
                    materialUiComponentFactory.process(url, it.jsonObject) as? Screen
                }
            }
        }
    }

    override fun JsonObject.processStyle() {
        withScope(LayoutLinearSchema.Style::Scope).run {
            backgroundColor?.processColor(LayoutLinearSchema.Style.Key.backgroundColor)
            orientation?.let { _orientation = it }
            fillMaxSize?.let { _fillMaxSize = it }
            fillMaxWidth?.let { _fillMaxWidth = it }
        }
    }

    override fun JsonObject.processColor(key: String) {
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
        get():Color? {
            return _backgroundColor?.withScope(ColorSchema::Scope)
                ?.default?.toColorOrNull()
        }

    private val items get():List<Screen>? = _items.value

    @Composable
    override fun showComponent(scope: Any?) {

        when (orientation) {
            Orientation.horizontal -> {
                var modifier: Modifier = Modifier
                if (fillMaxSize == true) {
                    modifier = modifier.fillMaxSize()
                } else if (fillMaxWidth == true) {
                    modifier = modifier.fillMaxWidth()
                }
                backgroundColor?.let {
                    modifier = modifier.background(it)
                }
                Row(
                    modifier = modifier
                ) {
                    items?.forEach { it.show(this@Row) }
                }
            }

            else -> {
                var modifier: Modifier = Modifier
                if (fillMaxSize == true) {
                    modifier = modifier.fillMaxSize()
                } else if (fillMaxWidth == true) {
                    modifier = modifier.fillMaxWidth()
                }
                backgroundColor?.let {
                    modifier = modifier.background(it)
                }
                Column(
                    modifier = modifier
                ) {
                    items?.forEach { it.show(this@Column) }
                }
            }
        }
    }


}