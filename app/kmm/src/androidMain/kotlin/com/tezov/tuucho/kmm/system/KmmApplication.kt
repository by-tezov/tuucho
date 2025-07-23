package com.tezov.tuucho.kmm.system

import android.app.Application
import com.tezov.tuucho.kmm.di.StartKoinModules
import org.koin.android.ext.koin.androidContext

open class KmmApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        StartKoinModules {
            //TODO replace name single instead of importing a lib just for that
            androidContext(this@KmmApplication)
        }
    }
}