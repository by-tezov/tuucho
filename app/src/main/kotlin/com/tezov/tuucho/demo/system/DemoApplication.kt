package com.tezov.tuucho.demo.system

import android.app.Application
import com.tezov.tuucho.core.data.di.SystemCoreDataModules
import com.tezov.tuucho.core.domain.di.SystemCoreDomainModules
import com.tezov.tuucho.demo.di.SystemAppModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            allowOverride(override = false)
            androidContext(this@DemoApplication)
            modules(SystemCoreDataModules())
            modules(SystemCoreDomainModules())
            modules(SystemAppModules())
        }
    }
}