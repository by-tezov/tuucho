package com.tezov.tuucho.core.domain.business.jsonSchema.material

import com.tezov.tuucho.core.domain.business.jsonSchema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScopeArgument
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SetStringDelegate
import kotlinx.serialization.json.JsonElement

object ImageSchema {
    const val root = "image"

    object Key {
        const val id = IdSchema.root
        const val type = TypeSchema.root
        const val cacheKey = "cache-key"
        const val source = "source"
        const val tags = "tags"
        const val tagsExcluder = "tags-excluder"
        const val timeToLive = "time-to-live"
    }

    object Value {
        object Group {
            const val common = "common"
        }

        object Tag {
            const val primary = "primary"
            const val placeholder = "placeholder"
        }
    }

    class Scope(
        argument: SchemaScopeArgument
    ) : OpenScope<Scope>(argument)

    open class OpenScope<T : OpenScope<T>>(
        argument: SchemaScopeArgument
    ) : OpenSchemaScope<T>(argument) {
        override val root = ImageSchema.root

        var id by delegate<JsonElement>(Key.id)
        var type by delegate<String?>(Key.type)
        var cacheKey by delegate<String?>(Key.cacheKey)
        var source by delegate<String?>(Key.source)
        var tags by delegate<SetStringDelegate?>(Key.tags)
        var tagsExcluder by delegate<SetStringDelegate?>(Key.tagsExcluder)
    }

    fun cacheKey(
        url: String,
        id: String
    ) = "$url+$id"
}
