package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.data.repository.parser.assembler.material.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.MaterialRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseRectifier
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.module.dsl.onClose
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

@OptIn(TuuchoExperimentalAPI::class)
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

    fun invoke() = ModuleProtocol.module(ModuleGroupData.Assembler) {
        material()
        response()
    }

    private fun Module.material() {
        scope<MaterialAssembler> {
            Material.run { invoke() }
        }
        single<MaterialAssembler> {
            MaterialAssembler()
        } withOptions {
            onClose { it?.close() }
        }
        factory<Scope>(Material.Name.SCOPE) {
            get<MaterialAssembler>().scope.also {
                val materialRectifierScope = get<MaterialRectifier>().scope
                it.linkTo(materialRectifierScope)
            }
        }
    }

    private fun Module.response() {
        scope<ResponseAssembler> {
            Response.run { invoke() }
        }
        single<ResponseAssembler> {
            ResponseAssembler()
        } withOptions {
            onClose { it?.close() }
        }
        factory<Scope>(Response.Name.SCOPE) {
            get<ResponseAssembler>().scope.also {
                val responseRectifierScope = get<ResponseRectifier>().scope
                it.linkTo(responseRectifierScope)
            }
        }

    }

}
