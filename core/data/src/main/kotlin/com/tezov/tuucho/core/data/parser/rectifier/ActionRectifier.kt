package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.schema.ActionSchema.Companion.actionPutObject
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.inject

class ActionRectifier : Rectifier() {

    override val matchers: List<MatcherProtocol> by inject(
        Name.Matcher.ACTION
    )

    override val childProcessors: List<Rectifier> by inject(
        Name.Processor.ACTION
    )

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement
    ) = JsonObject(mutableMapOf<String, JsonElement>().apply {
        actionPutObject(element.find(path).stringOrNull, null)
    })

}