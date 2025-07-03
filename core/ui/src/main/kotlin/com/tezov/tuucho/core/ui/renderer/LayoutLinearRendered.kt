package com.tezov.tuucho.core.ui.renderer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.model.schema._system.Schema.Companion.schema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LayoutLinearSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LayoutLinearSchema.Style.Value.Orientation
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
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

        val items = content.items
            ?.mapNotNull { renderer.process(it) as? ComposableScreenProtocol }
            ?: emptyList()

        return when (style.orientation) {
            Orientation.horizontal -> LayoutLinearScreen.Horizontal(item = items)
            else -> LayoutLinearScreen.Vertical(item = items)
        }
    }
}

sealed class LayoutLinearScreen(
    var children: List<ComposableScreenProtocol>,
) : ComposableScreenProtocol() {

    class Vertical(
        item: List<ComposableScreenProtocol>,
    ) : LayoutLinearScreen(item) {

        @Composable
        override fun show(scope: Any?) {
            Column {
                children.forEach { it.show(this@Column) }
            }
        }
    }

    class Horizontal(
        item: List<ComposableScreenProtocol>,
    ) : LayoutLinearScreen(item) {

        @Composable
        override fun show(scope: Any?) {
            Row {
                children.forEach { it.show(this@Row) }
            }
        }
    }

}