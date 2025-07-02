package com.tezov.tuucho.core.data.parser.rectifier._element.button.content.action

import com.tezov.tuucho.core.data.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.schema.ActionSchema.Companion.actionPutObject
import com.tezov.tuucho.core.domain.schema.TypeSchema
import com.tezov.tuucho.core.domain.schema._element.ButtonSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class ContentButtonActionRectifier : Rectifier() {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(ButtonSchema.Content.Key.action)) return false
        val parent = element.find(path.parent()) as? JsonObject
        return parent.isSubsetOf(ButtonSchema.Component.Value.subset)
                && parent.isTypeOf(TypeSchema.Value.Type.content)
    }

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement
    ) = JsonObject(mutableMapOf<String, JsonElement>().apply {
        actionPutObject(element.find(path).stringOrNull, null)
    })

}