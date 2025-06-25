package com.tezov.tuucho.core.ui.renderer

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain.schema.ComponentSchema
import com.tezov.tuucho.core.domain.schema.TextSchema
import com.tezov.tuucho.core.domain.schema._element.label.LabelSchema
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema.Companion.typeOrNull
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreen
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class LabelRendered : Renderer() {

    override fun accept(jsonObject: JsonObject): Boolean {
        return jsonObject.typeOrNull == TypeSchema.Value.Type.component &&
                jsonObject.subsetOrNull == LabelSchema.Value.subset
    }

    override fun process(jsonObject: JsonObject): ComposableScreen {
        val content = jsonObject[ComponentSchema.Key.content]!!.jsonObject
        val style = jsonObject[ComponentSchema.Key.style]!!.jsonObject

        val value = content[LabelSchema.Name.Key]!!.jsonObject
        val text = value[TextSchema.Key.default].string

        return LabelScreen(
            text = text
        )
    }
}

class LabelScreen(
    var text: String
) : ComposableScreen {

    @Composable
    override fun Any.show() {
        Text(text = text)
    }
}