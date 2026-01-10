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

        object Matcher {
            val TEXT get() = named("AssemblerModule.Name.Processor.TEXT")
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
