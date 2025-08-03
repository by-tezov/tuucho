package com.tezov.tuucho.core.domain.business.model.schema.material

import com.tezov.tuucho.core.domain.business.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.model.schema._system.SchemaScopeArgument
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object OptionSchema {

    const val root =
        TypeSchema.Value.option

    object Key {
        const val id = IdSchema.root
        const val type = TypeSchema.root
        const val subset = SubsetSchema.root
    }

    class Scope(argument: SchemaScopeArgument) : OpenScope<Scope>(argument)

    open class OpenScope<T : OpenScope<T>>(argument: SchemaScopeArgument) :
        OpenSchemaScope<T>(argument) {
        final override val root = OptionSchema.root
        var self by delegate<JsonObject?>(root)

        var id by delegate<JsonElement?>(Key.id)
        var type by delegate<String?>(Key.type)
        var subset by delegate<String?>(Key.subset)

    }

}



