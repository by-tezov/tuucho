package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.parser.assembler.material.ActionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ColorAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ComponentAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.ContentAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.DimensionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.OptionAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.StateAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.StyleAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material.TextAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._element.layout.linear.ContentLayoutLinearItemsMatcher
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AbstractAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.JsonObjectMerger
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.MaterialRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.ResponseRectifier
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import org.koin.core.module.dsl.onClose
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

@OptIn(TuuchoExperimentalAPI::class)
internal object AssemblerModule {
    fun invoke() = module(ModuleGroupData.Assembler) {
        // Material
        scope<MaterialAssembler> {
            Material.run { invoke() }
        }
        single<MaterialAssembler> {
            MaterialAssembler()
        } withOptions {
            onClose { it?.close() }
        }
        factory<Scope>(Material.Name.ASSEMBLERS_SCOPE) {
            get<MaterialAssembler>().scope.also {
                val materialRectifierScope = get<MaterialRectifier>().scope
                it.linkTo(materialRectifierScope)
            }
        }

        // Response
        scope<ResponseAssembler> {
            Response.run { invoke() }
        }
        single<ResponseAssembler> {
            ResponseAssembler()
        } withOptions {
            onClose { it?.close() }
        }
        factory<Scope>(Response.Name.ASSEMBLERS_SCOPE) {
            get<ResponseAssembler>().scope.also {
                val responseRectifierScope = get<ResponseRectifier>().scope
                it.linkTo(responseRectifierScope)
            }
        }
    }

    object Material {
        object Name {
            val ASSEMBLERS = named("AssemblerModule.Material.Name.ASSEMBLERS")
            val ASSEMBLERS_SCOPE = named("AssemblerModule.Material.Name.ASSEMBLERS")

            object Processor {
                val COMPONENT = named("MaterialAssemblerModule.Name.Processor.COMPONENT")
                val CONTENT = named("MaterialAssemblerModule.Name.Processor.CONTENT")
                val STYLE = named("MaterialAssemblerModule.Name.Processor.STYLE")
                val OPTION = named("MaterialAssemblerModule.Name.Processor.OPTION")
                val STATE = named("MaterialAssemblerModule.Name.Processor.STATE")
            }

            object Matcher {
                val COMPONENT = named("MaterialAssemblerModule.Name.Matcher.COMPONENT")
            }
        }

        fun ScopeDSL.invoke() {
            factory<List<AbstractAssembler>>(Name.ASSEMBLERS) {
                listOf(
                    get<ComponentAssembler>(),
                    get<ContentAssembler>(),
                    get<TextAssembler>(),
                    get<StateAssembler>(),
                )
            }

            scoped<JsonObjectMerger> {
                JsonObjectMerger()
            }

            componentModule()
            contentModule()
            styleModule()
            optionModule()
            stateModule()
            finalModule()
        }

        private fun ScopeDSL.componentModule() {
            scoped<ComponentAssembler> { ComponentAssembler(scope = get(Name.ASSEMBLERS_SCOPE)) }

            scoped<List<MatcherAssemblerProtocol>>(Name.Matcher.COMPONENT) {
                listOf(
                    ContentLayoutLinearItemsMatcher()
                )
            }

            scoped<List<AbstractAssembler>>(Name.Processor.COMPONENT) {
                listOf(
                    get<ContentAssembler>(),
                    get<StyleAssembler>(),
                    get<OptionAssembler>(),
                    get<StateAssembler>(),
                )
            }
        }

        private fun ScopeDSL.contentModule() {
            scoped<ContentAssembler> { ContentAssembler(scope = get(Name.ASSEMBLERS_SCOPE)) }

            scoped<List<AbstractAssembler>>(Name.Processor.CONTENT) {
                listOf(
                    get<TextAssembler>(),
                    get<ActionAssembler>(),
                    get<ComponentAssembler>()
                )
            }
        }

        private fun ScopeDSL.styleModule() {
            scoped<StyleAssembler> { StyleAssembler(scope = get(Name.ASSEMBLERS_SCOPE)) }

            scoped<List<AbstractAssembler>>(Name.Processor.STYLE) {
                listOf(
                    get<ColorAssembler>(),
                    get<DimensionAssembler>()
                )
            }
        }

        private fun ScopeDSL.optionModule() {
            scoped<OptionAssembler> { OptionAssembler(scope = get(Name.ASSEMBLERS_SCOPE)) }

            scoped<List<AbstractAssembler>>(Name.Processor.OPTION) {
                emptyList()
            }
        }

        private fun ScopeDSL.stateModule() {
            scoped<StateAssembler> { StateAssembler(scope = get(Name.ASSEMBLERS_SCOPE)) }

            scoped<List<AbstractAssembler>>(Name.Processor.STATE) {
                listOf(
                    get<TextAssembler>(),
                )
            }
        }

        private fun ScopeDSL.finalModule() {
            scoped<TextAssembler> { TextAssembler(scope = get(Name.ASSEMBLERS_SCOPE)) }
            scoped<ColorAssembler> { ColorAssembler(scope = get(Name.ASSEMBLERS_SCOPE)) }
            scoped<DimensionAssembler> { DimensionAssembler(scope = get(Name.ASSEMBLERS_SCOPE)) }
            scoped<ActionAssembler> { ActionAssembler(scope = get(Name.ASSEMBLERS_SCOPE)) }
        }
    }

    object Response {
        object Name {
            val ASSEMBLERS = named("AssemblerModule.Response.Name.ASSEMBLERS")
            val ASSEMBLERS_SCOPE = named("AssemblerModule.Response.Name.ASSEMBLERS_SCOPE")
        }

        fun ScopeDSL.invoke() {
            factory<List<AbstractAssembler>>(Name.ASSEMBLERS) {
                listOf()
            }
            scoped<ResponseAssembler> { ResponseAssembler() }
        }
    }
}
