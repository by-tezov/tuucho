package com.tezov.tuucho.core.data.repository.parser.assembler.response.form

import com.tezov.tuucho.core.data.repository.parser.assembler.material.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.TextAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import org.koin.core.component.createScope
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

@OptIn(TuuchoExperimentalAPI::class)
class FormAssembler : AbstractAssembler() {
    override val schemaType = FormSendSchema.Value.subset

    override val scope: Scope by lazy {
        createScope(this).also {
            with(getKoin()) {
                val responseAssemblerScope = get<ResponseAssembler>().scope
                it.linkTo(responseAssemblerScope)
            }
        }
    }

    override val childProcessors: List<AbstractAssembler> by lazy {
        with(scope) {
            listOf(
                get<ActionAssembler>(parameters = { parametersOf(this) }),
                get<TextAssembler>(parameters = { parametersOf(this) }),
            )
        }
    }
}
