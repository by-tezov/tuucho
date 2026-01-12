package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData.Assembler.ScopeContext
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.TextAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormActionAssemblerMatcher
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.response.form.FormFailureReasonTextAssemblerMatcher
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.scope
import org.koin.core.module.dsl.factoryOf
import org.koin.core.scope.Scope

object ResponseAssemblerScope {

    fun invoke() = scope(ScopeContext.Response) {
        factory<Scope> { this }
        factoryOf(::FormAssembler)

        associate<ResponseAssembler.Association.Processor> {
            declaration<FormAssembler>()
        }
    }

    object Form {
        fun invoke() = scope(ScopeContext.Response.Form) {
            factory<Scope> { this }
            factoryOf(::TextAssembler)
            factoryOf(::ActionAssembler)

            associate<FormAssembler.Association.Processor> {
                declaration<TextAssembler>()
                declaration<ActionAssembler>()
            }

            factoryOf(::FormFailureReasonTextAssemblerMatcher) associate TextAssembler.Association.Matcher::class
            factoryOf(::FormActionAssemblerMatcher) associate ActionAssembler.Association.Matcher::class
        }
    }

}
