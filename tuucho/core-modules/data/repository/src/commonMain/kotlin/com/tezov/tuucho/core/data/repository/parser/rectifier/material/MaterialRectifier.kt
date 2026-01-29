package com.tezov.tuucho.core.data.repository.parser.rectifier.material

import com.tezov.tuucho.core.data.repository.di.ModuleContextData.Rectifier.ScopeContext
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.material.component.ComponentRectifier
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.getAllAssociated
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinScopeComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject
import org.koin.core.scope.Scope

@OpenForTest
internal class MaterialRectifier : TuuchoKoinScopeComponent {
    sealed class Association {
        object Processor : Association()
    }

    override val lazyScope: Lazy<Scope> = lazy {
        getKoin().createScope(ScopeContext.Material.value, ScopeContext.Material)
    }

    private val componentRectifier: ComponentRectifier by inject()

    private val rectifiers: List<RectifierProtocol> by lazy {
        lazyScope.value.getAllAssociated(Association.Processor::class)
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun process(
        context: RectifierProtocol.Context,
        materialObject: JsonObject
    ) = materialObject
        .withScope(MaterialSchema::Scope)
        .apply {
            rootComponent?.let {
                rootComponent = componentRectifier.process(context, ROOT_PATH, it).jsonObject
            }
            rectifiers.forEach { rectifier ->
                this[rectifier.key]?.let { this[rectifier.key] = rectifier.process(context, ROOT_PATH, it).jsonArray }
            }
        }.collect()
}
