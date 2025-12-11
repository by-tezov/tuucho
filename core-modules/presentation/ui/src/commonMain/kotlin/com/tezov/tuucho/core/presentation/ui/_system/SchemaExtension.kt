package com.tezov.tuucho.core.presentation.ui._system

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ComponentSchema.Scope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema.Scope
import kotlinx.serialization.json.JsonObject

val JsonObject.type
    get() = withScope(::Scope).self
        ?: throw DomainException.Default("type value is null for $this")

val JsonObject.contentOrNull
    get() = withScope(::Scope).content

val JsonObject.styleOrNull
    get() = withScope(::Scope).style

val JsonObject.optionOrNull
    get() = withScope(::Scope).option

val JsonObject.stateOrNull
    get() = withScope(::Scope).state

val JsonObject.messageOrNull
    get() = withScope(::Scope).message
