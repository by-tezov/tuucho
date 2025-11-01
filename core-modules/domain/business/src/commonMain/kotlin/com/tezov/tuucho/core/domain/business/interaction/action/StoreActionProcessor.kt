package com.tezov.tuucho.core.domain.business.interaction.action

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.Action
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionProcessorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.RemoveKeyValueFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SaveKeyValueToStoreUseCase
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

internal class StoreActionProcessor(
    private val useCaseExecutor: UseCaseExecutor,
    private val saveKeyValueToStore: SaveKeyValueToStoreUseCase,
    private val removeKeyValueFromStore: RemoveKeyValueFromStoreUseCase,
) : ActionProcessorProtocol {

    override val priority: Int
        get() = ActionProcessorProtocol.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ): Boolean {
        return action.command == Action.Store.command && action.authority == Action.Store.KeyValue.authority
    }

    override suspend fun process(
        route: NavigationRoute.Url,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ) {
        action.query ?: throw DomainException.Default("query is null")
        when (val target = action.target) {
            Action.Store.KeyValue.Target.save -> saveValues(action.query)
            Action.Store.KeyValue.Target.remove -> removeKeys(action.query)
            else -> throw DomainException.Default("Unknown target $target")
        }
    }

    private suspend fun saveValues(query: JsonElement) {
        query.jsonObject.forEach {
            useCaseExecutor.invokeSuspend(
                useCase = saveKeyValueToStore,
                input = SaveKeyValueToStoreUseCase.Input(
                    key = it.key.toKey(),
                    value = it.value.string.toValue()
                )
            )
        }
    }

    private suspend fun removeKeys(query: JsonElement) {
        query.jsonArray.forEach {
            useCaseExecutor.invokeSuspend(
                useCase = removeKeyValueFromStore,
                input = RemoveKeyValueFromStoreUseCase.Input(
                    key = it.string.toKey(),
                )
            )
        }

    }

}