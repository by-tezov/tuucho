package com.tezov.tuucho.kmm.system

import android.app.Application
import com.tezov.tuucho.core.data.di.SystemCoreDataModules
import com.tezov.tuucho.core.domain.di.SystemCoreDomainModules
import com.tezov.tuucho.core.ui.di.SystemCoreUiModules
import com.tezov.tuucho.kmm.di.SystemAppModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

open class KmmApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            allowOverride(override = false)
            androidContext(this@KmmApplication)
            modules(SystemCoreDomainModules())
            modules(SystemCoreDataModules())
            modules(SystemCoreUiModules())
            modules(SystemAppModules())
        }
    }
}