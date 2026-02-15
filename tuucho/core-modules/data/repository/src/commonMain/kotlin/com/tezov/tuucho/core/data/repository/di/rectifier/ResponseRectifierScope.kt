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
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL
import org.koin.plugin.module.dsl.factory

internal object ResponseRectifierScope {
    fun invoke() = scope(ScopeContext.Response) {
        factory<Scope> { this }
        rectifiers()
        idAssociation()
        textAssociation()
        actionAssociation()
    }

    private fun ScopeDSL.rectifiers() {
        factory<RectifierIdGenerator>()
        factory<IdRectifier>()
        factory<TextRectifier>()
        factory<ActionRectifier>()

        associate<ResponseRectifier.Association.Processor> {
            declaration<ActionRectifier>()
            factoryOf(::FormFailureReasonRectifier)
        }
    }

    private fun ScopeDSL.idAssociation() {
        factory<IdMatcher>() associate IdRectifier.Association.Matcher::class
    }

    private fun ScopeDSL.textAssociation() {
        declaration<IdRectifier>() associate TextRectifier.Association.Processor::class
    }

    private fun ScopeDSL.actionAssociation() {
        factory<FormActionRectifierMatcher>() associate ActionRectifier.Association.Matcher::class
        declaration<IdRectifier>() associate ActionRectifier.Association.Processor::class
    }
}
