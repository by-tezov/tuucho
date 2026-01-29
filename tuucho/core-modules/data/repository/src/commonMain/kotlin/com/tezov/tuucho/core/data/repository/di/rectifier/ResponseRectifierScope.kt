package com.tezov.tuucho.core.data.repository.di.rectifier

import com.tezov.tuucho.core.data.repository.di.ModuleContextData.Rectifier.ScopeContext
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierIdGenerator
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.action.ActionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.text.TextRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.form.FormActionRectifierMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.form.FormFailureReasonRectifier
import com.tezov.tuucho.core.domain.business._system.koin.Associate.associate
import com.tezov.tuucho.core.domain.business._system.koin.Associate.declaration
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.scope
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

        associate<ResponseRectifier.Association.Processor> {
            declaration<ActionRectifier>()
            factoryOf(::FormFailureReasonRectifier)
        }
    }

    private fun ScopeDSL.idAssociation() {
        factoryOf(::IdMatcher) associate IdRectifier.Association.Matcher::class
    }

    private fun ScopeDSL.textAssociation() {
        declaration<IdRectifier>() associate TextRectifier.Association.Matcher::class
    }

    private fun ScopeDSL.actionAssociation() {
        associate<ActionRectifier.Association.Matcher> {
            declaration<IdRectifier>()
            factoryOf(::FormActionRectifierMatcher)
        }
    }
}
