package com.tezov.tuucho.core.data.repository.repository

import com.tezov.tuucho.core.data.repository.parser.shadower.MaterialShadower
import com.tezov.tuucho.core.data.repository.repository.source.shadower.ShadowerMaterialSourceProtocol
import com.tezov.tuucho.core.data.repository.repository.source.shadower.ShadowerMaterialSourceProtocol.Context
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol.Shadower
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class ShadowerMaterialRepository(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialCacheRepository: NavigationRepositoryProtocol.MaterialCache,
    private val materialShadower: MaterialShadower,
    private val shadowerMaterialSources: List<ShadowerMaterialSourceProtocol<Context>>,
) : Shadower,
    TuuchoKoinComponent {
    private data class Item(
        val shadower: ShadowerMaterialSourceProtocol<Context>,
        val context: Context
    )

    override suspend fun process(
        route: NavigationRoute.Url,
        types: List<String>
    ) = with(selectShadowers(route, types)) {
        processShadowers(route)
        finalizeShadowers()
    }

    private suspend fun selectShadowers(
        route: NavigationRoute.Url,
        types: List<String>
    ) = coroutineScopes.default.withContext {
        val componentObject = materialCacheRepository.getComponentObject(route.value)
        shadowerMaterialSources.mapNotNull {
            if (!types.contains(it.type)) {
                return@mapNotNull null
            }
            val setting = materialCacheRepository
                .getShadowerSettingObjectOrNull(route.value, it.type)
            if (!it.accept(
                    url = route.value,
                    setting = setting,
                    componentObject = componentObject
                )
            ) {
                return@mapNotNull null
            }
            Item(
                shadower = it,
                context = it.createContext(
                    url = route.value,
                    setting = setting,
                    componentObject = componentObject
                )
            )
        }
    }

    private suspend fun List<Item>.processShadowers(
        route: NavigationRoute.Url
    ) {
        materialShadower
            .process(
                componentObject = materialCacheRepository.getComponentObject(route.value)
            ).collect { jsonObject ->
                forEach { (shadower, context) -> shadower.process(context, jsonObject) }
            }
    }

    private fun List<Item>.finalizeShadowers() = asFlow()
        .map { item ->
            Shadower.Output(
                type = item.shadower.type,
                jsonObjects = item.shadower.finalize(item.context)
            )
        }.flowOn(coroutineScopes.default.dispatcher)
}
