package com.tezov.tuucho.core.presentation.ui.viewFactory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.ColorSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.ComponentSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.model.schema.material._element.layout.LayoutLinearSchema
import com.tezov.tuucho.core.domain.business.model.schema.material._element.layout.LayoutLinearSchema.Style.Value.Orientation
import com.tezov.tuucho.core.presentation.tool.modifier.onTrue
import com.tezov.tuucho.core.presentation.tool.modifier.then
import com.tezov.tuucho.core.presentation.tool.modifier.thenOnNotNull
import com.tezov.tuucho.core.presentation.ui._system.toColorOrNull
import com.tezov.tuucho.core.presentation.ui.viewFactory._system.View
import com.tezov.tuucho.core.presentation.ui.viewFactory._system.ViewFactory
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class LayoutLinearViewFactory : ViewFactory() {

    private val componentRendererFactory: ComponentRendererFactory by inject()

    override fun accept(componentElement: JsonObject) = componentElement.let {
        it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                it.withScope(SubsetSchema::Scope).self == LayoutLinearSchema.Component.Value.subset
    }

    override fun process(url: String, componentObject: JsonObject) = LayoutLinearView(
        url = url,
        componentElement = componentObject,
        componentRendererFactory = componentRendererFactory,
    ).also { it.init() }
}

class LayoutLinearView(
    url: String,
    componentElement: JsonObject,
    private val componentRendererFactory: ComponentRendererFactory,
) : View(url, componentElement) {

    private var _orientation: String? = null
    private var _fillMaxSize: Boolean? = null
    private var _fillMaxWidth: Boolean? = null
    private var _backgroundColor: JsonObject? = null
    private val _items = mutableStateOf<List<View>?>(null)

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
                    componentRendererFactory.process(url, it.jsonObject) as? View
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

    private val items get():List<View>? = _items.value

    @Composable
    override fun displayComponent(scope: Any?) {
        val modifier = Modifier
            .then {
                onTrue(fillMaxSize) { fillMaxSize() } or onTrue(fillMaxWidth) { fillMaxWidth() }
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