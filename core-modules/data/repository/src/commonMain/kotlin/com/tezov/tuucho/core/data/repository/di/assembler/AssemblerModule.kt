package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.data.repository.parser.assembler.material.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.JsonObjectMerger
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named

internal object AssemblerModule {
    object Name {
        val ASSEMBLERS get() = named("AssemblerModule.Name.ASSEMBLERS")

        object Processor {
            val COMPONENT get() = named("AssemblerModule.Name.Processor.COMPONENT")
            val CONTENT get() = named("AssemblerModule.Name.Processor.CONTENT")
            val STYLE get() = named("AssemblerModule.Name.Processor.STYLE")
            val OPTION get() = named("AssemblerModule.Name.Processor.OPTION")
            val STATE get() = named("AssemblerModule.Name.Processor.STATE")
        }

        object Matcher {
            val COMPONENT = named("AssemblerModule.Name.Matcher.COMPONENT")
            val TEXT get() = named("AssemblerModule.Name.Processor.TEXT")
            val COLOR get() = named("AssemblerModule.Name.Processor.COLOR")
            val DIMENSION get() = named("AssemblerModule.Name.Processor.DIMENSION")
            val ACTION get() = named("AssemblerModule.Name.Processor.ACTION")
        }
    }

    fun invoke() = module(ModuleGroupData.Assembler) {
        factoryOf(::JsonObjectMerger)
        singleOf(::MaterialAssembler)

        response() // TODO Group Scope
    }

    private fun Module.response() {
        singleOf(::ResponseAssembler)
        scope<ResponseAssembler> {
            ResponseAssemblerModule.run { invokeScoped() }
        }
        ResponseAssemblerModule.run { invoke() }
    }
}
