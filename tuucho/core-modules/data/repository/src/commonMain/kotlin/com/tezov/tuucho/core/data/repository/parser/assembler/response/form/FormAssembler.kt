package com.tezov.tuucho.core.data.repository.parser.assembler.response.form

import com.tezov.tuucho.core.data.repository.di.ModuleContextData.Assembler.ScopeContext
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.domain.business._system.koin.Associate.getAllAssociated
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendSchema
import org.koin.core.scope.Scope

class FormAssembler : AbstractAssembler() {
    sealed class Association {
        object Processor : Association()
    }

    override val schemaType = FormSendSchema.Value.subset

    override val lazyScope: Lazy<Scope> = lazy {
        with(getKoin()) {
            createScope(ScopeContext.Response.Form.value, ScopeContext.Response.Form).also {
                val responseAssembler = get<ResponseAssembler>()
                it.linkTo(responseAssembler.lazyScope.value)
            }
        }
    }

    override val childProcessors: List<AbstractAssembler> by lazy {
        lazyScope.value.getAllAssociated(Association.Processor::class)
    }
}
