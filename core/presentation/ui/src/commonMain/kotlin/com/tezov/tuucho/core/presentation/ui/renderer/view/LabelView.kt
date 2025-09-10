package com.tezov.tuucho.core.presentation.ui.renderer.view

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ColorSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ComponentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ComponentSchema.contentOrNull
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.idSourceOrNull
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.idValue
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.LabelSchema
import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.tool.json.string
import com.tezov.tuucho.core.presentation.ui._system.toColorOrNull
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.AbstractViewFactory
import kotlinx.serialization.json.JsonObject

class LabelViewFactory : AbstractViewFactory() {

    override fun accept(componentElement: JsonObject) = componentElement.let {
        it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                it.withScope(SubsetSchema::Scope).self == LabelSchema.Component.Value.subset
    }

    override suspend fun process(
        route: NavigationRoute,
        componentObject: JsonObject,
    ) =
        LabelView(
            componentObject = componentObject
        ).also { it.init() }
}

class LabelView(
    componentObject: JsonObject,
) : AbstractView(componentObject) {

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

    override suspend fun JsonObject.processComponent() {
        withScope(ComponentSchema::Scope).run {
            content?.processContent()
            style?.processStyle()
        }
    }

    override suspend fun JsonObject.processContent() {
        withScope(LabelSchema.Content::Scope).run {
            value?.processText(LabelSchema.Content.Key.value)
        }
    }

    override suspend fun JsonObject.processStyle() {
        withScope(LabelSchema.Style::Scope).run {
            fontColor?.processColor(LabelSchema.Style.Key.fontColor)
            fontSize?.processDimension(LabelSchema.Style.Key.fontSize)
        }
    }

    override suspend fun JsonObject.processText(key: String) {
        when (key) {
            LabelSchema.Content.Key.value -> _value.value = this
        }
    }

    override suspend fun JsonObject.processColor(key: String) {
        when (key) {
            LabelSchema.Style.Key.fontColor -> _fontColor = this
        }
    }

    override suspend fun JsonObject.processDimension(key: String) {
        when (key) {
            LabelSchema.Style.Key.fontSize -> _fontSize = this
        }
    }

    private val value
        @Composable get():String {
            //TODO language retrieve by Composition Local
            // selector for multiple text ?
            return _value.value?.get(LanguageModelDomain.Default.code)?.string ?: ""
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
