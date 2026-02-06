package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.model.action.LanguageActionDefinition.Current
import com.tezov.tuucho.core.domain.business.model.action.LanguageActionDefinition.System
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SetLanguageUseCase
import com.tezov.tuucho.core.domain.tool.json.string
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import kotlinx.serialization.json.JsonObject

internal class LanguageActionMiddleware(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val setLanguage: SetLanguageUseCase,
) : ActionMiddlewareProtocol,
    TuuchoKoinComponent {

    override val priority: Int
        get() = ActionMiddlewareProtocol.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute?,
        action: ActionModel,
    ) = action.command == Current.command

    override suspend fun process(
        context: ActionMiddlewareProtocol.Context,
        next: MiddlewareProtocol.Next<ActionMiddlewareProtocol.Context>?
    ) {


        when (context.actionModel.authority) {
            Current.authority -> {
                val query = (context.actionModel.query as? JsonObject) ?: throw DomainException.Default("should no be possible")
                setCurrent(query)
            }

            System.authority -> {
                setSystem()
            }
        }


        next?.invoke(context)
    }

    private suspend fun setCurrent(
        query: JsonObject
    ) {
        useCaseExecutor.await(
            useCase = setLanguage,
            input = SetLanguageUseCase.Input(
                language = LanguageModelDomain(
                    code = query[Current.Query.code].string,
                    country = query[Current.Query.country].stringOrNull
                )
            )
        )
    }

    private suspend fun setSystem() {
        useCaseExecutor.await(
            useCase = setLanguage,
            input = SetLanguageUseCase.Input(
                language = LanguageModelDomain(
                    code = null,
                    country = null
                )
            )
        )
    }
}
