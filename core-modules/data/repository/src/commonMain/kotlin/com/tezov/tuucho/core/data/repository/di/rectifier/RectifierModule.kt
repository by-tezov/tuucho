package com.tezov.tuucho.core.data.repository.di.rectifier

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.MaterialRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseRectifier
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.qualifier.named

@OptIn(TuuchoExperimentalAPI::class)
internal object RectifierModule {
    object Name {
        val RECTIFIERS get() = named("RectifierModule.Material.Name.RECTIFIERS")

        object Processor {
            val COMPONENT get() = named("RectifierModule.Name.Processor.COMPONENT")
            val CONTENT get() = named("RectifierModule.Name.Processor.CONTENT")
            val STYLE get() = named("RectifierModule.Name.Processor.STYLE")
            val OPTION get() = named("RectifierModule.Name.Processor.OPTION")
            val STATE get() = named("RectifierModule.Name.Processor.STATE")
            val TEXT get() = named("RectifierModule.Name.Processor.TEXT")
            val COLOR get() = named("RectifierModule.Name.Processor.COLOR")
            val DIMENSION get() = named("RectifierModule.Name.Processor.DIMENSION")
            val ACTION get() = named("RectifierModule.Name.Processor.ACTION")
        }

        object Matcher {
            val ID get() = named("RectifierModule.Name.Matcher.ID")
            val TEXT get() = named("RectifierModule.Name.Matcher.TEXT")
            val COLOR get() = named("RectifierModule.Name.Matcher.COLOR")
            val DIMENSION get() = named("RectifierModule.Name.Matcher.DIMENSION")
            val ACTION get() = named("RectifierModule.Name.Matcher.ACTION")
            val FIELD_VALIDATOR get() = named("RectifierModule.Name.Matcher.FIELD_VALIDATOR")
        }
    }

    fun invoke() = ModuleProtocol.module(ModuleGroupData.Rectifier) {
        material()
        response()
    }

    private fun Module.material() {
        scope<MaterialRectifier> {
            Material.run { invoke() }
        }
        single<MaterialRectifier> {
            MaterialRectifier()
        }
    }

    private fun Module.response() {
        scope<ResponseRectifier> {
            Response.run { invoke() }
        }
        single<ResponseRectifier> {
            ResponseRectifier()
        }
    }
}
