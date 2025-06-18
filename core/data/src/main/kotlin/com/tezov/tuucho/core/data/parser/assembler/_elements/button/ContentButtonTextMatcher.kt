package com.tezov.tuucho.core.data.parser.assembler._elements.button

import com.tezov.tuucho.core.data.parser._schema.ContentSchema
import com.tezov.tuucho.core.data.parser._schema._element.button.ButtonSchema
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.Matcher.Companion.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.Matcher.Companion.isTypeOf
import com.tezov.tuucho.core.data.parser._system.Matcher.Companion.lastSegmentIs
import com.tezov.tuucho.core.data.parser._system.find
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class ContentButtonTextMatcher : Matcher {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(ButtonSchema.Name.value)) return false
        val parent = element.find(path.parent()) as? JsonObject
        return parent.isSubsetOf(ButtonSchema.Default.subset)
                && parent.isTypeOf(ContentSchema.Default.type)
    }

}