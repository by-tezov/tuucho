package com.tezov.tuucho.core.data.parser.rectifier.setting

import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationSelectorSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema.Spec.Value.DirectionNavigation
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.navigationSchema.SettingNavigationTransitionSchema.Spec.Value.DirectionScreen
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class SettingNavigationDefinitionRectifier : Rectifier() {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        return path.lastSegmentIs(SettingNavigationSchema.Key.definition)
    }

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList {
        add(element.find(path).jsonObject)
    }.let(::JsonArray)

    override fun afterAlterArray(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).jsonArray.map {
        afterAlterObject("".toPath(), it) ?: it
    }.let(::JsonArray)

    override fun afterAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? {
        val definitionScope = element.find(path)
            .withScope(SettingNavigationSchema.Definition::Scope)
        definitionScope.selector
            ?.rectifySelector()
            ?.let { definitionScope.selector = it }
        definitionScope[SettingNavigationSchema.Definition.Key.transition]
            ?.rectifyTransition()
            ?.let { definitionScope.transition = it }
        return definitionScope.collectChangedOrNull()
    }

    private fun JsonObject.rectifySelector(): JsonObject? {
        withScope(SettingNavigationSelectorSchema::Scope).apply {
            val value = (element as? JsonObject)?.get(SettingNavigationSelectorSchema.Key.value)
            if (value is JsonPrimitive) {
                this.values = JsonArray(listOf(value))
                return collect()
            }
            val values = (element as? JsonObject)?.get(SettingNavigationSelectorSchema.Key.values)
            if (values is JsonPrimitive) {
                this.values = JsonArray(listOf(values))
                return collect()
            }
        }
        return null
    }

    private fun JsonElement.rectifyTransition(): JsonObject? {
        if (this is JsonPrimitive) {
            return withScope(::SchemaScope).apply {
                this[SettingNavigationTransitionSchema.Spec.Key.type] = this.element
            }.collect().rectifyTransition()
        }
        val scope = withScope(SettingNavigationTransitionSchema::Scope)
        val remainingMap = scope.element.jsonObject -
                SettingNavigationTransitionSchema.Key.forward -
                SettingNavigationTransitionSchema.Key.backward
        return scope.apply {
            (this[SettingNavigationTransitionSchema.Key.forward] ?: JsonObject(emptyMap()))
                .rectifyTransitionSet(buildMap {
                    putAll(remainingMap)
                    put(
                        SettingNavigationTransitionSchema.Spec.Key.directionNavigation,
                        JsonPrimitive(DirectionNavigation.forward)
                    )
                })
                ?.let { forward = it }
            (this[SettingNavigationTransitionSchema.Key.backward] ?: JsonObject(emptyMap()))
                .rectifyTransitionSet(buildMap {
                    putAll(remainingMap)
                    put(
                        SettingNavigationTransitionSchema.Spec.Key.directionNavigation,
                        JsonPrimitive(DirectionNavigation.backward)
                    )
                })
                ?.let { backward = it }
            remainingMap.forEach { (key, _) -> remove(key) }
        }.collectChangedOrNull()
    }

    private fun JsonElement.rectifyTransitionSet(
        remaining: Map<String, JsonElement>,
    ): JsonObject? {
        if (this is JsonPrimitive) {
            return withScope(::SchemaScope).apply {
                this[SettingNavigationTransitionSchema.Spec.Key.type] = this.element
            }.collect().rectifyTransitionSet(remaining)
        }
        val scope = withScope(SettingNavigationTransitionSchema.Set::Scope)
        val remainingMap = buildMap {
            putAll(remaining)
            putAll(
                scope.element.jsonObject -
                        SettingNavigationTransitionSchema.Set.Key.enter -
                        SettingNavigationTransitionSchema.Set.Key.exit
            )
        }
        return scope.apply {
            (this[SettingNavigationTransitionSchema.Set.Key.enter] ?: JsonObject(emptyMap()))
                .rectifyTransitionSpec(buildMap {
                    putAll(remainingMap)
                    put(
                        SettingNavigationTransitionSchema.Spec.Key.directionScreen,
                        JsonPrimitive(DirectionScreen.enter)
                    )
                })
                ?.let { enter = it }
            (this[SettingNavigationTransitionSchema.Set.Key.exit] ?: JsonObject(emptyMap()))
                .rectifyTransitionSpec(buildMap {
                    putAll(remainingMap)
                    put(
                        SettingNavigationTransitionSchema.Spec.Key.directionScreen,
                        JsonPrimitive(DirectionScreen.exit)
                    )
                })
                ?.let { exit = it }
            remainingMap.forEach { (key, _) -> remove(key) }
        }.collectChangedOrNull()
    }

    private fun JsonElement.rectifyTransitionSpec(
        remaining: Map<String, JsonElement>,
    ): JsonObject? {
        if (this is JsonPrimitive) {
            return withScope(::SchemaScope).apply {
                this[SettingNavigationTransitionSchema.Spec.Key.type] = this.element
            }.collect().rectifyTransitionSpec(remaining)
        }
        return withScope(SettingNavigationTransitionSchema.Spec::Scope).apply {
            remaining.forEach { (key, value) ->
                if (!contains(key)) {
                    this[key] = value
                }
            }
        }.collectChangedOrNull()
    }

}
