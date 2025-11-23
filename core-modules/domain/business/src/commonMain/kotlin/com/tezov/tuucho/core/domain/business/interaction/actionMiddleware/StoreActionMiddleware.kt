package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.action.StoreAction
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.RemoveKeyValueFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SaveKeyValueToStoreUseCase
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

internal class StoreActionMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val saveKeyValueToStore: SaveKeyValueToStoreUseCase,
    private val removeKeyValueFromStore: RemoveKeyValueFromStoreUseCase,
) : ActionMiddleware {
    override val priority: Int
        get() = ActionMiddleware.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute.Url?,
        action: ActionModelDomain,
    ): Boolean = action.command == StoreAction.command && action.authority == StoreAction.KeyValue.authority && action.query != null

    override suspend fun process(
        context: ActionMiddleware.Context,
        next: MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output?>
    ) = with(context.input) {
        val query = action.query ?: throw DomainException.Default("should no be possible")
        when (val target = action.target) {
            StoreAction.KeyValue.Target.save -> saveValues(query)
            StoreAction.KeyValue.Target.remove -> removeKeys(query)
            else -> throw DomainException.Default("Unknown target $target")
        }
        next.invoke(context)
    }

    private suspend fun saveValues(
        query: JsonElement
    ) {
        query.jsonObject.forEach {
            useCaseExecutor.await(
                useCase = saveKeyValueToStore,
                input = SaveKeyValueToStoreUseCase.Input(
                    key = it.key.toKey(),
                    value = it.value.string.toValue()
                )
            )
        }
    }

    private suspend fun removeKeys(
        query: JsonElement
    ) {
        query.jsonArray.forEach {
            useCaseExecutor.await(
                useCase = removeKeyValueFromStore,
                input = RemoveKeyValueFromStoreUseCase.Input(
                    key = it.string.toKey(),
                )
            )
        }
    }
}
