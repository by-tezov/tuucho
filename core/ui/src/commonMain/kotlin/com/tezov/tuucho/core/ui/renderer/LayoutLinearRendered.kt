package com.tezov.tuucho.core.ui.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tezov.tuucho.core.domain.model.schema._system.Schema.Companion.schema
import com.tezov.tuucho.core.domain.model.schema.material.ColorSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LayoutLinearSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LayoutLinearSchema.Style.Value.Orientation
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.ui._system.toColorOrNull
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class LayoutLinearRendered : Renderer() {

    private val renderer: ScreenRendererProtocol by inject()

    override fun accept(element: JsonElement) = element.schema()
        .let {
            it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                    it.withScope(SubsetSchema::Scope).self == LayoutLinearSchema.Component.Value.subset
        }

    override fun process(element: JsonElement): ComposableScreenProtocol {
        val schema = element.schema()
        val content = schema.onScope(LayoutLinearSchema.Content::Scope)
        val style = schema.onScope(LayoutLinearSchema.Style::Scope)

        val backgroundColor = style.backgroundColor?.schema()
            ?.withScope(ColorSchema::Scope)?.default //TODO manage "selector",not only default

        val items = content.items
            ?.mapNotNull { renderer.process(it) as? ComposableScreenProtocol }
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