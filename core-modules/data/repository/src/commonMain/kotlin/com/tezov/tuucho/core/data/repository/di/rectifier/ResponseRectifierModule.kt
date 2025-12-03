package com.tezov.tuucho.core.data.repository.di.rectifier

import com.tezov.tuucho.core.data.repository.di.rectifier.RectifierModule.Name.Matcher
import com.tezov.tuucho.core.data.repository.di.rectifier.RectifierModule.Name.Processor
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierIdGenerator
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.action.ActionRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.id.IdRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.text.TextRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.form.FormActionMatcher
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.form.FormFailureReasonRectifier
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import org.koin.core.qualifier.named
import org.koin.dsl.ScopeDSL

@OptIn(TuuchoExperimentalAPI::class)
internal object Response {
    object Name {
        val SCOPE get() = named("RectifierModule.Response.Name.SCOPE")
    }

    fun ScopeDSL.invoke() {
        factory<List<AbstractRectifier>>(RectifierModule.Name.RECTIFIERS) {
            listOf(
                get<ActionRectifier>(),
                FormFailureReasonRectifier(scope = get(Name.SCOPE))
            )
        }
        idModule()
        textModule()
        actionModule()
    }

    private fun ScopeDSL.idModule() {
        scoped<IdRectifier> {
            IdRectifier(
                scope = get(Name.SCOPE),
                idGenerator = RectifierIdGenerator(
                    idGenerator = get()
                )
            )
        }

        scoped<List<MatcherRectifierProtocol>>(Matcher.ID) {
            listOf(IdMatcher())
        }
    }

    private fun ScopeDSL.textModule() {
        scoped<TextRectifier> {
            TextRectifier(scope = get(Name.SCOPE))
        }

        scoped<List<MatcherRectifierProtocol>>(Matcher.TEXT) {
            emptyList()
        }

        scoped<List<AbstractRectifier>>(Processor.TEXT) {
            listOf(get<IdRectifier>())
        }
    }

    private fun ScopeDSL.actionModule() {
        scoped<ActionRectifier> {
            ActionRectifier(scope = get(Name.SCOPE))
        }

        scoped<List<MatcherRectifierProtocol>>(Matcher.ACTION) {
            listOf(
                FormActionMatcher()
            )
        }

        scoped<List<AbstractRectifier>>(Processor.ACTION) {
            listOf(get<IdRectifier>())
        }
    }
}
