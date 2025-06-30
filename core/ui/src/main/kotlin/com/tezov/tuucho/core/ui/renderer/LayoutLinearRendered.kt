package com.tezov.tuucho.core.ui.renderer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.schema.ComponentSchema.Companion.contentObject
import com.tezov.tuucho.core.domain.schema.ComponentSchema.Companion.styleObject
import com.tezov.tuucho.core.domain.schema.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.TypeSchema
import com.tezov.tuucho.core.domain.schema.TypeSchema.Companion.typeOrNull
import com.tezov.tuucho.core.domain.schema._element.LayoutLinearSchema
import com.tezov.tuucho.core.domain.schema._element.LayoutLinearSchema.Content.itemsArray
import com.tezov.tuucho.core.domain.schema._element.LayoutLinearSchema.Style.Value.Orientation
import com.tezov.tuucho.core.domain.schema._element.LayoutLinearSchema.Style.orientation
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class LayoutLinearRendered : Renderer() {

    private val renderer: ScreenRendererProtocol by inject()

    override fun accept(jsonObject: JsonObject): Boolean {
        return jsonObject.typeOrNull == TypeSchema.Value.Type.component &&
                jsonObject.subsetOrNull == LayoutLinearSchema.Component.Value.subset
    }

    override fun process(jsonObject: JsonObject): ComposableScreenProtocol {
        val content = jsonObject.contentObject
        val style = jsonObject.styleObject

        val items = content.itemsArray
            .mapNotNull {
                renderer.process(it.jsonObject) as? ComposableScreenProtocol
            }

        return when (style.orientation) {
            Orientation.horizontal -> LayoutLinearScreen.Horizontal(
                item = items
            )

            else -> LayoutLinearScreen.Vertical(
                item = items
            )
        }
    }
}

sealed class LayoutLinearScreen(
    var children: List<ComposableScreenProtocol>
) : ComposableScreenProtocol() {

    class Vertical(
        item: List<ComposableScreenProtocol>
    ) : LayoutLinearScreen(item) {

        @Composable
        override fun show(scope: Any?) {
            Column {
                children.forEach { it.show(this@Column) }
            }
        }
    }

    class Horizontal(
        item: List<ComposableScreenProtocol>
    ) : LayoutLinearScreen(item) {

        @Composable
        override fun show(scope: Any?) {
            Row {
                children.forEach { it.show(this@Row) }
            }
        }
    }

}