package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.model.action.StoreActionDefinition
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol.Context
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.RemoveKeyValueFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SaveKeyValueToStoreUseCase
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

internal class StoreActionMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val saveKeyValueToStore: SaveKeyValueToStoreUseCase,
    private val removeKeyValueFromStore: RemoveKeyValueFromStoreUseCase,
) : ActionMiddlewareProtocol {
    override val priority: Int
        get() = ActionMiddlewareProtocol.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute?,
        action: ActionModel,
    ) = action.command == StoreActionDefinition.KeyValue.command &&
        action.authority == StoreActionDefinition.KeyValue.authority &&
        action.query != null

    override suspend fun process(
        context: Context,
        next: MiddlewareProtocol.Next<Context>?
    ) {
        val query = context.actionModel.query ?: throw DomainException.Default("should no be possible")
        when (val target = context.actionModel.target) {
            StoreActionDefinition.KeyValue.Target.save -> saveValues(query)
            StoreActionDefinition.KeyValue.Target.remove -> removeKeys(query)
            else -> throw DomainException.Default("Unknown target $target")
        }
        next?.invoke(context)
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
        suspend fun execute(
            key: JsonElement
        ) {
            useCaseExecutor.await(
                useCase = removeKeyValueFromStore,
                input = RemoveKeyValueFromStoreUseCase.Input(
                    key = key.string.toKey(),
                )
            )
        }
        when (query) {
            is JsonArray -> {
                query.jsonArray.forEach {
                    execute(it)
                }
            }

            is JsonPrimitive -> {
                execute(query)
            }

            else -> {}
        }
    }
}
