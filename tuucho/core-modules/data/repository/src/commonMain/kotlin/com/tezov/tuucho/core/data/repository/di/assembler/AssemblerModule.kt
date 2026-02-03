package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.parser.assembler.material.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.JsonObjectMerger
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import org.koin.dsl.onClose
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single

internal object AssemblerModule {
    fun invoke() = module(ModuleContextData.Assembler) {
        factory<JsonObjectMerger>()
        single<MaterialAssembler>() onClose { assembler ->
            assembler?.closeScope()
        }
        single<ResponseAssembler>() onClose { assembler ->
            assembler?.closeScope()
        }
    }
}
