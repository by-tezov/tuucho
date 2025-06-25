package com.tezov.tuucho.core.ui.renderer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.schema.ComponentSchema
import com.tezov.tuucho.core.domain.schema.StyleSchema
import com.tezov.tuucho.core.domain.schema._element.layout.LayoutLinearSchema
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema.Companion.typeOrNull
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreen
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class LayoutLinearRendered : Renderer() {

    private val renderer: MaterialRenderer by inject()

    override fun accept(jsonObject: JsonObject): Boolean {
        return jsonObject.typeOrNull == TypeSchema.Value.Type.component &&
                jsonObject.subsetOrNull == LayoutLinearSchema.Default.subset
    }

    override fun process(jsonObject: JsonObject): ComposableScreen {
        val content = jsonObject[ComponentSchema.Key.content]!!.jsonObject
        val children = content[LayoutLinearSchema.Key.items]!!.jsonArray
            .mapNotNull {
                renderer.process(it.jsonObject)
            }

        val style = jsonObject[ComponentSchema.Key.style]!!.jsonObject
        val orientation = style[StyleSchema.Key.orientation].stringOrNull
        return when (orientation) {
            StyleSchema.Value.Orientation.horizontal -> LayoutLinearScreen.Horizontal(
                children = children
            )

            else -> LayoutLinearScreen.Vertical(
                children = children
            )
        }

    }
}

sealed class LayoutLinearScreen(
    var children: List<ComposableScreen>
) : ComposableScreen {

    class Vertical(
        children: List<ComposableScreen>
    ) : LayoutLinearScreen(children) {

        @Composable
        override fun Any.show() {
            Column {
                children.forEach { it.apply { this@Column.show() } }
            }
        }
    }

    class Horizontal(
        children: List<ComposableScreen>
    ) : LayoutLinearScreen(children) {

        @Composable
        override fun Any.show() {
            Row {
                children.forEach { it.apply { this@Row.show() } }
            }
        }
    }

}