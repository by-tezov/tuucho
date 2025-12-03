package com.tezov.tuucho.core.data.repository.parser.assembler.response.form

import com.tezov.tuucho.core.data.repository.parser.assembler.material.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.TextAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import org.koin.core.scope.Scope

@OptIn(TuuchoExperimentalAPI::class)
class FormAssembler(
    scope: Scope
) : AbstractAssembler(scope) {
    override val schemaType = FormSendSchema.Value.subset

    override val childProcessors: List<AbstractAssembler> by lazy {
        with(scope) {
            listOf(
                get<ActionAssembler>(),
                get<TextAssembler>(),
            )
        }
    }

}
