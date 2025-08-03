package com.tezov.tuucho.core.presentation.ui.viewFactory._system

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema
import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema.contentOrNull
import com.tezov.tuucho.core.domain.model.schema.material.ContentSchema
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema.idSourceOrNull
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema.idValue
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema.type
import com.tezov.tuucho.core.presentation.ui.composable.shimmerComposable
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

abstract class ViewFactory : MatcherViewProtocol, KoinComponent {

    abstract fun process(url: String, componentObject: JsonObject): ViewProtocol

}

abstract class View(
    protected val url: String,
    componentObject: JsonObject
) : ViewProtocol {

    protected var isInitialized = false

    private val typeIds = mutableMapOf<String, String>()
    private val _canBeRendered = mutableStateOf(false)

    protected var componentObject = componentObject
        private set(value) {
            if(field.idValue != value.idValue) {
                throw UiException.Default("You can't change the id, new data set must have the same original id")
            }
            field = value
        }

    private fun keyOf(type: String?, id: String?) = "$type+$id"

    protected open fun canBeRendered(): Boolean {
        return componentObject.idSourceOrNull == null &&
                componentObject.contentOrNull?.idSourceOrNull == null
    }

    protected fun updateCanBeRendered() {
        _canBeRendered.value = canBeRendered()
    }

    fun init() {
        val componentScope = componentObject.withScope(ComponentSchema::Scope)
        addTypeId(TypeSchema.Value.component, id = componentObject.idValue)
        componentScope.content?.idValue?.let {
            addTypeId(TypeSchema.Value.content, id = it)
        }
        onInit()
        componentObject.processComponent()
        updateCanBeRendered()
        isInitialized = true
    }

    open fun onInit() {}

    final override fun update(jsonObject: JsonObject) {
        val id = jsonObject.idValue
        val type = jsonObject.type
        val key = typeIds[keyOf(type, id)] ?: return

        if (!typeIds.contains(keyOf(type, id))) return
        when (type) {
            TypeSchema.Value.component -> {
                componentObject = jsonObject
                jsonObject.processComponent()
            }

            TypeSchema.Value.content -> {
                componentObject = componentObject.withScope(ComponentSchema::Scope).apply {
                    content = jsonObject
                }.collect()
                jsonObject.processContent()
            }

            TypeSchema.Value.text -> {
                componentObject = componentObject.withScope(ComponentSchema::Scope).apply {
                    content?.let {
                        content = it.withScope(ContentSchema::Scope).apply {
                            this[key] = jsonObject
                        }.collect()
                    }
                }.collect()
                jsonObject.processText(key)
            }

            TypeSchema.Value.message -> jsonObject.processMessage()

            else -> throw UiException.Default("Unknown update type $type")
        }
        updateCanBeRendered()
    }

    protected fun addTypeId(type: String?, id: String?) {
        val typeId = keyOf(type, id)
        if (typeIds.contains(typeId)) {
            println("Warning, typeId $typeId already already")
        }
        typeIds.put(typeId, "")
    }

    protected fun addTypeIdForKey(type: String?, id: String?, key: String) {
        val typeId = keyOf(type, id)
        if (typeIds.contains(typeId)) {
            println("Warning, typeId $typeId already already exist for $key")
        }
        typeIds.put(typeId, key)
    }

    open fun JsonObject.processComponent() {}
    open fun JsonObject.processContent() {}
    open fun JsonObject.processStyle() {}
    open fun JsonObject.processOption() {}
    open fun JsonObject.processState() {}
    open fun JsonObject.processText(key: String) {}
    open fun JsonObject.processDimension(key: String) {}
    open fun JsonObject.processColor(key: String) {}
    open fun JsonObject.processMessage() {}

    private val canBeRendered get() = _canBeRendered.value

    @Composable
    final override fun display(scope: Any?) {
        if (canBeRendered) {
            displayComponent(scope)
        } else {
            displayPlaceholder(scope)
        }
    }

    @Composable
    protected abstract fun displayComponent(scope: Any?)

    @Composable
    protected open fun displayPlaceholder(scope: Any?) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .shimmerComposable(width = 1000f)
        )
    }

}
