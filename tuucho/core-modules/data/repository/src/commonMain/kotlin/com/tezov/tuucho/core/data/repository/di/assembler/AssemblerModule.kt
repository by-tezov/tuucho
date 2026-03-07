package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.parser.assembler.material.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.JsonObjectMerger
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.onClose

internal object AssemblerModule {
    fun invoke() = module(ModuleContextData.Assembler) {
        factoryOf(::JsonObjectMerger)
        singleOf(::MaterialAssembler) onClose { assembler ->
            assembler?.closeScope()
        }
        singleOf(::ResponseAssembler) onClose { assembler ->
            assembler?.closeScope()
        }
    }
}
