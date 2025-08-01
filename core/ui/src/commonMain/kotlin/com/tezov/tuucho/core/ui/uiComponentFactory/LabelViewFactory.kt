package com.tezov.tuucho.core.ui.uiComponentFactory

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain.config.Language
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.ColorSchema
import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema
import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema.contentOrNull
import com.tezov.tuucho.core.domain.model.schema.material.DimensionSchema
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema.idSourceOrNull
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema.idValue
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LabelSchema
import com.tezov.tuucho.core.ui._system.toColorOrNull
import com.tezov.tuucho.core.ui.uiComponentFactory._system.View
import com.tezov.tuucho.core.ui.uiComponentFactory._system.ViewFactory
import kotlinx.serialization.json.JsonObject

class LabelViewFactory() : ViewFactory() {

    override fun accept(componentElement: JsonObject) = componentElement.let {
        it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                it.withScope(SubsetSchema::Scope).self == LabelSchema.Component.Value.subset
    }

    override fun process(url: String, componentObject: JsonObject) =
        LabelView(url, componentObject)
            .also { it.init() }
}

class LabelView(
    url: String,
    componentObject: JsonObject
) : View(url, componentObject) {

    private val _value = mutableStateOf<JsonObject?>(null)
    private var _fontColor: JsonObject? = null
    private var _fontSize: JsonObject? = null

    override fun canBeRendered() = super.canBeRendered() &&
            componentObject.contentOrNull
                ?.withScope(LabelSchema.Content::Scope)
                ?.value?.idSourceOrNull == null

    override fun onInit() {
        componentObject.contentOrNull?.withScope(LabelSchema.Content::Scope)?.run {
            value?.idValue?.let {
                addTypeIdForKey(
                    type = TypeSchema.Value.text,
                    id = it,
                    key = LabelSchema.Content.Key.value
                )
            }
        }
    }

    override fun JsonObject.processComponent() {
        withScope(ComponentSchema::Scope).run {
            content?.processContent()
            style?.processStyle()
        }
    }

    override fun JsonObject.processContent() {
        withScope(LabelSchema.Content::Scope).run {
            value?.processText(LabelSchema.Content.Key.value)
        }
    }

    override fun JsonObject.processStyle() {
        withScope(LabelSchema.Style::Scope).run {
            fontColor?.processColor(LabelSchema.Style.Key.fontColor)
            fontSize?.processDimension(LabelSchema.Style.Key.fontSize)
        }
    }

    override fun JsonObject.processText(key: String) {
        when (key) {
            LabelSchema.Content.Key.value -> _value.value = this
        }
    }

    override fun JsonObject.processColor(key: String) {
        when (key) {
            LabelSchema.Style.Key.fontColor -> _fontColor = this
        }
    }

    override fun JsonObject.processDimension(key: String) {
        when (key) {
            LabelSchema.Style.Key.fontSize -> _fontSize = this
        }
    }

    private val value
        @Composable get():String {
            //TODO language retrieve by Composition Local
            // selector for multiple text ?
            return _value.value?.get(Language.Default.code)?.string ?: ""
        }

    private val fontColor
        get():Color? {
            //TODO default selector ? how to do that ?
            return _fontColor?.withScope(ColorSchema::Scope)
                ?.default?.toColorOrNull()
        }

    private val fontSize
        get():TextUnit? {
            //TODO default selector ? how to do that ?
            return _fontSize?.withScope(DimensionSchema::Scope)
                ?.default?.toFloatOrNull()?.sp
        }

    @Composable
    override fun displayComponent(scope: Any?) {
        val textStyle = LocalTextStyle.current.let { current ->
            current.copy(
                color = fontColor ?: current.color,
                fontSize = fontSize ?: current.fontSize,
            )
        }
        Text(
            text = value,
            style = textStyle
        )
     }


}
