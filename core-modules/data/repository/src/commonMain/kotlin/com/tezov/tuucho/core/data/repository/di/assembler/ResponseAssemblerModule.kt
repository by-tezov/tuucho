package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.parser.assembler.material.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.TextAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormActionMatcher
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormFailureReasonTextMatcher
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.ScopeDSL

@OptIn(TuuchoExperimentalAPI::class)
object Response {
    fun ScopeDSL.invokeScoped() {
        factory<List<AbstractAssembler>>(AssemblerModule.Name.ASSEMBLERS) {
            listOf(
                FormAssembler()
            )
        }
        factory<TextAssembler> { parameters -> TextAssembler(scope = parameters.get()) }
        factory<ActionAssembler> { parameters -> ActionAssembler(scope = parameters.get()) }
    }

    fun Module.invoke() {
        scope<FormAssembler> {
            formModule()
        }
    }

    private fun ScopeDSL.formModule() {
        factory<List<MatcherAssemblerProtocol>>(AssemblerModule.Name.Matcher.TEXT) {
            listOf(
                FormFailureReasonTextMatcher()
            )
        }
        factory<List<MatcherAssemblerProtocol>>(AssemblerModule.Name.Matcher.ACTION) {
            listOf(
                FormActionMatcher()
            )
        }
    }
}
