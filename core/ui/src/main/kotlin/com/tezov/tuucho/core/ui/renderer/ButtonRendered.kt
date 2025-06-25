package com.tezov.tuucho.core.ui.renderer

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain.schema.ComponentSchema
import com.tezov.tuucho.core.domain.schema.TextSchema
import com.tezov.tuucho.core.domain.schema._element.button.ButtonSchema
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema.Companion.typeOrNull
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreen
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class ButtonRendered : Renderer() {

    private val renderer: MaterialRenderer by inject()

    override fun accept(jsonObject: JsonObject): Boolean {
        return jsonObject.typeOrNull == TypeSchema.Value.Type.component &&
                jsonObject.subsetOrNull == ButtonSchema.Value.subset
    }

    override fun process(jsonObject: JsonObject): ComposableScreen {
        val content = jsonObject[ComponentSchema.Key.content]!!.jsonObject
        val style = jsonObject[ComponentSchema.Key.style]!!.jsonObject

        val value = content[ButtonSchema.Key.value]!!.jsonObject
        val text = value[TextSchema.Key.default].string
        val action = content[ButtonSchema.Key.action].string

        return ButtonScreen(
            text = text,
            action = action
        )
    }
}

class ButtonScreen(
    var text: String,
    var action: String
) : ComposableScreen {

    @Composable
    override fun Any.show() {
        Button(onClick = {
            println(action)
        }) {
            Text(text = text)
        }
    }
}