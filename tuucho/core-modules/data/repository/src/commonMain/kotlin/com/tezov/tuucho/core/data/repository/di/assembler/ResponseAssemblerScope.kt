package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.di.ModuleContextData.Assembler.ScopeContext
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.TextAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormActionAssemblerMatcher
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormFailureReasonTextAssemblerMatcher
import com.tezov.tuucho.core.domain.business._system.koin.Associate.associate
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.scope
import org.koin.core.scope.Scope
import org.koin.dsl.onClose
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.scoped

object ResponseAssemblerScope {
    fun invoke() = scope(ScopeContext.Response) {
        factory<Scope> { this }
        scoped<FormAssembler>() onClose { assembler ->
            assembler?.closeScope()
        }
        associate<ResponseAssembler.Association.Processor> {
            declaration<FormAssembler>()
        }
    }

    object Form {
        fun invoke() = scope(ScopeContext.Response.Form) {
            factory<Scope> { this }
            factory<TextAssembler>()
            factory<ActionAssembler>()

            associate<FormAssembler.Association.Processor> {
                declaration<TextAssembler>()
                declaration<ActionAssembler>()
            }

            factory<FormFailureReasonTextAssemblerMatcher>() associate TextAssembler.Association.Matcher::class
            factory<FormActionAssemblerMatcher>() associate ActionAssembler.Association.Matcher::class
        }
    }
}
