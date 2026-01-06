package com.tezov.tuucho.core.data.repository.di.rectifier

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData.Rectifier.ScopeContext
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierIdGenerator
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.action.ActionAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.action.ActionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.text.TextAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.text.TextRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseAssociation
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.form.FormActionRectifierMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.form.FormFailureReasonRectifier
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.associate
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.declaration
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.scope
import org.koin.core.module.dsl.factoryOf
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

internal object ResponseRectifierScope {
    fun invoke() = scope(ScopeContext.Response) {
        factory<Scope> { this }
        rectifiers()
        idAssociation()
        textAssociation()
        actionAssociation()
    }

    private fun ScopeDSL.rectifiers() {
        factoryOf(::RectifierIdGenerator)
        factoryOf(::IdRectifier)
        factoryOf(::TextRectifier)
        factoryOf(::ActionRectifier)

        associate<ResponseAssociation.Rectifier> {
            declaration<ActionRectifier>()
            factoryOf(::FormFailureReasonRectifier)
        }
    }

    private fun ScopeDSL.idAssociation() {
        // matchers
        factoryOf(::IdMatcher) associate IdAssociation.Matcher::class
    }

    private fun ScopeDSL.textAssociation() {
        // matchers
        declaration<IdRectifier>() associate TextAssociation.Matcher::class
    }

    private fun ScopeDSL.actionAssociation() {
        // matchers
        associate<ActionAssociation.Matcher> {
            declaration<IdRectifier>()
            factoryOf(::FormActionRectifierMatcher)
        }
    }
}
