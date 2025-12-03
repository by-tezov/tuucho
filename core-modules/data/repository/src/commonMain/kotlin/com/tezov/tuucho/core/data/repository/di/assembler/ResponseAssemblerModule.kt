package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.parser.assembler.material.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.TextAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.JsonObjectMerger
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormActionMatcher
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormFailureReasonTextMatcher
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import org.koin.core.qualifier.named
import org.koin.dsl.ScopeDSL

@OptIn(TuuchoExperimentalAPI::class)
object Response {
    object Name {
        val SCOPE get() = named("AssemblerModule.Response.Name.ASSEMBLERS_SCOPE")
    }

    fun ScopeDSL.invoke() {
        factory<List<AbstractAssembler>>(AssemblerModule.Name.ASSEMBLERS) {
            listOf(
                FormAssembler(scope = get(Name.SCOPE)),
            )
        }
        scoped<JsonObjectMerger> {
            JsonObjectMerger()
        }

        textModule()
        actionModule()
    }

    private fun ScopeDSL.textModule() {
        scoped<TextAssembler> { TextAssembler(scope = get(Name.SCOPE)) }

        scoped<List<MatcherAssemblerProtocol>>(AssemblerModule.Name.Matcher.TEXT) {
            listOf(
                FormFailureReasonTextMatcher()
            )
        }
    }

    private fun ScopeDSL.actionModule() {
        scoped<ActionAssembler> { ActionAssembler(scope = get(Name.SCOPE)) }

        scoped<List<MatcherAssemblerProtocol>>(AssemblerModule.Name.Matcher.ACTION) {
            listOf(
                FormActionMatcher()
            )
        }
    }
}
