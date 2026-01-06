package com.tezov.tuucho.core.data.repository.parser.assembler.response.form

import com.tezov.tuucho.core.data.repository.parser.assembler.material.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.TextAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendSchema
import org.koin.core.component.createScope
import org.koin.core.scope.Scope

class FormAssembler : AbstractAssembler() {
    override val schemaType = FormSendSchema.Value.subset

    override val scope: Scope by lazy {
        createScope<FormAssembler>().also {
            with(getKoin()) {
                val responseAssemblerScope = get<ResponseAssembler>().scope
                it.linkTo(responseAssemblerScope)
            }
        }
    }

    override val childProcessors: List<AbstractAssembler> by lazy {
        listOf(
            ActionAssembler(scope = scope),
            TextAssembler(scope = scope)
        )
    }
}
