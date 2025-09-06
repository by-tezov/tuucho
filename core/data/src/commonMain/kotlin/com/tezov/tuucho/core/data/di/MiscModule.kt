package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.domain.tool.datetime.ExpirationDateTimeRectifier
import org.koin.dsl.module

object MiscModule {

    internal operator fun invoke() = module {
        factory<ExpirationDateTimeRectifier> {
            ExpirationDateTimeRectifier()
        }
    }

}


