package com.tezov.tuucho.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tezov.tuucho.sample.di.ApplicationModuleDeclaration
import com.tezov.tuucho.data.platform.AppScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { AppScreen(ApplicationModuleDeclaration.invoke(applicationContext)) }
//        BuildConfig
    }

}
