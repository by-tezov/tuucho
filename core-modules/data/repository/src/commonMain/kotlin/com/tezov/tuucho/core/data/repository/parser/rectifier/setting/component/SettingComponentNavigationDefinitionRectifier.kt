package com.tezov.tuucho.core.data.repository.parser.rectifier.setting.component

import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.AbstractRectifier
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema.Spec.Value.DirectionNavigation
import com.tezov.tuucho.core.domain.business.jsonSchema.material.componentSetting.navigationSchema.SettingComponentNavigationTransitionSchema.Spec.Value.DirectionScreen
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSelectorSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

@OptIn(TuuchoExperimentalAPI::class)
class SettingComponentNavigationDefinitionRectifier : AbstractRectifier() {
    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean = path.lastSegmentIs(ComponentSettingNavigationSchema.Key.definition)

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ) = buildList {
        add(element.find(path).jsonObject)
    }.let(::JsonArray)

    override fun afterAlterArray(
        path: JsonElementPath,
        element: JsonElement,
    ) = element
        .find(path)
        .jsonArray
        .map {
            afterAlterObject("".toPath(), it) ?: it
        }.let(::JsonArray)

    override fun afterAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? {
        val definitionScope = element
            .find(path)
            .withScope(ComponentSettingNavigationSchema.Definition::Scope)
        definitionScope.selector
            ?.rectifySelector()
            ?.let { definitionScope.selector = it }
        definitionScope[ComponentSettingNavigationSchema.Definition.Key.transition]
            ?.rectifyTransition()
            ?.let { definitionScope.transition = it }
        return definitionScope.collectChangedOrNull()
    }

    private fun JsonObject.rectifySelector(): JsonObject? {
        withScope(ComponentSettingNavigationSelectorSchema::Scope).apply {
            val value = (element as? JsonObject)?.get(ComponentSettingNavigationSelectorSchema.Key.value)
            if (value is JsonPrimitive) {
                this.values = JsonArray(listOf(value))
                return collect()
            }
            val values = (element as? JsonObject)?.get(ComponentSettingNavigationSelectorSchema.Key.values)
            if (values is JsonPrimitive) {
                this.values = JsonArray(listOf(values))
                return collect()
            }
        }
        return null
    }

    private fun JsonElement.rectifyTransition(): JsonObject? {
        if (this is JsonPrimitive) {
            return withScope(::SchemaScope)
                .apply {
                    this[SettingComponentNavigationTransitionSchema.Spec.Key.type] = this.element
                }.collect()
                .rectifyTransition()
        }
        val scope = withScope(SettingComponentNavigationTransitionSchema::Scope)
        val remainingMap = scope.element.jsonObject -
            SettingComponentNavigationTransitionSchema.Key.forward -
            SettingComponentNavigationTransitionSchema.Key.backward
        return scope
            .apply {
                (this[SettingComponentNavigationTransitionSchema.Key.forward] ?: JsonObject(emptyMap()))
                    .rectifyTransitionSet(buildMap {
                        putAll(remainingMap)
                        put(
                            SettingComponentNavigationTransitionSchema.Spec.Key.directionNavigation,
                            JsonPrimitive(DirectionNavigation.forward)
                        )
                    })
                    ?.let { forward = it }
                (this[SettingComponentNavigationTransitionSchema.Key.backward] ?: JsonObject(emptyMap()))
                    .rectifyTransitionSet(buildMap {
                        putAll(remainingMap)
                        put(
                            SettingComponentNavigationTransitionSchema.Spec.Key.directionNavigation,
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
            return withScope(::SchemaScope)
                .apply {
                    this[SettingComponentNavigationTransitionSchema.Spec.Key.type] = this.element
                }.collect()
                .rectifyTransitionSet(remaining)
        }
        val scope = withScope(SettingComponentNavigationTransitionSchema.Set::Scope)
        val remainingMap = buildMap {
            putAll(remaining)
            putAll(
                scope.element.jsonObject -
                    SettingComponentNavigationTransitionSchema.Set.Key.enter -
                    SettingComponentNavigationTransitionSchema.Set.Key.exit
            )
        }
        return scope
            .apply {
                (this[SettingComponentNavigationTransitionSchema.Set.Key.enter] ?: JsonObject(emptyMap()))
                    .rectifyTransitionSpec(buildMap {
                        putAll(remainingMap)
                        put(
                            SettingComponentNavigationTransitionSchema.Spec.Key.directionScreen,
                            JsonPrimitive(DirectionScreen.enter)
                        )
                    })
                    ?.let { enter = it }
                (this[SettingComponentNavigationTransitionSchema.Set.Key.exit] ?: JsonObject(emptyMap()))
                    .rectifyTransitionSpec(buildMap {
                        putAll(remainingMap)
                        put(
                            SettingComponentNavigationTransitionSchema.Spec.Key.directionScreen,
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
            return withScope(::SchemaScope)
                .apply {
                    this[SettingComponentNavigationTransitionSchema.Spec.Key.type] = this.element
                }.collect()
                .rectifyTransitionSpec(remaining)
        }
        return withScope(SettingComponentNavigationTransitionSchema.Spec::Scope)
            .apply {
                remaining.forEach { (key, value) ->
                    if (!contains(key)) {
                        this[key] = value
                    }
                }
            }.collectChangedOrNull()
    }
}
