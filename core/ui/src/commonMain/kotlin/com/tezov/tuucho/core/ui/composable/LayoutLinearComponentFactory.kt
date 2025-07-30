package com.tezov.tuucho.core.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.ColorSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LayoutLinearSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LayoutLinearSchema.Style.Value.Orientation
import com.tezov.tuucho.core.ui._system.toColorOrNull
import com.tezov.tuucho.core.ui.composable._system.ComposableScreenProtocol
import com.tezov.tuucho.core.ui.composable._system.UiComponentFactory
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class LayoutLinearRendered : UiComponentFactory() {

    private val componentRendered: MaterialUiComponentFactory by inject()

    override fun accept(componentElement: JsonObject) = componentElement
        .let {
            it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                    it.withScope(SubsetSchema::Scope).self == LayoutLinearSchema.Component.Value.subset
        }

    override fun process(componentElement: JsonObject): ComposableScreenProtocol {
        val content = componentElement.onScope(LayoutLinearSchema.Content::Scope)
        val style = componentElement.onScope(LayoutLinearSchema.Style::Scope)

        val backgroundColor = style.backgroundColor
            ?.withScope(ColorSchema::Scope)?.default //TODO manage "selector",not only default

        val items = content.items
            ?.mapNotNull { componentRendered.process(it as JsonObject) as? ComposableScreenProtocol }
            ?: emptyList()

        return when (style.orientation) {
            Orientation.horizontal -> LayoutLinearScreen.Horizontal(
                fillMaxSize = style.fillMaxSize,
                fillMaxWidth = style.fillMaxWidth,
                backgroundColor = backgroundColor?.toColorOrNull(),
                item = items,
            )

            else -> LayoutLinearScreen.Vertical(
                fillMaxSize = style.fillMaxSize,
                fillMaxWidth = style.fillMaxWidth,
                backgroundColor = backgroundColor?.toColorOrNull(),
                item = items
            )
        }
    }
}

sealed class LayoutLinearScreen(
    var fillMaxSize: Boolean?,
    var fillMaxWidth: Boolean?,
    var backgroundColor: Color?,
    var children: List<ComposableScreenProtocol>,
) : ComposableScreenProtocol() {

    class Vertical(
        fillMaxSize: Boolean?,
        fillMaxWidth: Boolean?,
        backgroundColor: Color?,
        item: List<ComposableScreenProtocol>,
    ) : LayoutLinearScreen(fillMaxSize, fillMaxWidth, backgroundColor, item) {

        @Composable
        override fun show(scope: Any?) {
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
                children.forEach { it.show(this@Column) }
            }
        }
    }

    class Horizontal(
        fillMaxSize: Boolean?,
        fillMaxWidth: Boolean?,
        backgroundColor: Color?,
        item: List<ComposableScreenProtocol>,
    ) : LayoutLinearScreen(fillMaxSize, fillMaxWidth, backgroundColor, item) {

        @Composable
        override fun show(scope: Any?) {
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
                children.forEach { it.show(this@Row) }
            }
        }
    }

}