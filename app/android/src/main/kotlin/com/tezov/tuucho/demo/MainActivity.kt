package com.tezov.tuucho.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tezov.tuucho.demo.di.ApplicationModuleDeclaration
import com.tezov.tuucho.kmm.AppScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { AppScreen(ApplicationModuleDeclaration.invoke(applicationContext)) }
    }

}
