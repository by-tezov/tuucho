@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui._system

import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ComponentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

val JsonElement.idValue
    get() = idValueOrNull ?: throw UiException.Default("id value is null for $this")

val JsonElement.idValueOrNull
    get() = onScope(IdSchema::Scope).value

val JsonElement.idSourceOrNull
    get() = onScope(IdSchema::Scope).source

val JsonObject.type
    get() = withScope(TypeSchema::Scope).self
        ?: throw UiException.Default("type value is null for $this")

val JsonObject.subsetOrNull
    get() = withScope(SubsetSchema::Scope).self

val JsonObject.subset
    get() = subsetOrNull ?: throw UiException.Default("subset value is null for $this")

val JsonObject.contentOrNull
    get() = withScope(ComponentSchema::Scope).content

val JsonObject.styleOrNull
    get() = withScope(ComponentSchema::Scope).style

val JsonObject.optionOrNull
    get() = withScope(ComponentSchema::Scope).option

val JsonObject.stateOrNull
    get() = withScope(ComponentSchema::Scope).state

val JsonObject.messageOrNull
    get() = withScope(ComponentSchema::Scope).message
