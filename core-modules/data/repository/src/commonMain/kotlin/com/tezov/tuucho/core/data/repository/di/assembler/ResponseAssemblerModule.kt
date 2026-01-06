package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.parser.assembler.material.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.TextAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerMatcherProtocol
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormActionAssemblerMatcher
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormFailureReasonTextMatcher
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

object ResponseAssemblerModule {
    fun ScopeDSL.invokeScoped() {
        factory<Scope> { this }
        factory<List<AbstractAssembler>>(AssemblerModule.Name.ASSEMBLERS) {
            listOf(
                FormAssembler()
            )
        }
        factoryOf(::TextAssembler)
        factoryOf(::ActionAssembler)
    }

    fun Module.invoke() {
        scope<FormAssembler> {
            formModule()
        }
    }

    private fun ScopeDSL.formModule() {
        factory<List<AssemblerMatcherProtocol>>(AssemblerModule.Name.Matcher.TEXT) {
            listOf(
                FormFailureReasonTextMatcher()
            )
        }
        factory<List<AssemblerMatcherProtocol>>(AssemblerModule.Name.Matcher.ACTION) {
            listOf(
                FormActionAssemblerMatcher()
            )
        }
    }
}
