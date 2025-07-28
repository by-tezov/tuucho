package com.tezov.tuucho.kmm.system

import android.app.Application
import com.tezov.tuucho.kmm.di.StartKoinModules
import com.tezov.tuucho.kmm.system.di.MiscModuleAndroid

open class KmmApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        StartKoinModules {
            modules(MiscModuleAndroid.invoke(this@KmmApplication))
        }
    }
}