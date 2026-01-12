package com.tezov.tuucho.core.data.repository.di.assembler

import com.tezov.tuucho.core.data.repository.di.ModuleGroupData
import com.tezov.tuucho.core.data.repository.parser.assembler.material.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.JsonObjectMerger
import com.tezov.tuucho.core.data.repository.parser.assembler.response.ResponseAssembler
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf

internal object AssemblerModule {

    fun invoke() = module(ModuleGroupData.Assembler) {
        factoryOf(::JsonObjectMerger)
        singleOf(::MaterialAssembler)
        singleOf(::ResponseAssembler)
    }
}
